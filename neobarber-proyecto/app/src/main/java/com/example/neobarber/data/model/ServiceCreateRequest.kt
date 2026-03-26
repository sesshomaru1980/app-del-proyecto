package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

data class ServiceCreateRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("durationMinutes")
    val durationMinutes: Int,

    @SerializedName("price")
    val price: Double,

    @SerializedName("imageUrl")
    val imageUrl: String
)