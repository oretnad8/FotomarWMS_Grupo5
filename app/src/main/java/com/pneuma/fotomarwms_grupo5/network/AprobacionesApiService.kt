package com.pneuma.fotomarwms_grupo5.network

import com.pneuma.fotomarwms_grupo5.model.Aprobacion
import com.pneuma.fotomarwms_grupo5.network.models.AprobacionRequest
import com.pneuma.fotomarwms_grupo5.network.models.AprobarRequest
import com.pneuma.fotomarwms_grupo5.network.models.RechazarRequest
import retrofit2.Response
import retrofit2.http.*

interface AprobacionesApiService {

    @GET(":8085/api/aprobaciones")
    suspend fun getAprobaciones(@Query("estado") estado: String?): Response<List<Aprobacion>>

    @GET(":8085/api/aprobaciones/{id}")
    suspend fun getAprobacionById(@Path("id") id: Int): Response<Aprobacion>

    @POST(":8085/api/aprobaciones")
    suspend fun createAprobacion(@Body request: AprobacionRequest): Response<Aprobacion>

    @PUT(":8085/api/aprobaciones/{id}/aprobar")
    suspend fun aprobarSolicitud(
        @Path("id") id: Int,
        @Body request: AprobarRequest
    ): Response<Aprobacion>

    @PUT(":8085/api/aprobaciones/{id}/rechazar")
    suspend fun rechazarSolicitud(
        @Path("id") id: Int,
        @Body request: RechazarRequest
    ): Response<Aprobacion>

    @GET(":8085/api/aprobaciones/mis-solicitudes")
    suspend fun getMisSolicitudes(): Response<List<Aprobacion>>
}