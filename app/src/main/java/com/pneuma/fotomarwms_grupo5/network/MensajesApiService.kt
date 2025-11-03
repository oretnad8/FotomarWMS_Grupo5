package com.pneuma.fotomarwms_grupo5.network

import com.pneuma.fotomarwms_grupo5.model.Mensaje
import com.pneuma.fotomarwms_grupo5.network.models.MensajeRequest
import com.pneuma.fotomarwms_grupo5.network.models.MensajeResumenDTO
import retrofit2.Response
import retrofit2.http.*

interface MensajesApiService {

    @GET(":8086/api/mensajes")
    suspend fun getMensajes(
        @Query("soloNoLeidos") soloNoLeidos: Boolean?,
        @Query("soloImportantes") soloImportantes: Boolean?
    ): Response<List<Mensaje>>

    @GET(":8086/api/mensajes/resumen")
    suspend fun getResumenMensajes(): Response<MensajeResumenDTO>

    @GET(":8086/api/mensajes/enviados")
    suspend fun getMensajesEnviados(): Response<List<Mensaje>>

    @GET(":8086/api/mensajes/{id}")
    suspend fun getMensajeById(@Path("id") id: Int): Response<Mensaje>

    @POST(":8086/api/mensajes")
    suspend fun createMensaje(@Body request: MensajeRequest): Response<Mensaje>

    @PUT(":8086/api/mensajes/{id}/marcar-leido")
    suspend fun marcarComoLeido(@Path("id") id: Int): Response<Mensaje>

    @PUT(":8086/api/mensajes/{id}/toggle-importante")
    suspend fun toggleImportante(@Path("id") id: Int): Response<Mensaje>
}