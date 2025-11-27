package com.pneuma.fotomarwms_grupo5.services

import android.app.Application
import com.pneuma.fotomarwms_grupo5.db.AppDatabase
import com.pneuma.fotomarwms_grupo5.db.entities.UbicacionLocal
import com.pneuma.fotomarwms_grupo5.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio para gestionar ubicaciones
 * Proporciona funciones para convertir códigos de ubicación a IDs
 */
class UbicacionService(private val application: Application) {

    private val ubicacionDao = AppDatabase.getDatabase(application).ubicacionDao()
    private val ubicacionesService = RetrofitClient.ubicacionesService

    /**
     * Obtiene el ID de una ubicación basado en su código
     * Primero intenta obtener de la base de datos local
     * Si no está disponible, obtiene del backend
     */
    suspend fun getIdUbicacionByCodigo(codigo: String): Int? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ubicacionesService.getUbicacionByCodigo(codigo)
                if (response.isSuccessful && response.body() != null) {
                    val ubicacionResponse = response.body()!!

                    // Guardar en base de datos local para futuras consultas
                    val ubicacionLocal = UbicacionLocal(
                        codigo = ubicacionResponse.codigoUbicacion,
                        pasillo = ubicacionResponse.pasillo,
                        piso = ubicacionResponse.piso,
                        numero = ubicacionResponse.numero
                    )
                    ubicacionDao.insert(ubicacionLocal)

                    return@withContext ubicacionResponse.idUbicacion
                }

                null
            } catch (e: Exception) {
                null
            }
        }
    }
}