package com.example.neobarber.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Panel principal del cliente.
 * Desde aquí puede reservar cita o cerrar sesión.
 */
@Composable
fun ClientHomeScreen(
    onBookingClick: () -> Unit,
    onLogout: () -> Unit
) {
    val topColor = Color(0xFF2A1520)
    val bottomColor = Color(0xFF1B2740)
    val buttonBlue = Color(0xFF114BFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(topColor, bottomColor)
                )
            )
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Panel Cliente",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.padding(top = 12.dp)
        )

        Button(
            onClick = onBookingClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBlue,
                contentColor = Color.White
            )
        ) {
            Text("Reservar cita")
        }

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.padding(top = 12.dp)
        )

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC62828),
                contentColor = Color.White
            )
        ) {
            Text("Cerrar sesión")
        }
    }
}