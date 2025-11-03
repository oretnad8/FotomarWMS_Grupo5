package com.pneuma.fotomarwms_grupo5.network

import com.pneuma.fotomarwms_grupo5.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface InventarioApiService {

    @GET(":8087/api/inventario/progreso")
    suspend fun getProgreso(): Response<ProgresoResponse>

    @POST(":8087/api/inventario/conteo")
    suspend fun registrarConteo(@Body request: ConteoRequest): Response<ConteoResponse>

    @GET(":8087/api/inventario/diferencias")
    suspend fun getDiferencias(
        @Query("soloConDiferencias") soloConDiferencias: Boolean?
    ): Response<List<DiferenciaResponse>>

    @POST(":8087/api/inventario/finalizar")
    suspend fun finalizarInventario(): Response<FinalizarInventarioResponse>
}