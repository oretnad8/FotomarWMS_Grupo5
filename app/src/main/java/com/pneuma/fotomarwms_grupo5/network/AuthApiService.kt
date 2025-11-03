package com.pneuma.fotomarwms_grupo5.network

import com.pneuma.fotomarwms_grupo5.network.models.LoginRequest
import com.pneuma.fotomarwms_grupo5.network.models.LoginResponse
import com.pneuma.fotomarwms_grupo5.network.models.ValidateTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST(":8081/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST(":8081/api/auth/logout")
    suspend fun logout(): Response<Unit> // Token se añade por Interceptor

    @GET(":8081/api/auth/validate")
    suspend fun validateToken(): Response<ValidateTokenResponse> // Token se añade por Interceptor
}