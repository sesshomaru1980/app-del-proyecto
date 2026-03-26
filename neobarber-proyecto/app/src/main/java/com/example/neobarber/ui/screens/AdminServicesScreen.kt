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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.neobarber.core.network.RetrofitClient
import com.example.neobarber.core.session.SessionManager
import com.example.neobarber.data.model.ServiceCreateRequest
import com.example.neobarber.data.model.ServiceDto
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun AdminServicesScreen(
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // Lista reactiva de servicios
    val services = remember { mutableStateListOf<ServiceDto>() }

    // Formulario
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // ID del servicio que estamos editando
    var editingServiceId by remember { mutableStateOf<String?>(null) }

    // Estado UI
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    // Colores
    val topColor = Color(0xFF2A1520)
    val bottomColor = Color(0xFF1B2740)
    val primaryBlue = Color(0xFF114BFF)
    val cardColor = Color.White.copy(alpha = 0.12f)

    /**
     * Limpia el formulario.
     */
    fun clearForm() {
        name = ""
        description = ""
        durationMinutes = ""
        price = ""
        imageUrl = ""
        editingServiceId = null
    }

    /**
     * Carga los servicios del backend.
     */
    fun loadServices() {
        val token = session.getToken() ?: return

        scope.launch {
            try {
                isLoading = true
                message = null

                val result = RetrofitClient.api.getServices("Bearer $token")
                services.clear()
                services.addAll(result)

                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                message = e.message ?: "Error cargando servicios"
            }
        }
    }

    /**
     * Pasa un servicio al formulario para editar.
     */
    fun startEditing(service: ServiceDto) {
        editingServiceId = service.id
        name = service.name ?: ""
        description = service.description ?: ""
        durationMinutes = (service.durationMinutes ?: 0).toString()
        price = (service.price ?: 0.0).toString()
        imageUrl = service.imageUrl ?: ""
    }

    LaunchedEffect(Unit) {
        loadServices()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(topColor, bottomColor)
                )
            )
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // =========================
        // CABECERA
        // =========================
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
                text = "Servicios",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // =========================
        // FORMULARIO CREAR / EDITAR
        // =========================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (editingServiceId == null) "Crear servicio" else "Editar servicio",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = serviceTextFieldColors(primaryBlue)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    minLines = 3,
                    maxLines = 5,
                    colors = serviceTextFieldColors(primaryBlue)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = durationMinutes,
                    onValueChange = { durationMinutes = it },
                    label = { Text("Duración en minutos", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = serviceTextFieldColors(primaryBlue)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = serviceTextFieldColors(primaryBlue)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de imagen", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = serviceTextFieldColors(primaryBlue)
                )

                if (imageUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Vista previa",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(Color.White, RoundedCornerShape(14.dp))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Botón guardar / actualizar
                Button(
                    onClick = {
                        val token = session.getToken() ?: return@Button

                        if (name.isBlank() || durationMinutes.isBlank() || price.isBlank()) {
                            message = "Completa nombre, duración y precio"
                            return@Button
                        }

                        scope.launch {
                            try {
                                isLoading = true
                                message = null

                                val request = ServiceCreateRequest(
                                    name = name,
                                    description = description,
                                    durationMinutes = durationMinutes.toInt(),
                                    price = price.toDouble(),
                                    imageUrl = imageUrl
                                )

                                if (editingServiceId == null) {
                                    // Crear
                                    RetrofitClient.api.createService(
                                        token = "Bearer $token",
                                        request = request
                                    )
                                    message = "Servicio creado correctamente"
                                } else {
                                    // Editar
                                    RetrofitClient.api.updateService(
                                        token = "Bearer $token",
                                        id = editingServiceId!!,
                                        request = request
                                    )
                                    message = "Servicio actualizado correctamente"
                                }

                                isLoading = false
                                clearForm()
                                loadServices()

                            } catch (e: HttpException) {
                                isLoading = false
                                message = e.response()?.errorBody()?.string()
                                    ?: "Error HTTP ${e.code()}"
                            } catch (e: Exception) {
                                isLoading = false
                                message = e.message ?: "Error guardando servicio"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Guardar"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (editingServiceId == null) "Guardar servicio" else "Actualizar servicio")
                    }
                }

                // Botón cancelar edición
                if (editingServiceId != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            clearForm()
                            message = "Edición cancelada"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancelar"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancelar edición")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Servicios registrados",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!message.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.14f)
                )
            ) {
                Text(
                    text = message ?: "",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // =========================
        // LISTA DE SERVICIOS
        // =========================
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(services) { service ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        if (!service.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = service.imageUrl,
                                contentDescription = service.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(14.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Sin imagen",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sin imagen de referencia",
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Text(
                            text = service.name ?: "Servicio",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = service.description ?: "Sin descripción",
                            color = Color.White.copy(alpha = 0.88f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Duración: ${service.durationMinutes ?: 0} min",
                            color = Color.White
                        )

                        Text(
                            text = "Precio: ${service.price ?: 0.0}",
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Botón editar
                            Button(
                                onClick = {
                                    startEditing(service)
                                    message = "Editando servicio"
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Editar")
                            }

                            // Botón eliminar
                            Button(
                                onClick = {
                                    val token = session.getToken() ?: return@Button
                                    val serviceId = service.id ?: return@Button

                                    scope.launch {
                                        try {
                                            isLoading = true
                                            message = null

                                            RetrofitClient.api.deleteService(
                                                token = "Bearer $token",
                                                id = serviceId
                                            )

                                            isLoading = false
                                            message = "Servicio eliminado correctamente"

                                            // Si estaba en edición y lo borraron, limpiamos formulario
                                            if (editingServiceId == serviceId) {
                                                clearForm()
                                            }

                                            loadServices()

                                        } catch (e: HttpException) {
                                            isLoading = false
                                            message = e.response()?.errorBody()?.string()
                                                ?: "Error HTTP ${e.code()}"
                                        } catch (e: Exception) {
                                            isLoading = false
                                            message = e.message ?: "Error eliminando servicio"
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFC62828),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Colores reutilizables para los campos de servicios.
 */
@Composable
fun serviceTextFieldColors(primary: Color) = OutlinedTextFieldDefaults.colors(
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