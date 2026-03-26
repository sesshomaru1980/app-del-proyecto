package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo del perfil de barbero que devuelve el backend.
 *
 * El backend retorna un perfil de barbero y dentro de "userId"
 * viene la información del usuario asociado.
 */
data class BarberDto(

    // ID del perfil del barbero
    @SerializedName("_id")
    val id: String? = null,

    // Usuario asociado al perfil del barbero
    @SerializedName("userId")
    val user: BarberUserDto? = null,

    // Biografía o descripción del barbero
    @SerializedName("bio")
    val bio: String? = null,

    // URL de imagen del barbero
    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    // Disponibilidad semanal del barbero
    @SerializedName("weeklyAvailability")
    val weeklyAvailability: List<WeeklyAvailabilityDto>? = null
)

/**
 * Usuario asociado al perfil del barbero.
 */
data class BarberUserDto(

    // ID del usuario
    @SerializedName("_id")
    val id: String? = null,

    // Nombre completo del usuario
    @SerializedName("fullName")
    val fullName: String? = null,

    // Correo electrónico del usuario
    @SerializedName("email")
    val email: String? = null,

    // Rol del usuario
    @SerializedName("role")
    val role: String? = null,

    // Estado activo/inactivo del usuario
    @SerializedName("isActive")
    val isActive: Boolean? = null
)