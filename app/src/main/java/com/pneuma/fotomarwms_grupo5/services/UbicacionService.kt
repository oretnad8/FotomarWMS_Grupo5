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
                // 1. Intentar obtener de la base de datos local
                val ubicacionLocal = ubicacionDao.getByCodigoUbicacion(codigo)
                if (ubicacionLocal != null) {
                    return@withContext ubicacionLocal.idUbicacion
                }

                // 2. Si no está en local, obtener del backend
                val response = ubicacionesService.getUbicacionByCodigo(codigo)
                if (response.isSuccessful && response.body() != null) {
                    val ubicacionResponse = response.body()!!
                    
                    // Guardar en base de datos local para futuras consultas
                    val ubicacionLocal = UbicacionLocal(
                        idUbicacion = ubicacionResponse.idUbicacion,
                        codigoUbicacion = ubicacionResponse.codigoUbicacion,
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

    /**
     * Obtiene el código de una ubicación basado en su ID
     */
    suspend fun getCodigoUbicacionById(id: Int): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Intentar obtener de la base de datos local
                val ubicacionLocal = ubicacionDao.getById(id)
                if (ubicacionLocal != null) {
                    return@withContext ubicacionLocal.codigoUbicacion
                }

                // Si no está en local, retornar null (no hacer llamada al backend por ID)
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Obtiene todas las ubicaciones y las almacena en la base de datos local
     * Útil para sincronizar el caché local
     */
    suspend fun syncUbicaciones(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = ubicacionesService.getUbicaciones()
                if (response.isSuccessful && response.body() != null) {
                    val ubicaciones = response.body()!!.map { ubicacionResponse ->
                        UbicacionLocal(
                            idUbicacion = ubicacionResponse.idUbicacion,
                            codigoUbicacion = ubicacionResponse.codigoUbicacion,
                            pasillo = ubicacionResponse.pasillo,
                            piso = ubicacionResponse.piso,
                            numero = ubicacionResponse.numero
                        )
                    }
                    ubicacionDao.insertAll(ubicaciones)
                    return@withContext true
                }
                false
            } catch (e: Exception) {
                false
            }
        }
    }
}
