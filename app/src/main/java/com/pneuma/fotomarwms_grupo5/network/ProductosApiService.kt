package com.pneuma.fotomarwms_grupo5.network

import com.pneuma.fotomarwms_grupo5.model.Producto
import com.pneuma.fotomarwms_grupo5.network.models.ProductoRequest
import com.pneuma.fotomarwms_grupo5.network.models.ProductoUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface ProductosApiService {

    @GET(":8083/api/productos/search")
    suspend fun searchProductos(@Query("q") query: String?): Response<List<Producto>>

    @GET(":8083/api/productos/{sku}")
    suspend fun getProductoBySku(@Path("sku") sku: String): Response<Producto>

    @POST(":8083/api/productos")
    suspend fun createProducto(@Body request: ProductoRequest): Response<Producto>

    @PUT(":8083/api/productos/{sku}")
    suspend fun updateProducto(
        @Path("sku") sku: String,
        @Body request: ProductoUpdateRequest
    ): Response<Producto>

    @DELETE(":8083/api/productos/{sku}")
    suspend fun deleteProducto(@Path("sku") sku: String): Response<Unit>
}