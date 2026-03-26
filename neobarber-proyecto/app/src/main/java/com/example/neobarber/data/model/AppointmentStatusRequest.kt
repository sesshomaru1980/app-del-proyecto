package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

/**
 * Body para actualizar el estado de una cita.
 * El backend espera:
 * {
 *   "status": "Confirmada"
 * }
 */
data class AppointmentStatusRequest(
    @SerializedName("status")
    val status: String
)