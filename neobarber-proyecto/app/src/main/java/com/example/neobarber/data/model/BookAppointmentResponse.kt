package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

data class BookAppointmentResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("message")
    val message: String? = null
)