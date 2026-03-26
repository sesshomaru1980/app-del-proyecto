package com.example.neobarber.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Panel principal del administrador.
 */
@Composable
fun AdminHomeScreen(
    onGoServices: () -> Unit,
    onGoBarbers: () -> Unit,
    onGoAppointments: () -> Unit,
    onLogout: () -> Unit
) {
    val topColor = Color(0xFF2A1520)
    val bottomColor = Color(0xFF1B2740)
    val cardColor = Color.White.copy(alpha = 0.12f)
    val primaryBlue = Color(0xFF114BFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(topColor, bottomColor)
                )
            )
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        Text(
            text = "Panel Administrador",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Gestiona servicios, barberos y citas",
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        AdminDashboardCard(
            title = "Servicios",
            subtitle = "Crear, editar y eliminar servicios",
            icon = {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Servicios",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            },
            cardColor = cardColor,
            onClick = onGoServices
        )

        Spacer(modifier = Modifier.height(14.dp))

        AdminDashboardCard(
            title = "Barberos",
            subtitle = "Registrar y administrar barberos",
            icon = {
                Icon(
                    imageVector = Icons.Default.ContentCut,
                    contentDescription = "Barberos",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            },
            cardColor = cardColor,
            onClick = onGoBarbers
        )

        Spacer(modifier = Modifier.height(14.dp))

        AdminDashboardCard(
            title = "Citas",
            subtitle = "Consultar las citas del sistema",
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Citas",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            },
            cardColor = cardColor,
            onClick = onGoAppointments
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryBlue,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Cerrar sesión"
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun AdminDashboardCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    cardColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            icon()

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}