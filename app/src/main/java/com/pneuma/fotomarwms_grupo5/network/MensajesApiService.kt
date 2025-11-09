package com.pneuma.fotomarwms_grupo5.network

import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio API para mensajes (Puerto 8086)
 */
interface MensajesApiService {

    /**
     * GET /api/mensajes
     * Listar mensajes del usuario
     * Query: soloNoLeidos=[true|false], soloImportantes=[true|false]
     */
    @GET("api/mensajes")
    suspend fun getMensajes(
        @Query("soloNoLeidos") soloNoLeidos: Boolean? = null,
        @Query("soloImportantes") soloImportantes: Boolean? = null
    ): Response<List<MensajeResponse>>

    /**
     * GET /api/mensajes/resumen
     * Resumen de mensajes
     */
    @GET("api/mensajes/resumen")
    suspend fun getResumenMensajes(): Response<ResumenMensajesResponse>

    /**
     * GET /api/mensajes/enviados
     * Mensajes enviados por mí
     */
    @GET("api/mensajes/enviados")
    suspend fun getMensajesEnviados(): Response<List<MensajeResponse>>

    /**
     * GET /api/mensajes/{id}
     * Obtener mensaje por ID
     */
    @GET("api/mensajes/{id}")
    suspend fun getMensajeById(@Path("id") id: Int): Response<MensajeResponse>

    /**
     * POST /api/mensajes
     * Enviar mensaje
     * Roles: JEFE, SUPERVISOR, ADMIN
     */
    @POST("api/mensajes")
    suspend fun enviarMensaje(@Body request: MensajeRequest): Response<MensajeResponse>

    /**
     * PUT /api/mensajes/{id}/marcar-leido
     * Marcar mensaje como leído
     */
    @PUT("api/mensajes/{id}/marcar-leido")
    suspend fun marcarComoLeido(@Path("id") id: Int): Response<Unit>

    /**
     * PUT /api/mensajes/{id}/toggle-importante
     * Cambiar importancia del mensaje
     * Roles: JEFE, SUPERVISOR, ADMIN
     */
    @PUT("api/mensajes/{id}/toggle-importante")
    suspend fun toggleImportante(@Path("id") id: Int): Response<Unit>
}
