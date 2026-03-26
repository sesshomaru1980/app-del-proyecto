package com.example.neobarber.core.network

/**
 * Configuración central de la URL base del backend.
 *
 * Si se usa el emulador de Android Studio normalmente se reemplaza por:
 * http://10.0.2.2:3000/
 *
 * Si se usa un dispositivo físico, se debe colocar la IP local del PC.
 */
object ApiConfig {
    const val BASE_URL = "http://192.168.100.69:3000/"
}
