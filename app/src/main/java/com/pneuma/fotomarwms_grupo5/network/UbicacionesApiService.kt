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
     * GET /api/ubicaciones/pasillo/{pasillo}/posicion/{posicion}
     * Obtener las 3 ubicaciones (pisos A, B, C) de una posición específica en un pasillo
     * Ejemplo: pasillo=4, posicion=25 retorna P4-A-25, P4-B-25, P4-C-25
     */
    @GET("api/ubicaciones/pasillo/{pasillo}/posicion/{posicion}")
    suspend fun getUbicacionesByPasilloYPosicion(
        @Path("pasillo") pasillo: Int,
        @Path("posicion") posicion: Int
    ): Response<List<UbicacionResponse>>

    /**
     * POST /api/ubicaciones/asignar
     * Asignar producto a ubicación
     * Body: { sku, codigoUbicacion (formato P1-A-01), cantidad }
     * Roles: JEFE, SUPERVISOR, OPERADOR
     */
    @POST("api/ubicaciones/asignar")
    suspend fun asignarProducto(@Body request: AsignarUbicacionRequest): Response<Unit>
}
