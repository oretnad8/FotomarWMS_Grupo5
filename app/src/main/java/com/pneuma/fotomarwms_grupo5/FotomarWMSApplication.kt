package com.pneuma.fotomarwms_grupo5

import android.app.Application
import com.pneuma.fotomarwms_grupo5.db.AppDatabase
import com.pneuma.fotomarwms_grupo5.network.RetrofitClient
import com.pneuma.fotomarwms_grupo5.repository.ProductoRepository
import com.pneuma.fotomarwms_grupo5.repository.UbicacionRepository

/**
 * Application class para FotomarWMS
 * Inicializa base de datos y repositorios
 */
class FotomarWMSApplication : Application() {

    // Base de datos
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    // Repositorios
    val productoRepository: ProductoRepository by lazy {
        ProductoRepository(
            productoDao = database.productoDao(),
            apiService = RetrofitClient.productosService
        )
    }

    val ubicacionRepository: UbicacionRepository by lazy {
        UbicacionRepository(
            ubicacionDao = database.ubicacionDao(),
            asignacionDao = database.asignacionUbicacionDao(),
            apiService = RetrofitClient.ubicacionesService
        )
    }

    override fun onCreate() {
        super.onCreate()
        
        // Restaurar token de autenticación si existe
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        if (token != null) {
            RetrofitClient.setAuthToken(token)
        }
    }

    /**
     * Guarda el token de autenticación
     */
    fun saveAuthToken(token: String, rol: String, userId: Int, nombre: String? = null, email: String? = null) {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = prefs.edit()
            .putString("token", token)
            .putString("rol", rol)
            .putInt("userId", userId)
        
        nombre?.let { editor.putString("nombre", it) }
        email?.let { editor.putString("email", it) }
        
        editor.apply()
        
        RetrofitClient.setAuthToken(token)
    }

    /**
     * Limpia el token de autenticación (logout)
     */
    fun clearAuthToken() {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        prefs.edit().clear().apply()
        
        RetrofitClient.setAuthToken(null)
    }

    /**
     * Obtiene el rol del usuario actual
     */
    fun getCurrentUserRole(): String? {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        return prefs.getString("rol", null)
    }

    /**
     * Obtiene el ID del usuario actual
     */
    fun getCurrentUserId(): Int {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        return prefs.getInt("userId", -1)
    }

    /**
     * Verifica si el usuario está autenticado
     */
    fun isAuthenticated(): Boolean {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        return prefs.getString("token", null) != null
    }
}
