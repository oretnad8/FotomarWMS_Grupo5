package com.pneuma.fotomarwms_grupo5.db.daos

import androidx.room.*
import com.pneuma.fotomarwms_grupo5.db.entities.MensajeLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface MensajeDao {

    @Query("SELECT * FROM mensajes_pendientes ORDER BY timestamp ASC")
    fun getAllPendientes(): Flow<List<MensajeLocal>>

    @Query("SELECT * FROM mensajes_pendientes WHERE idLocal = :idLocal LIMIT 1")
    suspend fun getPendienteById(idLocal: Long): MensajeLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mensaje: MensajeLocal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mensajes: List<MensajeLocal>)

    @Update
    suspend fun update(mensaje: MensajeLocal)

    @Delete
    suspend fun delete(mensaje: MensajeLocal)

    @Query("DELETE FROM mensajes_pendientes WHERE idLocal = :idLocal")
    suspend fun deleteById(idLocal: Long)

    @Query("DELETE FROM mensajes_pendientes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM mensajes_pendientes")
    suspend fun countPendientes(): Int
}
