package com.pneuma.fotomarwms_grupo5.network

import com.pneuma.fotomarwms_grupo5.model.Usuario
import com.pneuma.fotomarwms_grupo5.network.models.UsuarioRequest
import com.pneuma.fotomarwms_grupo5.network.models.UsuarioUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface UsuariosApiService {

    @GET(":8082/api/usuarios")
    suspend fun getAllUsuarios(): Response<List<Usuario>>

    @GET(":8082/api/usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Int): Response<Usuario>

    @POST(":8082/api/usuarios")
    suspend fun createUsuario(@Body request: UsuarioRequest): Response<Usuario>

    @PUT(":8082/api/usuarios/{id}")
    suspend fun updateUsuario(
        @Path("id") id: Int,
        @Body request: UsuarioUpdateRequest
    ): Response<Usuario>

    @DELETE(":8082/api/usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Response<Unit>

    @PUT(":8082/api/usuarios/{id}/toggle-activo")
    suspend fun toggleActivo(@Path("id") id: Int): Response<Usuario>
}