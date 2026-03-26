package com.example.neobarber.core.network

import com.example.neobarber.data.model.AppointmentListResponse
import com.example.neobarber.data.model.AppointmentStatusRequest
import com.example.neobarber.data.model.BarberCreateRequest
import com.example.neobarber.data.model.BarberDto
import com.example.neobarber.data.model.BookAppointmentRequest
import com.example.neobarber.data.model.BookAppointmentResponse
import com.example.neobarber.data.model.LoginRequest
import com.example.neobarber.data.model.LoginResponse
import com.example.neobarber.data.model.ServiceCreateRequest
import com.example.neobarber.data.model.ServiceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Todas las rutas HTTP consumidas por la aplicación.
 * Se mantiene alineado con el backend real.
 */
interface ApiService {

    // =========================
    // AUTH
    // =========================

    /**
     * Iniciar sesión.
     */
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    /**
     * Registrar usuario.
     * Se usa en la pantalla de registro.
     */
    @POST("api/auth/register")
    suspend fun register(
        @Body request: AuthRegisterRequest
    ): AuthRegisterResponse

    // =========================
    // SERVICES
    // =========================

    @GET("api/services")
    suspend fun getServices(
        @Header("Authorization") token: String
    ): List<ServiceDto>

    @POST("api/services")
    suspend fun createService(
        @Header("Authorization") token: String,
        @Body request: ServiceCreateRequest
    ): ServiceDto

    @PUT("api/services/{id}")
    suspend fun updateService(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: ServiceCreateRequest
    ): ServiceDto

    @DELETE("api/services/{id}")
    suspend fun deleteService(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    // =========================
    // BARBERS
    // =========================

    @GET("api/barbers")
    suspend fun getBarbers(
        @Header("Authorization") token: String
    ): List<BarberDto>

    @POST("api/barbers/admin-create")
    suspend fun createBarber(
        @Header("Authorization") token: String,
        @Body request: BarberCreateRequest
    ): Response<Unit>

    @PATCH("api/barbers/{userId}/active")
    suspend fun setBarberActive(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body request: BarberActiveRequest
    ): Response<GenericMessageResponse>

    // =========================
    // APPOINTMENTS
    // =========================

    @POST("api/appointments")
    suspend fun createAppointment(
        @Header("Authorization") token: String,
        @Body request: BookAppointmentRequest
    ): BookAppointmentResponse

    /**
     * Listar citas del admin.
     */
    @GET("api/appointments")
    suspend fun getAppointments(
        @Header("Authorization") token: String
    ): AppointmentListResponse

    /**
     * Listar citas del barbero.
     */
    @GET("api/appointments/barber")
    suspend fun getBarberAppointments(
        @Header("Authorization") token: String
    ): AppointmentListResponse

    @PUT("api/appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: AppointmentStatusRequest
    ): Response<GenericMessageResponse>

    @PUT("api/appointments/{id}/cancel")
    suspend fun cancelAppointment(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<GenericMessageResponse>
}

/**
 * Request para activar o desactivar un barbero.
 */
data class BarberActiveRequest(
    val isActive: Boolean
)

/**
 * Respuesta genérica simple del backend.
 */
data class GenericMessageResponse(
    val success: Boolean? = null,
    val message: String? = null
)

/**
 * Request para registrar usuarios.
 * El backend espera fullName, email, password y role.
 */
data class AuthRegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String
)

/**
 * Respuesta genérica del registro.
 */
data class AuthRegisterResponse(
    val success: Boolean? = null,
    val message: String? = null
)