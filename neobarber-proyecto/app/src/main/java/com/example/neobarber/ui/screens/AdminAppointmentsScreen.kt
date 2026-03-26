package com.example.neobarber.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.neobarber.core.network.RetrofitClient
import com.example.neobarber.core.session.SessionManager
import com.example.neobarber.data.model.AppointmentDto
import com.example.neobarber.data.model.AppointmentStatusRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun AdminAppointmentsScreen(
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // Lista reactiva de citas
    val appointments = remember { mutableStateListOf<AppointmentDto>() }

    // Estados de UI
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val topColor = Color(0xFF2A1520)
    val bottomColor = Color(0xFF1B2740)
    val primaryBlue = Color(0xFF114BFF)

    /**
     * Carga las citas del backend.
     * El backend devuelve un objeto con success + data.
     */
    fun loadAppointments() {
        val token = session.getToken()

        if (token.isNullOrBlank()) {
            message = "No hay sesión activa. Vuelve a iniciar sesión como admin."
            return
        }

        scope.launch {
            try {
                isLoading = true
                message = null

                val result = RetrofitClient.api.getAppointments("Bearer $token")

                appointments.clear()
                appointments.addAll(result.data)

                isLoading = false
            } catch (e: HttpException) {
                isLoading = false
                message = when (e.code()) {
                    401 -> "Sesión inválida o expirada."
                    403 -> "No tienes permisos para ver las citas."
                    else -> e.response()?.errorBody()?.string() ?: "Error HTTP ${e.code()}"
                }
            } catch (e: Exception) {
                isLoading = false
                message = e.message ?: "Error cargando citas"
            }
        }
    }

    /**
     * Actualiza el estado de una cita.
     * Estados válidos del backend:
     * Pendiente, Confirmada, Cancelada, Completada
     */
    fun updateStatus(appointmentId: String, newStatus: String) {
        val token = session.getToken()

        if (token.isNullOrBlank()) {
            message = "No hay sesión activa."
            return
        }

        scope.launch {
            try {
                isLoading = true
                message = null

                val response = RetrofitClient.api.updateAppointmentStatus(
                    token = "Bearer $token",
                    id = appointmentId,
                    request = AppointmentStatusRequest(status = newStatus)
                )

                isLoading = false
                message = response.body()?.message ?: "Estado actualizado"

                // Refrescar lista para ver el cambio
                loadAppointments()

            } catch (e: HttpException) {
                isLoading = false
                message = when (e.code()) {
                    401 -> "Sesión inválida o expirada."
                    403 -> "No tienes permisos para actualizar citas."
                    else -> e.response()?.errorBody()?.string() ?: "Error HTTP ${e.code()}"
                }
            } catch (e: Exception) {
                isLoading = false
                message = e.message ?: "Error actualizando cita"
            }
        }
    }

    /**
     * Cancela una cita usando la ruta dedicada del backend.
     */
    fun cancelAppointment(appointmentId: String) {
        val token = session.getToken()

        if (token.isNullOrBlank()) {
            message = "No hay sesión activa."
            return
        }

        scope.launch {
            try {
                isLoading = true
                message = null

                val response = RetrofitClient.api.cancelAppointment(
                    token = "Bearer $token",
                    id = appointmentId
                )

                isLoading = false
                message = response.body()?.message ?: "Cita cancelada"

                // Refrescar lista
                loadAppointments()

            } catch (e: HttpException) {
                isLoading = false
                message = when (e.code()) {
                    401 -> "Sesión inválida o expirada."
                    403 -> "No tienes permisos para cancelar citas."
                    else -> e.response()?.errorBody()?.string() ?: "Error HTTP ${e.code()}"
                }
            } catch (e: Exception) {
                isLoading = false
                message = e.message ?: "Error cancelando cita"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAppointments()
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
                text = "Citas",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cargando
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Mensaje del sistema
        if (!message.isNullOrBlank()) {
            Text(
                text = message ?: "",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // =========================
        // LISTA DE CITAS
        // =========================
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(appointments) { appointment ->

                val appointmentId = appointment.id.orEmpty()

                val clientName = appointment.client?.name ?: "Cliente"
                val clientEmail = appointment.client?.email ?: ""

                val barberName = appointment.barber?.name ?: "Barbero"
                val barberEmail = appointment.barber?.email ?: ""

                val serviceName = appointment.service?.name ?: "Servicio"
                val servicePrice = appointment.service?.price ?: 0.0
                val serviceDuration = appointment.service?.duration ?: 0

                val date = appointment.startAt ?: ""
                val status = appointment.status ?: "Pendiente"
                val notes = appointment.notes ?: ""

                // Color visual según estado
                val statusColor = when (status) {
                    "Pendiente" -> Color.Yellow
                    "Confirmada" -> Color.Cyan
                    "Completada" -> Color.Green
                    "Cancelada" -> Color.Red
                    else -> Color.White
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {

                        // Fecha
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = date,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Cliente
                        Text(
                            text = "Cliente: $clientName",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        if (clientEmail.isNotBlank()) {
                            Text(
                                text = clientEmail,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Barbero
                        Text(
                            text = "Barbero: $barberName",
                            color = Color.White
                        )
                        if (barberEmail.isNotBlank()) {
                            Text(
                                text = barberEmail,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Servicio
                        Text(
                            text = "Servicio: $serviceName",
                            color = Color.White
                        )
                        Text(
                            text = "Duración: $serviceDuration min",
                            color = Color.White
                        )
                        Text(
                            text = "Precio: $servicePrice",
                            color = Color.White
                        )

                        if (notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Notas: $notes",
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Estado actual
                        Text(
                            text = "Estado: $status",
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // =========================
                        // ACCIONES ADMIN
                        // =========================

                        // Si está pendiente, puede confirmarse o cancelarse
                        if (status == "Pendiente") {
                            Row {
                                Button(
                                    onClick = {
                                        if (appointmentId.isNotBlank()) {
                                            updateStatus(appointmentId, "Confirmada")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryBlue,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Confirmar")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (appointmentId.isNotBlank()) {
                                            cancelAppointment(appointmentId)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC62828),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        }

                        // Si está confirmada, puede completarse o cancelarse
                        if (status == "Confirmada") {
                            Row {
                                Button(
                                    onClick = {
                                        if (appointmentId.isNotBlank()) {
                                            updateStatus(appointmentId, "Completada")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2E7D32),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Completar")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (appointmentId.isNotBlank()) {
                                            cancelAppointment(appointmentId)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC62828),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        }

                        // Si ya está cancelada o completada, no mostramos botones
                    }
                }
            }
        }
    }
}