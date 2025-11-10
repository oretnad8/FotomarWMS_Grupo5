package com.pneuma.fotomarwms_grupo5.repository

import android.util.Log
import androidx.compose.foundation.layout.size
import kotlinx.coroutines.flow.first
import com.pneuma.fotomarwms_grupo5.db.daos.AsignacionUbicacionDao
import com.pneuma.fotomarwms_grupo5.db.daos.UbicacionDao
import com.pneuma.fotomarwms_grupo5.db.entities.AsignacionUbicacionLocal
import com.pneuma.fotomarwms_grupo5.db.entities.UbicacionLocal
import com.pneuma.fotomarwms_grupo5.models.Ubicacion
import com.pneuma.fotomarwms_grupo5.network.AsignarUbicacionRequest
import com.pneuma.fotomarwms_grupo5.network.UbicacionesApiService
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para ubicaciones con patrón local-first
 */
class UbicacionRepository(
    private val ubicacionDao: UbicacionDao,
    private val asignacionDao: AsignacionUbicacionDao,
    private val apiService: UbicacionesApiService
) {

    companion object {
        private const val TAG = "UbicacionRepository"
    }

    // ========== CONSULTA DE UBICACIONES ==========

    /**
     * Obtiene todas las ubicaciones del backend y las cachea localmente
     */
    suspend fun getUbicaciones(piso: String? = null, forceRefresh: Boolean = false): Result<List<Ubicacion>> {
        return try {
            // Si no es refresh forzado, intentar obtener del cache
            if (!forceRefresh) {
                val cached = if (piso != null) {
                    ubicacionDao.getByPiso(piso)
                } else {
                    ubicacionDao.getAll()
                }

                // 2. Usamos .first() para leer el valor actual del Flow sin bloquear.
                val cachedData = cached.first()

                // 3. Si el caché tiene datos, los devolvemos y terminamos la función aquí mismo.
                if (cachedData.isNotEmpty()) {
                    Log.d(TAG, "Cargando ${cachedData.size} ubicaciones desde el caché.")
                    val ubicacionesFromCache = cachedData.map { local ->
                        Ubicacion(
                            idUbicacion = 0, // Asumimos que la entidad local no tiene el ID correcto
                            codigoUbicacion = local.codigo,
                            piso = local.piso.firstOrNull() ?: ' ',
                            numero = local.numero,
                            productos = null
                        )
                    }
                    return Result.success(ubicacionesFromCache)
                }
                // --------------------------
            }

            // Obtener del backend (Este código solo se ejecuta si el caché está vacío o se fuerza el refresco)
            val response = apiService.getUbicaciones(piso)
            if (response.isSuccessful && response.body() != null) {
                val ubicaciones = response.body()!!.map { apiUbicacion -> // apiUbicacion ahora es tu nueva UbicacionResponse
                    Ubicacion(        idUbicacion = apiUbicacion.idUbicacion,
                        codigoUbicacion = apiUbicacion.codigoUbicacion, // ¡Ahora sí existe y no es nulo!
                        piso = apiUbicacion.piso.firstOrNull() ?: ' ',
                        numero = apiUbicacion.numero,
                        productos = null // Mantenemos esto simple por ahora
                    )
                }

                // Actualizar cache
                val ubicacionesLocal = ubicaciones.map {
                    UbicacionLocal(
                        codigo = it.codigoUbicacion,
                        piso = it.piso.toString(),
                        numero = it.numero
                    )
                }
                ubicacionDao.insertAll(ubicacionesLocal)

                Log.d(TAG, "Ubicaciones actualizadas desde backend: ${ubicaciones.size}")
                Result.success(ubicaciones)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener ubicaciones", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene una ubicación específica por código
     */
    suspend fun getUbicacionByCodigo(codigo: String): Result<Ubicacion> {
        return try {
            val response = apiService.getUbicacionByCodigo(codigo)
            if (response.isSuccessful && response.body() != null) {
                val ub = response.body()!!
                val ubicacion = Ubicacion(
                    idUbicacion = 0,
                    // --- CÓDIGO CORREGIDO ---
                    codigoUbicacion = ub.codigoUbicacion,
                    piso = ub.piso.firstOrNull() ?: 'A',
                    numero = ub.numero,
                    productos = null
                )
                Result.success(ubicacion)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener ubicación $codigo", e)
            Result.failure(e)
        }
    }

    // ========== ASIGNACIÓN DE PRODUCTOS (LOCAL-FIRST) ==========

    /**
     * Asigna un producto a una ubicación con patrón local-first
     */
    suspend fun asignarProducto(sku: String, codigoUbicacion: String, cantidad: Int): Result<Unit> {
        return try {
            // 1. Guardar en Room primero
            val asignacionLocal = AsignacionUbicacionLocal(
                sku = sku,
                codigoUbicacion = codigoUbicacion,
                cantidad = cantidad
            )
            val idLocal = asignacionDao.insert(asignacionLocal)
            Log.d(TAG, "Asignación guardada localmente: $sku -> $codigoUbicacion")

            // 2. Intentar enviar al backend
            try {
                val request = AsignarUbicacionRequest(
                    sku = sku,
                    codigoUbicacion = codigoUbicacion,
                    cantidad = cantidad
                )
                val response = apiService.asignarProducto(request)
                
                if (response.isSuccessful) {
                    // 3. Si OK, eliminar de Room
                    asignacionDao.deleteById(idLocal)
                    Log.d(TAG, "Asignación completada en backend: $sku -> $codigoUbicacion")
                    Result.success(Unit)
                } else {
                    Log.w(TAG, "Asignación guardada solo localmente: ${response.code()}")
                    Result.failure(Exception("Guardado localmente. Sincronizará después."))
                }
            } catch (e: Exception) {
                Log.w(TAG, "Asignación guardada solo localmente (sin conexión)", e)
                Result.failure(Exception("Guardado localmente. Sincronizará cuando haya conexión."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al asignar producto", e)
            Result.failure(e)
        }
    }

    // ========== SINCRONIZACIÓN ==========

    /**
     * Obtiene todas las asignaciones pendientes de sincronización
     */
    fun getAsignacionesPendientes(): Flow<List<AsignacionUbicacionLocal>> {
        return asignacionDao.getAllPendientes()
    }

    /**
     * Sincroniza todas las asignaciones pendientes
     */
    suspend fun syncAsignacionesPendientes(): Result<Int> {
        return try {
            val pendientes = asignacionDao.getAllPendientes()
            var syncCount = 0
            
            pendientes.collect { lista ->
                lista.forEach { asignacionLocal ->
                    try {
                        val request = AsignarUbicacionRequest(
                            sku = asignacionLocal.sku,
                            codigoUbicacion = asignacionLocal.codigoUbicacion,
                            cantidad = asignacionLocal.cantidad
                        )

                        val response = apiService.asignarProducto(request)
                        if (response.isSuccessful) {
                            asignacionDao.deleteById(asignacionLocal.idLocal)
                            syncCount++
                            Log.d(TAG, "Asignación sincronizada: ${asignacionLocal.sku}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al sincronizar asignación ${asignacionLocal.idLocal}", e)
                    }
                }
            }
            
            Result.success(syncCount)
        } catch (e: Exception) {
            Log.e(TAG, "Error en sincronización de asignaciones", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene ubicaciones del cache local
     */
    fun getUbicacionesFromCache(piso: String? = null): Flow<List<UbicacionLocal>> {
        return if (piso != null) {
            ubicacionDao.getByPiso(piso)
        } else {
            ubicacionDao.getAll()
        }
    }
}
