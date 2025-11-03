package com.pneuma.fotomarwms_grupo5.data.local.dao

import androidx.room.*
import com.pneuma.fotomarwms_grupo5.data.local.entities.ConteoEntity

@Dao
interface ConteoDao {
    @Insert
    suspend fun insertarConteo(conteo: ConteoEntity)

    // Obtener todos los conteos pendientes de subir
    @Query("SELECT * FROM conteos WHERE needsSync = 1 ORDER BY timestamp ASC")
    suspend fun getConteosPendientes(): List<ConteoEntity>

    // Eliminar conteos una vez subidos y confirmados por el backend
    @Query("DELETE FROM conteos WHERE id IN (:ids)")
    suspend fun deleteConteosSincronizados(ids: List<Long>)

    // Opcional: Obtener todos los conteos (quizás para historial local)
    @Query("SELECT * FROM conteos ORDER BY timestamp DESC")
    suspend fun getAllConteos(): List<ConteoEntity>
}