package com.example.neobarber.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.neobarber.core.network.BarberActiveRequest
import com.example.neobarber.core.network.RetrofitClient
import com.example.neobarber.core.session.SessionManager
import com.example.neobarber.data.model.BarberCreateRequest
import com.example.neobarber.data.model.BarberDto
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun AdminBarbersScreen(
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // Lista reactiva de barberos
    val barbers = remember { mutableStateListOf<BarberDto>() }

    // Estados del formulario de creación
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // Estado de UI
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showOnlyActive by remember { mutableStateOf(false) }

    // Colores del módulo
    val topColor = Color(0xFF2A1520)
    val bottomColor = Color(0xFF1B2740)
    val primaryBlue = Color(0xFF114BFF)
    val cardColor = Color.White.copy(alpha = 0.12f)

    /**
     * Limpia el formulario luego de crear.
     */
    fun clearForm() {
        fullName = ""
        email = ""
        password = ""
        bio = ""
        imageUrl = ""
    }

    /**
     * Carga la lista de barberos desde el backend.
     */
    fun loadBarbers() {
        val token = session.getToken()

        if (token.isNullOrBlank()) {
            message = "No hay sesión activa. Vuelve a iniciar sesión como admin."
            return
        }

        scope.launch {
            try {
                isLoading = true
                message = null

                val result = RetrofitClient.api.getBarbers("Bearer $token")

                barbers.clear()
                barbers.addAll(result)

                isLoading = false
            } catch (e: HttpException) {
                isLoading = false
                message = when (e.code()) {
                    401 -> "Sesión inválida o expirada. Vuelve a iniciar sesión."
                    403 -> "No tienes permisos para ver barberos."
                    else -> e.response()?.errorBody()?.string() ?: "Error HTTP ${e.code()}"
                }
            } catch (e: Exception) {
                isLoading = false
                message = e.message ?: "Error cargando barberos"
            }
        }
    }

    /**
     * Activa o desactiva un barbero.
     * OJO: el backend espera el userId del usuario barbero, no el id del perfil.
     */
    fun toggleBarber(barber: BarberDto, newValue: Boolean) {
        val token = session.getToken()
        val userId = barber.user?.id

        if (token.isNullOrBlank()) {
            message = "No hay sesión activa."
            return
        }

        if (userId.isNullOrBlank()) {
            message = "No se encontró el userId del barbero."
            return
        }

        scope.launch {
            try {
                isLoading = true
                message = null

                val response = RetrofitClient.api.setBarberActive(
                    token = "Bearer $token",
                    userId = userId,
                    request = BarberActiveRequest(isActive = newValue)
                )

                isLoading = false
                message = response.body()?.message ?: "Estado actualizado correctamente"

                // Recargamos la lista para reflejar el cambio
                loadBarbers()

            } catch (e: HttpException) {
                isLoading = false
                message = when (e.code()) {
                    401 -> "Sesión inválida o expirada."
                    403 -> "No tienes permisos para cambiar el estado."
                    else -> e.response()?.errorBody()?.string() ?: "Error HTTP ${e.code()}"
                }
            } catch (e: Exception) {
                isLoading = false
                message = e.message ?: "Error actualizando estado"
            }
        }
    }

    LaunchedEffect(Unit) {
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
        // =========================
        // CABECERA
        // =========================
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Barberos",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // =========================
        // FORMULARIO CREAR BARBERO
        // =========================
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Crear barbero",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = barberTextFieldColors(primaryBlue)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = barberTextFieldColors(primaryBlue)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = barberTextFieldColors(primaryBlue)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Biografía", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    minLines = 3,
                    maxLines = 5,
                    colors = barberTextFieldColors(primaryBlue)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de imagen", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = barberTextFieldColors(primaryBlue)
                )

                // Vista previa de la foto
                if (imageUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Vista previa del barbero",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.White, RoundedCornerShape(14.dp))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val token = session.getToken()

                        if (token.isNullOrBlank()) {
                            message = "No hay sesión activa."
                            return@Button
                        }

                        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                            message = "Completa nombre, email y password"
                            return@Button
                        }

                        scope.launch {
                            try {
                                isLoading = true
                                message = null

                                RetrofitClient.api.createBarber(
                                    token = "Bearer $token",
                                    request = BarberCreateRequest(
                                        fullName = fullName,
                                        email = email,
                                        password = password,
                                        bio = bio,
                                        imageUrl = imageUrl
                                    )
                                )

                                isLoading = false
                                message = "Barbero creado correctamente"

                                clearForm()
                                loadBarbers()

                            } catch (e: HttpException) {
                                isLoading = false
                                message = when (e.code()) {
                                    401 -> "Sesión inválida o expirada."
                                    403 -> "No tienes permisos para crear barberos."
                                    else -> e.response()?.errorBody()?.string() ?: "Error HTTP ${e.code()}"
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                message = e.message ?: "Error creando barbero"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(primaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = "Guardar")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Crear")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // =========================
        // FILTRO
        // =========================
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Lista de barberos",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.width(12.dp))

            FilterChip(
                selected = showOnlyActive,
                onClick = { showOnlyActive = !showOnlyActive },
                label = {
                    Text(if (showOnlyActive) "Solo activos" else "Todos")
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mensaje del sistema
        if (!message.isNullOrBlank()) {
            Text(
                text = message ?: "",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // =========================
        // LISTA DE BARBEROS
        // =========================
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            val filteredBarbers = if (showOnlyActive) {
                barbers.filter { it.user?.isActive == true }
            } else {
                barbers
            }

            items(filteredBarbers) { barber ->
                val name = barber.user?.fullName ?: "Sin nombre"
                val emailText = barber.user?.email ?: ""
                val active = barber.user?.isActive ?: false
                val barberBio = barber.bio?.takeIf { it.isNotBlank() } ?: "Sin biografía"
                val barberImage = barber.imageUrl?.takeIf { it.isNotBlank() }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {

                        // Imagen del barbero
                        if (barberImage != null) {
                            AsyncImage(
                                model = barberImage,
                                contentDescription = name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(14.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Sin foto",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sin foto de referencia",
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Text(
                            text = name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = emailText,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = barberBio,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Estado y switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContentCut,
                                    contentDescription = "Estado",
                                    tint = if (active) Color.Green else Color.Red
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (active) "Activo" else "Inactivo",
                                    color = if (active) Color.Green else Color.Red
                                )
                            }

                            Switch(
                                checked = active,
                                onCheckedChange = { newValue ->
                                    toggleBarber(barber, newValue)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = primaryBlue
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Colores reutilizables para campos del formulario.
 */
@Composable
fun barberTextFieldColors(primary: Color) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedBorderColor = primary,
    unfocusedBorderColor = Color.LightGray,
    focusedLabelColor = primary,
    unfocusedLabelColor = Color.DarkGray,
    cursorColor = primary
)