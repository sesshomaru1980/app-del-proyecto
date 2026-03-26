package com.example.neobarber.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.neobarber.core.network.RetrofitClient
import com.example.neobarber.core.session.SessionManager
import com.example.neobarber.data.model.AppointmentDto
import com.example.neobarber.data.model.AppointmentStatusRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * Pantalla de citas del barbero.
 * Consume el endpoint real:
 * GET /api/appointments/barber
 */
@Composable
fun AppointmentsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // Lista reactiva de citas del barbero.
    val appointments = remember { mutableStateListOf<AppointmentDto>() }

    // Estados de interfaz.
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val topColor = Color(0xFF2A1520)
    val bottomColor = Color(0xFF1B2740)
    val primaryBlue = Color(0xFF114BFF)

    /**
     * Devuelve el token con el prefijo Bearer.
     */
    fun getBearerToken(): String? {
        val token = sessionManager.getToken()
        return if (token.isNullOrBlank()) null else "Bearer $token"
    }

    /**
     * Carga las citas del barbero autenticado.
     */
    fun loadAppointments() {
        val bearerToken = getBearerToken()

        if (bearerToken == null) {
            message = "No hay sesión activa."
            return
        }

        scope.launch {
            try {
                isLoading = true
                message = ""

                val response = RetrofitClient.api.getBarberAppointments(bearerToken)

                appointments.clear()
                appointments.addAll(response.data)

            } catch (e: HttpException) {
                message = when (e.code()) {
                    401 -> "Sesión expirada o no autorizada."
                    403 -> "No tienes permisos para ver estas citas."
                    else -> "Error HTTP ${e.code()}"
                }
            } catch (e: Exception) {
                message = e.message ?: "Error al cargar las citas."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Actualiza el estado de la cita.
     * Según el backend, el barbero puede confirmar o completar.
     */
    fun updateStatus(appointmentId: String, newStatus: String) {
        val bearerToken = getBearerToken()

        if (bearerToken == null) {
            message = "No hay sesión activa."
            return
        }

        scope.launch {
            try {
                isLoading = true
                message = ""

                val response = RetrofitClient.api.updateAppointmentStatus(
                    token = bearerToken,
                    id = appointmentId,
                    request = AppointmentStatusRequest(status = newStatus)
                )

                if (response.isSuccessful) {
                    message = response.body()?.message ?: "Estado actualizado correctamente."
                    loadAppointments()
                } else {
                    message = "No fue posible actualizar el estado."
                }
            } catch (e: HttpException) {
                message = when (e.code()) {
                    400 -> "Solicitud inválida."
                    401 -> "Sesión expirada o no autorizada."
                    403 -> "No tienes permisos para modificar esta cita."
                    404 -> "La cita no fue encontrada."
                    else -> "Error HTTP ${e.code()}"
                }
            } catch (e: Exception) {
                message = e.message ?: "Error al actualizar la cita."
            } finally {
                isLoading = false
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
        // Cabecera.
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
                text = "Mis citas",
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

        if (message.isNotBlank()) {
            Text(
                text = message,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(appointments) { appointment ->
                val appointmentId = appointment.id.orEmpty()
                val clientName = appointment.client?.name ?: "Cliente"
                val clientEmail = appointment.client?.email ?: ""
                val serviceName = appointment.service?.name ?: "Servicio"
                val serviceDuration = appointment.service?.duration ?: 0
                val servicePrice = appointment.service?.price ?: 0.0
                val date = appointment.startAt ?: ""
                val status = appointment.status ?: "Pendiente"
                val notes = appointment.notes ?: ""

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
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Fecha de la cita",
                                tint = Color.White
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = date,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Datos del cliente.
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

                        // Datos del servicio.
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

                        Text(
                            text = "Estado: $status",
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Acciones disponibles para el barbero.
                        if (status == "Pendiente") {
                            Button(
                                onClick = {
                                    if (appointmentId.isNotBlank()) {
                                        updateStatus(appointmentId, "Confirmada")
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Confirmar cita")
                            }
                        }

                        if (status == "Confirmada") {
                            Button(
                                onClick = {
                                    if (appointmentId.isNotBlank()) {
                                        updateStatus(appointmentId, "Completada")
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Marcar como completada")
                            }
                        }
                    }
                }
            }
        }
    }
}