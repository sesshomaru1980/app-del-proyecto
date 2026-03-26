package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request usado para iniciar sesión.
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

/**
 * Request usado para registrar un cliente nuevo.
 *
 * Se deja en este mismo archivo para no alterar la estructura base
 * del proyecto que ya traía el paquete data.model.
 */
data class RegisterRequest(
    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("role")
    val role: String
)
