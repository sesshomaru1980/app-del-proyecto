package com.example.neobarber.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.neobarber.core.network.RetrofitClient
import com.example.neobarber.core.session.SessionManager
import com.example.neobarber.data.model.BarberDto
import com.example.neobarber.data.model.BookAppointmentRequest
import com.example.neobarber.data.model.ServiceDto
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun ClientBookingScreen(
    onBack: () -> Unit
) {
    // Contexto actual para acceder a la sesión del usuario.
    val context = LocalContext.current

    // Administrador de sesión para recuperar el token JWT.
    val sessionManager = remember { SessionManager(context) }

    // Scope para ejecutar corrutinas.
    val scope = rememberCoroutineScope()

    // Listas reactivas de servicios y barberos.
    val services = remember { mutableStateListOf<ServiceDto>() }
    val barbers = remember { mutableStateListOf<BarberDto>() }

    // Estados de selección del usuario.
    var selectedServiceId by remember { mutableStateOf<String?>(null) }
    var selectedBarberId by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    // Estados de interfaz.
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    // Mensaje de éxito al reservar la cita.
    var successMessage by remember { mutableStateOf("") }

    // Colores de la pantalla.
    val topColor = Color(0xFF2A1520)
    val bottomColor = Color(0xFF1B2740)
    val cardNormal = Color.White.copy(alpha = 0.10f)
    val cardSelected = Color(0xFF114BFF)
    val textDark = Color.White

    /**
     * Obtiene el token con formato Bearer para consumir el backend.
     */
    fun getBearerToken(): String? {
        val token = sessionManager.getToken()
        return if (token.isNullOrBlank()) null else "Bearer $token"
    }

    /**
     * Carga la lista de servicios.
     */
    fun loadServices() {
        val bearerToken = getBearerToken() ?: run {
            message = "No hay sesión activa."
            return
        }

        scope.launch {
            try {
                val result = RetrofitClient.api.getServices(bearerToken)
                services.clear()
                services.addAll(result)
            } catch (e: Exception) {
                message = e.message ?: "Error cargando servicios"
            }
        }
    }

    /**
     * Carga la lista de barberos.
     */
    fun loadBarbers() {
        val bearerToken = getBearerToken() ?: run {
            message = "No hay sesión activa."
            return
        }

        scope.launch {
            try {
                val result = RetrofitClient.api.getBarbers(bearerToken)
                barbers.clear()
                barbers.addAll(result)
            } catch (e: Exception) {
                message = e.message ?: "Error cargando barberos"
            }
        }
    }

    /**
     * Reserva una cita con los datos seleccionados por el usuario.
     * El backend espera:
     * - serviceId
     * - barberId (ID del usuario barbero)
     * - startAt
     */
    fun bookAppointment() {
        val bearerToken = getBearerToken()

        if (bearerToken == null) {
            message = "No hay sesión activa."
            successMessage = ""
            return
        }

        if (selectedServiceId.isNullOrBlank()) {
            message = "Selecciona un servicio."
            successMessage = ""
            return
        }

        if (selectedBarberId.isNullOrBlank()) {
            message = "Selecciona un barbero."
            successMessage = ""
            return
        }

        if (selectedDate.isBlank()) {
            message = "Ingresa la fecha de la cita."
            successMessage = ""
            return
        }

        if (selectedTime.isBlank()) {
            message = "Ingresa la hora de la cita."
            successMessage = ""
            return
        }

        // Se construye la fecha/hora como string ISO básico esperado por el backend.
        val startAt = "${selectedDate}T${selectedTime}:00"

        scope.launch {
            try {
                isLoading = true
                message = ""
                successMessage = ""

                val request = BookAppointmentRequest(
                    serviceId = selectedServiceId!!,
                    barberId = selectedBarberId!!,
                    startAt = startAt
                )

                val response = RetrofitClient.api.createAppointment(
                    token = bearerToken,
                    request = request
                )

                // Mensaje de éxito visible para el usuario.
                successMessage = response.message ?: "Cita reservada correctamente"
                message = ""

                // Se limpia el formulario para una nueva reserva.
                selectedServiceId = null
                selectedBarberId = null
                selectedDate = ""
                selectedTime = ""

            } catch (e: HttpException) {
                message = e.response()?.errorBody()?.string() ?: "Error HTTP ${e.code()}"
                successMessage = ""
            } catch (e: Exception) {
                message = e.message ?: "Error al reservar cita"
                successMessage = ""
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Al entrar a la pantalla se cargan servicios y barberos.
     */
    LaunchedEffect(Unit) {
        loadServices()
        loadBarbers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(topColor, bottomColor))
            )
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Cabecera de la pantalla.
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Reservar cita",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Mensaje de error.
        if (message.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFC62828)
                )
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Mensaje de éxito.
        if (successMessage.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text(
                    text = successMessage,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // =========================
            // SERVICIOS
            // =========================
            item {
                Text(
                    text = "Servicios",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(services) { service ->
                val serviceId = service.id
                val isSelected = selectedServiceId == serviceId

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = serviceId != null) {
                            selectedServiceId = serviceId
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) cardSelected else cardNormal
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {

                        if (!service.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = service.imageUrl,
                                contentDescription = service.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(14.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = "Servicio",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = service.name ?: "Servicio",
                                color = textDark,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = service.description ?: "Sin descripción",
                            color = textDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Duración: ${service.durationMinutes ?: 0} min",
                            color = textDark
                        )

                        Text(
                            text = "Precio: ${service.price ?: 0.0}",
                            color = textDark
                        )
                    }
                }
            }

            // =========================
            // BARBEROS
            // =========================
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Barberos",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(barbers) { barber ->
                // El backend espera el ID del usuario barbero, no el ID del perfil.
                val barberId = barber.user?.id
                val isSelected = selectedBarberId == barberId

                val barberName = barber.user?.fullName ?: "Barbero"
                val barberEmail = barber.user?.email ?: ""
                val barberBio = barber.bio ?: ""
                val barberImage = barber.imageUrl

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = barberId != null) {
                            selectedBarberId = barberId
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) cardSelected else cardNormal
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {

                        if (!barberImage.isNullOrBlank()) {
                            AsyncImage(
                                model = barberImage,
                                contentDescription = barberName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(14.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Barbero",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = barberName,
                                color = textDark,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (barberEmail.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = barberEmail,
                                color = textDark
                            )
                        }

                        if (barberBio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = barberBio,
                                color = textDark
                            )
                        }
                    }
                }
            }

            // =========================
            // FECHA Y HORA
            // =========================
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fecha y hora",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardNormal)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Fecha",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Fecha (AAAA-MM-DD)",
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        androidx.compose.material3.OutlinedTextField(
                            value = selectedDate,
                            onValueChange = { selectedDate = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            placeholder = { Text("Ej: 2026-03-30") }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Hora (HH:MM)",
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        androidx.compose.material3.OutlinedTextField(
                            value = selectedTime,
                            onValueChange = { selectedTime = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            placeholder = { Text("Ej: 10:30") }
                        )
                    }
                }
            }

            // =========================
            // BOTÓN RESERVAR
            // =========================
            item {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { bookAppointment() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF114BFF),
                        contentColor = Color.White
                    )
                ) {
                    Text("Reservar cita")
                }
            }
        }
    }
}