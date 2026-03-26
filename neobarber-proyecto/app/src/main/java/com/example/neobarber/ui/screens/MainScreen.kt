package com.example.neobarber.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * Pantalla interna con navegación inferior.
 * Se deja corregida para evitar errores de compilación.
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val items = listOf(
        "home",
        "appointments",
        "profile"
    )

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.CalendarMonth,
        Icons.Default.Person
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEachIndexed { index, route ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(icons[index], contentDescription = route)
                        },
                        label = {
                            Text(route)
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeContent() }

            // Se corrige el parámetro obligatorio onBack.
            composable("appointments") {
                AppointmentsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("profile") { ProfileScreen() }
        }
    }
}