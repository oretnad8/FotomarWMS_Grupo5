package com.pneuma.fotomarwms_grupo5.data.local.dao

import androidx.room.*
import com.pneuma.fotomarwms_grupo5.data.local.entities.UbicacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UbicacionDao {
    @Upsert
    suspend fun upsertUbicaciones(ubicaciones: List<UbicacionEntity>)

    @Query("SELECT * FROM ubicaciones ORDER BY piso ASC, numero ASC")
    fun getAllUbicaciones(): Flow<List<UbicacionEntity>>

    @Query("SELECT * FROM ubicaciones WHERE codigoUbicacion = :codigo LIMIT 1")
    suspend fun getUbicacionByCodigo(codigo: String): UbicacionEntity?

    @Query("SELECT * FROM ubicaciones WHERE piso = :piso ORDER BY numero ASC")
    fun getUbicacionesByPiso(piso: Char): Flow<List<UbicacionEntity>>

    @Query("SELECT * FROM ubicaciones WHERE needsSync = 1")
    suspend fun getUbicacionesPendientes(): List<UbicacionEntity>

    @Query("UPDATE ubicaciones SET needsSync = 0 WHERE serverId IN (:serverIds)")
    suspend fun marcarUbicacionesComoSincronizadas(serverIds: List<Int>)

    @Query("DELETE FROM ubicaciones")
    suspend fun deleteAll()
}