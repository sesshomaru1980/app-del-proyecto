package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request para crear un barbero desde el panel administrador.
 *
 * El endpoint /api/barbers/admin-create recibe:
 * - fullName
 * - email
 * - password
 * - bio
 * - imageUrl
 * - weeklyAvailability (opcional)
 */
data class BarberCreateRequest(

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("bio")
    val bio: String = "",

    @SerializedName("imageUrl")
    val imageUrl: String = "",

    // Lo dejamos vacío por ahora para no complicar la UI.
    @SerializedName("weeklyAvailability")
    val weeklyAvailability: List<WeeklyAvailabilityDto> = emptyList()
)

/**
 * Disponibilidad semanal del barbero.
 * El backend la soporta, aunque por ahora no la estamos llenando desde la app.
 */
data class WeeklyAvailabilityDto(

    @SerializedName("dayOfWeek")
    val dayOfWeek: Int,

    @SerializedName("start")
    val start: String,

    @SerializedName("end")
    val end: String
)