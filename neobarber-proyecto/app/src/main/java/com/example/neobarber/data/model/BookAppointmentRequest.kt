package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

data class BookAppointmentRequest(
    @SerializedName("serviceId")
    val serviceId: String,

    @SerializedName("barberId")
    val barberId: String,

    @SerializedName("startAt")
    val startAt: String
)