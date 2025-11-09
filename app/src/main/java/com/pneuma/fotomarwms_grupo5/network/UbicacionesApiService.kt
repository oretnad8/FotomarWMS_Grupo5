package com.pneuma.fotomarwms_grupo5.network

import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio API para ubicaciones (Puerto 8084)
 */
interface UbicacionesApiService {

    /**
     * GET /api/ubicaciones
     * Listar todas las ubicaciones
     * Query opcional: piso=A|B|C
     */
    @GET("api/ubicaciones")
    suspend fun getUbicaciones(@Query("piso") piso: String? = null): Response<List<UbicacionResponse>>

    /**
     * GET /api/ubicaciones/{codigo}
     * Obtener ubicación por código
     */
    @GET("api/ubicaciones/{codigo}")
    suspend fun getUbicacionByCodigo(@Path("codigo") codigo: String): Response<UbicacionResponse>

    /**
     * POST /api/ubicaciones/asignar
     * Asignar producto a ubicación
     * Roles: JEFE, SUPERVISOR, OPERADOR
     */
    @POST("api/ubicaciones/asignar")
    suspend fun asignarProducto(@Body request: AsignarUbicacionRequest): Response<Unit>
}
