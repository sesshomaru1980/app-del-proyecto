package com.example.neobarber.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String? = null,

    @SerializedName("accessToken")
    val accessToken: String? = null,

    @SerializedName("user")
    val user: UserDto? = null
)

data class UserDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("role")
    val role: String? = null
)