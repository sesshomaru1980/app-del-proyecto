package com.example.neobarber.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.neobarber.core.session.SessionManager

@Composable
fun ProfileScreen() {

    val navController = rememberNavController()
    val context = navController.context
    val session = SessionManager(context)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Perfil")

        Button(onClick = {
            session.logout()
        }) {
            Text("Cerrar sesión")
        }
    }
}