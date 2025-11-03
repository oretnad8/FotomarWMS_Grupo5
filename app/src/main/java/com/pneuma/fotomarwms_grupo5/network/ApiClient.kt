package com.pneuma.fotomarwms_grupo5.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // URL base de tu servidor DDNS
    private const val BASE_URL = "http://fotomarwmsdb.ddns.net"

    // Variable para almacenar el token JWT
    private var token: String? = null

    /**
     * Actualiza el token JWT después de un inicio de sesión exitoso.
     */
    fun setToken(newToken: String) {
        token = newToken
    }

    /**
     * Limpia el token al cerrar sesión.
     */
    fun clearToken() {
        token = null
    }

    // Interceptor para añadir automáticamente el token a todas las llamadas
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        chain.proceed(requestBuilder.build())
    }

    // Interceptor para ver los logs de red en Logcat (útil para depurar)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp que usa los interceptors y define timeouts
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Configuración de Gson para manejar fechas (si es necesario)
    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    // Instancia principal de Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // --- Instancias de los Servicios ---

    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val usuariosService: UsuariosApiService by lazy {
        retrofit.create(UsuariosApiService::class.java)
    }

    val productosService: ProductosApiService by lazy {
        retrofit.create(ProductosApiService::class.java)
    }

    val ubicacionesService: UbicacionesApiService by lazy {
        retrofit.create(UbicacionesApiService::class.java)
    }

    val aprobacionesService: AprobacionesApiService by lazy {
        retrofit.create(AprobacionesApiService::class.java)
    }

    val mensajesService: MensajesApiService by lazy {
        retrofit.create(MensajesApiService::class.java)
    }

    val inventarioService: InventarioApiService by lazy {
        retrofit.create(InventarioApiService::class.java)
    }
}