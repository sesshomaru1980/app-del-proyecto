package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del backend para listar citas:
 *
 * {
 *   "success": true,
 *   "data": [ ... ]
 * }
 */
data class AppointmentListResponse(
    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("data")
    val data: List<AppointmentDto> = emptyList()
)

/**
 * Modelo de una cita ya formateada por el backend.
 */
data class AppointmentDto(

    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("client")
    val client: AppointmentPersonDto? = null,

    @SerializedName("barber")
    val barber: AppointmentPersonDto? = null,

    @SerializedName("service")
    val service: AppointmentServiceDto? = null,

    @SerializedName("startAt")
    val startAt: String? = null,

    @SerializedName("endAt")
    val endAt: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("notes")
    val notes: String? = null
)

/**
 * Cliente o barbero dentro de la cita.
 */
data class AppointmentPersonDto(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone")
    val phone: String? = null
)

/**
 * Servicio dentro de la cita.
 */
data class AppointmentServiceDto(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("price")
    val price: Double? = null,

    @SerializedName("description")
    val description: String? = null
)