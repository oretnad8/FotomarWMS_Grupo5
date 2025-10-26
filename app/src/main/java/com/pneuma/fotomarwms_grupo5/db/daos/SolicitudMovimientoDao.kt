package com.pneuma.fotomarwms_grupo5.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pneuma.fotomarwms_grupo5.db.entities.SolicitudMovimientoLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudMovimientoDao {

    // Guarda una nueva solicitud y devuelve su ID local generado
    @Insert
    suspend fun insertarSolicitud(solicitud: SolicitudMovimientoLocal): Long

    // Borra una solicitud usando su ID local
    @Query("DELETE FROM solicitudes_pendientes WHERE idLocal = :id")
    suspend fun borrarSolicitudPorId(id: Long): Int // Devuelve filas borradas

    // Obtiene todas las solicitudes guardadas (para verlas o reintentar)
    @Query("SELECT * FROM solicitudes_pendientes ORDER BY timestamp ASC")
    fun obtenerTodasLasSolicitudes(): Flow<List<SolicitudMovimientoLocal>>
}