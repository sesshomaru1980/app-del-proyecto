package com.example.neobarber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.neobarber.core.network.AuthRegisterRequest
import com.example.neobarber.core.network.RetrofitClient
import com.example.neobarber.core.session.SessionManager
import com.example.neobarber.data.model.LoginRequest
import com.example.neobarber.ui.screens.AdminAppointmentsScreen
import com.example.neobarber.ui.screens.AdminBarbersScreen
import com.example.neobarber.ui.screens.AdminHomeScreen
import com.example.neobarber.ui.screens.AdminServicesScreen
import com.example.neobarber.ui.screens.AppointmentsScreen
import com.example.neobarber.ui.screens.BarberHomeScreen
import com.example.neobarber.ui.screens.ClientBookingScreen
import com.example.neobarber.ui.screens.ClientHomeScreen
import com.example.neobarber.ui.screens.ForgotPasswordScreen
import com.example.neobarber.ui.screens.LoginScreen
import com.example.neobarber.ui.screens.RegisterScreen
import com.example.neobarber.ui.screens.WelcomeScreen
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * Actividad principal de la aplicación.
 * Desde aquí se inicia Compose y la navegación general.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

/**
 * Navegación principal de la app.
 *
 * Se usa WelcomeScreen como pantalla inicial.
 * Además, aquí se controla el estado del login y registro.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // =========================
    // ESTADOS DEL LOGIN
    // =========================
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // =========================
    // ESTADOS DEL REGISTRO
    // =========================
    var registerLoading by remember { mutableStateOf(false) }
    var registerError by remember { mutableStateOf<String?>(null) }
    var registerSuccess by remember { mutableStateOf<String?>(null) }

    /**
     * Limpia completamente el formulario de inicio de sesión.
     * Se usa al cerrar sesión y al volver a entrar al login.
     */
    fun clearLoginForm() {
        loginEmail = ""
        loginPassword = ""
        loginError = null
        loginLoading = false
    }

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        /**
         * Pantalla principal de bienvenida.
         */
        composable("welcome") {
            WelcomeScreen(
                onLogin = { navController.navigate("login") },
                onRegister = { navController.navigate("register") }
            )
        }

        /**
         * Pantalla de login.
         * Cada vez que se entra aquí, se limpia el formulario.
         */
        composable("login") {
            LaunchedEffect(Unit) {
                clearLoginForm()
            }

            LoginScreen(
                email = loginEmail,
                password = loginPassword,
                isLoading = loginLoading,
                errorMessage = loginError,
                onEmailChange = { loginEmail = it },
                onPasswordChange = { loginPassword = it },
                onLogin = {
                    if (loginEmail.isBlank() || loginPassword.isBlank()) {
                        loginError = "Ingresa email y contraseña"
                        return@LoginScreen
                    }

                    scope.launch {
                        try {
                            loginLoading = true
                            loginError = null

                            val response = RetrofitClient.api.login(
                                LoginRequest(
                                    email = loginEmail,
                                    password = loginPassword
                                )
                            )

                            val token = response.token ?: response.accessToken
                            val role = response.user?.role

                            if (token.isNullOrBlank()) {
                                loginError = "No se recibió token de sesión"
                                return@launch
                            }

                            // Se guarda la sesión mínima necesaria.
                            sessionManager.saveToken(token)
                            sessionManager.saveRole(role)

                            when (role) {
                                "Admin" -> {
                                    navController.navigate("admin_home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }

                                "Client" -> {
                                    navController.navigate("client_home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }

                                "Barber" -> {
                                    navController.navigate("barber_home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }

                                else -> {
                                    loginError = "Rol no reconocido"
                                }
                            }
                        } catch (e: HttpException) {
                            loginError = e.response()?.errorBody()?.string()
                                ?: "Error HTTP ${e.code()}"
                        } catch (e: Exception) {
                            loginError = e.message ?: "Error al iniciar sesión"
                        } finally {
                            loginLoading = false
                        }
                    }
                },
                onForgotPassword = {
                    navController.navigate("forgot_password")
                },
                onBack = {
                    clearLoginForm()
                    navController.popBackStack()
                }
            )
        }

        /**
         * Pantalla de registro.
         * Muestra mensaje de éxito y no entra directo al panel cliente.
         */
        composable("register") {
            RegisterScreen(
                isLoading = registerLoading,
                errorMessage = registerError,
                successMessage = registerSuccess,
                onRegister = { fullName, phone, email, password ->
                    if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                        registerError = "Completa nombre, email y contraseña"
                        registerSuccess = null
                        return@RegisterScreen
                    }

                    scope.launch {
                        try {
                            registerLoading = true
                            registerError = null
                            registerSuccess = null

                            val response = RetrofitClient.api.register(
                                AuthRegisterRequest(
                                    fullName = fullName,
                                    email = email,
                                    password = password,
                                    role = "Client"
                                )
                            )

                            // Se muestra éxito, pero no se redirige automáticamente.
                            registerSuccess = response.message
                                ?: "Registro exitoso. Ahora puedes iniciar sesión."
                            registerError = null

                        } catch (e: HttpException) {
                            registerError = e.response()?.errorBody()?.string()
                                ?: "Error HTTP ${e.code()}"
                            registerSuccess = null
                        } catch (e: Exception) {
                            registerError = e.message ?: "Error al registrar usuario"
                            registerSuccess = null
                        } finally {
                            registerLoading = false
                        }
                    }
                },
                onGoLogin = {
                    clearLoginForm()
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        /**
         * Pantalla visual de recuperación de contraseña.
         */
        composable("forgot_password") {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }

        /**
         * Panel principal del cliente.
         */
        composable("client_home") {
            ClientHomeScreen(
                onBookingClick = {
                    navController.navigate("client_booking")
                },
                onLogout = {
                    // Se cierra la sesión y se limpia el formulario de login.
                    sessionManager.logout()
                    clearLoginForm()

                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        /**
         * Pantalla de reserva de citas para cliente.
         */
        composable("client_booking") {
            ClientBookingScreen(
                onBack = { navController.popBackStack() }
            )
        }

        /**
         * Panel principal del administrador.
         */
        composable("admin_home") {
            AdminHomeScreen(
                onGoServices = {
                    navController.navigate("admin_services")
                },
                onGoBarbers = {
                    navController.navigate("admin_barbers")
                },
                onGoAppointments = {
                    navController.navigate("admin_appointments")
                },
                onLogout = {
                    // Se cierra la sesión y se limpia el formulario de login.
                    sessionManager.logout()
                    clearLoginForm()

                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        /**
         * Pantalla de administración de servicios.
         */
        composable("admin_services") {
            AdminServicesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        /**
         * Pantalla de administración de barberos.
         */
        composable("admin_barbers") {
            AdminBarbersScreen(
                onBack = { navController.popBackStack() }
            )
        }

        /**
         * Pantalla de administración de citas.
         */
        composable("admin_appointments") {
            AdminAppointmentsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        /**
         * Panel principal del barbero.
         */
        composable("barber_home") {
            BarberHomeScreen(
                onOpenAppointments = {
                    navController.navigate("barber_appointments")
                },
                onLogout = {
                    // Se cierra la sesión y se limpia el formulario de login.
                    sessionManager.logout()
                    clearLoginForm()

                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        /**
         * Pantalla de citas del barbero.
         */
        composable("barber_appointments") {
            AppointmentsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}