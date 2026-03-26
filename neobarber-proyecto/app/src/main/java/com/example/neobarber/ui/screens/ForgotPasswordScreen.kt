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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Pantalla visual de recuperación de contraseña.
 * No envía correo todavía. Solo es simulación para la actividad.
 */
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val green = Color(0xFF69C51D)
    val bg = Color(0xFFF1F1F1)
    val darkButton = Color(0xFF3E3E3E)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿Olvidaste tu contraseña?",
            color = green,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Introduce tu nombre de usuario, dirección de email o número de móvil para restablecer la contraseña",
            color = Color.DarkGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF9DDC75),
                unfocusedBorderColor = Color(0xFF9DDC75),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        if (successMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = successMessage,
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Row {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkButton,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Atrás")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    successMessage = if (userInput.isBlank()) {
                        "Ingresa un dato para continuar."
                    } else {
                        "Solicitud registrada correctamente. En esta versión no se envía correo todavía."
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Siguiente paso")
            }
        }
    }
}