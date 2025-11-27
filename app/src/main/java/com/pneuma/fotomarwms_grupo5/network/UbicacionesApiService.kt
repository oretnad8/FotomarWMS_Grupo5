package com.pneuma.fotomarwms_grupo5.network

import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio API para ubicaciones (Puerto 8084)
 * Actualizado para soportar 5 pasillos con hasta 60 posiciones cada uno y 3 pisos (A, B, C)
 * Total: 900 ubicaciones (5 pasillos × 60 posiciones × 3 pisos)
 */
interface UbicacionesApiService {

    /**
     * GET /api/ubicaciones
     * Listar todas las ubicaciones (900)
     * Query opcional: piso=A|B|C para filtrar por piso
     * Query opcional: pasillo=1|2|3|4|5 para filtrar por pasillo
     */
    @GET("api/ubicaciones")
    suspend fun getUbicaciones(
        @Query("piso") piso: String? = null,
        @Query("pasillo") pasillo: Int? = null
    ): Response<List<UbicacionResponse>>

    /**
     * GET /api/ubicaciones/{codigo}
     * Obtener ubicación por código
     * Formato: P{pasillo}-{piso}-{numero} (ej: P1-A-15, P3-B-42)
     */
    @GET("api/ubicaciones/{codigo}")
    suspend fun getUbicacionByCodigo(@Path("codigo") codigo: String): Response<UbicacionResponse>

    /**
     * POST /api/ubicaciones/asignar
     * Asignar producto a ubicación (INGRESO)
     * Body: { sku, codigoUbicacion (formato P1-A-01), cantidad }
     * Roles: JEFE, SUPERVISOR, OPERADOR
     */
    @POST("api/ubicaciones/asignar")
    suspend fun asignarProducto(@Body request: AsignarUbicacionRequest): Response<Unit>
    
    /**
     * POST /api/ubicaciones/egreso
     * Registrar egreso de producto desde ubicación
     * Body: { sku, codigoUbicacion, cantidad, motivo }
     * Roles: JEFE, SUPERVISOR
     */
    @POST("api/ubicaciones/egreso")
    suspend fun egresoProducto(@Body request: EgresoUbicacionRequest): Response<Unit>
    
    /**
     * POST /api/ubicaciones/reubicar
     * Reubicar producto de una ubicación a otra
     * Body: { sku, codigoUbicacionOrigen, codigoUbicacionDestino, cantidad, motivo }
     * Roles: JEFE, SUPERVISOR
     */
    @POST("api/ubicaciones/reubicar")
    suspend fun reubicarProducto(@Body request: ReubicarUbicacionRequest): Response<Unit>
}
