package com.pneuma.fotomarwms_grupo5.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pneuma.fotomarwms_grupo5.db.entities.AsignacionUbicacionLocal
import kotlinx.coroutines.flow.Flow

@Dao // Indica a Room que esto es un DAO
interface AsignacionUbicacionDao {

    /**
     * Guarda una asignación de ubicación pendiente en la BD local.
     * Devuelve el ID local autogenerado.
     */
    @Insert
    suspend fun insertarAsignacionPendiente(asignacion: AsignacionUbicacionLocal): Long

    /**
     * Borra una asignación pendiente de la BD local usando su ID.
     * Se llamará cuando el backend confirme la asignación.
     */
    @Query("DELETE FROM asignaciones_pendientes WHERE idLocal = :id")
    suspend fun borrarAsignacionPendientePorId(id: Long): Int // Devuelve filas borradas (debería ser 1)

    /**
     * (Opcional) Obtiene todas las asignaciones pendientes.
     * Útil para reintentos futuros.
     */
    @Query("SELECT * FROM asignaciones_pendientes ORDER BY timestamp ASC")
    fun obtenerAsignacionesPendientes(): Flow<List<AsignacionUbicacionLocal>>
}