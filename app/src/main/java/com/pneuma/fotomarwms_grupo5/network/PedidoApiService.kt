package com.pneuma.fotomarwms_grupo5.network

import retrofit2.Response
import retrofit2.http.*

interface PedidoApiService {

    @GET("api/pedidos/pendientes")
    suspend fun getPedidosPendientes(): Response<List<Pedido>>

    @GET("api/pedidos/picking")
    suspend fun getPedidosEnPicking(): Response<List<Pedido>>

    @GET("api/pedidos/all")
    suspend fun getAllPedidos(): Response<List<Pedido>>

    @PUT("api/pedidos/{id}/aceptar")
    suspend fun aceptarPedido(@Path("id") id: Int): Response<ApiResponse<Pedido>>

    @GET("api/pedidos/{id}/hoja-picking")
    suspend fun getHojaPicking(@Path("id") id: Int): Response<HojaPickingResponse>

    @GET("api/pedidos/{id}")
    suspend fun getPedido(@Path("id") id: Int): Response<Pedido>

    @POST("api/pedidos/{id}/confirmar-picking")
    suspend fun confirmarPicking(
        @Path("id") id: Int,
        @Body request: ConfirmarPickingRequest
    ): Response<ApiResponse<Pedido>>

    @PUT("api/pedidos/{id}/validar-despacho")
    suspend fun validarDespacho(@Path("id") id: Int): Response<ApiResponse<Pedido>>

    @PUT("api/pedidos/{id}/iniciar-transporte")
    suspend fun iniciarTransporte(@Path("id") id: Int): Response<ApiResponse<Pedido>>

    @POST("api/pedidos/{id}/finalizar-entrega")
    suspend fun finalizarEntrega(
        @Path("id") id: Int,
        @Body request: FinalizarEntregaRequest
    ): Response<ApiResponse<Pedido>>

    @GET("api/pedidos/monitoreo")
    suspend fun getMonitoreo(): Response<MonitoreoResponse>
}
