package com.pneuma.fotomarwms_grupo5.network

import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio API para inventario (Puerto 8087)
 */
interface InventarioApiService {

    /**
     * GET /api/inventario/progreso
     * Obtener progreso del inventario
     */
    @GET("api/inventario/progreso")
    suspend fun getProgreso(): Response<ProgresoInventarioResponse>

    /**
     * POST /api/inventario/conteo
     * Registrar conteo físico
     * Roles: OPERADOR, SUPERVISOR, JEFE
     */
    @POST("api/inventario/conteo")
    suspend fun registrarConteo(@Body request: ConteoRequest): Response<Unit>

    /**
     * GET /api/inventario/diferencias
     * Listar diferencias
     * Query: soloConDiferencias=[true|false]
     */
    @GET("api/inventario/diferencias")
    suspend fun getDiferencias(
        @Query("soloConDiferencias") soloConDiferencias: Boolean? = null
    ): Response<List<DiferenciaInventarioResponse>>

    /**
     * POST /api/inventario/finalizar
     * Finalizar inventario y ajustar
     * Roles: JEFE, SUPERVISOR
     */
    @POST("api/inventario/finalizar")
    suspend fun finalizarInventario(): Response<Unit>
}
