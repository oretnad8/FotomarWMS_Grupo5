package com.pneuma.fotomarwms_grupo5.network

import com.pneuma.fotomarwms_grupo5.model.Ubicacion
import com.pneuma.fotomarwms_grupo5.network.models.AsignarProductoRequest
import com.pneuma.fotomarwms_grupo5.network.models.AsignarProductoResponse
import retrofit2.Response
import retrofit2.http.*

interface UbicacionesApiService {

    @GET(":8084/api/ubicaciones")
    suspend fun getUbicaciones(@Query("piso") piso: Char?): Response<List<Ubicacion>>

    @GET(":8084/api/ubicaciones/{codigo}")
    suspend fun getUbicacionByCodigo(@Path("codigo") codigo: String): Response<Ubicacion>

    @POST(":8084/api/ubicaciones/asignar")
    suspend fun asignarProducto(@Body request: AsignarProductoRequest): Response<AsignarProductoResponse>
}