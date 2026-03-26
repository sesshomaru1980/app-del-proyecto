package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

data class ServiceDto(

    @SerializedName(value = "_id", alternate = ["id"])
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("durationMinutes")
    val durationMinutes: Int? = null,

    @SerializedName("price")
    val price: Double? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null
)