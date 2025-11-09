package com.pneuma.fotomarwms_grupo5.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit centralizado para todos los microservicios
 * Gestiona la configuración de red, interceptores y autenticación
 */
object RetrofitClient {

    // URLs base de los microservicios
    private const val BASE_URL_AUTH = "http://fotomarwmsdb.ddns.net:8081/"
    private const val BASE_URL_USUARIOS = "http://fotomarwmsdb.ddns.net:8082/"
    private const val BASE_URL_PRODUCTOS = "http://fotomarwmsdb.ddns.net:8083/"
    private const val BASE_URL_UBICACIONES = "http://fotomarwmsdb.ddns.net:8084/"
    private const val BASE_URL_APROBACIONES = "http://fotomarwmsdb.ddns.net:8085/"
    private const val BASE_URL_MENSAJES = "http://fotomarwmsdb.ddns.net:8086/"
    private const val BASE_URL_INVENTARIO = "http://fotomarwmsdb.ddns.net:8087/"

    // Token de autenticación (se actualiza después del login)
    private var authToken: String? = null

    /**
     * Actualiza el token de autenticación para todas las peticiones
     */
    fun setAuthToken(token: String?) {
        authToken = token
    }

    /**
     * Obtiene el token actual
     */
    fun getAuthToken(): String? = authToken

    /**
     * Interceptor para agregar el token de autenticación a todas las peticiones
     */
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        
        // Agregar token si existe
        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        // Agregar Content-Type
        requestBuilder.addHeader("Content-Type", "application/json")
        
        chain.proceed(requestBuilder.build())
    }

    /**
     * Interceptor para logging (solo en debug)
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente HTTP compartido con interceptores
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Crea una instancia de Retrofit para una URL base específica
     */
    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ========== SERVICIOS API ==========

    val authService: AuthApiService by lazy {
        createRetrofit(BASE_URL_AUTH).create(AuthApiService::class.java)
    }

    val usuariosService: UsuariosApiService by lazy {
        createRetrofit(BASE_URL_USUARIOS).create(UsuariosApiService::class.java)
    }

    val productosService: ProductosApiService by lazy {
        createRetrofit(BASE_URL_PRODUCTOS).create(ProductosApiService::class.java)
    }

    val ubicacionesService: UbicacionesApiService by lazy {
        createRetrofit(BASE_URL_UBICACIONES).create(UbicacionesApiService::class.java)
    }

    val aprobacionesService: AprobacionesApiService by lazy {
        createRetrofit(BASE_URL_APROBACIONES).create(AprobacionesApiService::class.java)
    }

    val mensajesService: MensajesApiService by lazy {
        createRetrofit(BASE_URL_MENSAJES).create(MensajesApiService::class.java)
    }

    val inventarioService: InventarioApiService by lazy {
        createRetrofit(BASE_URL_INVENTARIO).create(InventarioApiService::class.java)
    }
}
