package com.pneuma.fotomarwms_grupo5.db.daos

import androidx.room.*
import com.pneuma.fotomarwms_grupo5.db.entities.UbicacionLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface UbicacionDao {
    
    @Query("SELECT * FROM ubicaciones_cache ORDER BY codigo ASC")
    fun getAll(): Flow<List<UbicacionLocal>>
    
    @Query("SELECT * FROM ubicaciones_cache WHERE piso = :piso ORDER BY numero ASC")
    fun getByPiso(piso: String): Flow<List<UbicacionLocal>>
    
    @Query("SELECT * FROM ubicaciones_cache WHERE codigo = :codigo LIMIT 1")
    suspend fun getByCodigo(codigo: String): UbicacionLocal?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ubicacion: UbicacionLocal)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ubicaciones: List<UbicacionLocal>)
    
    @Update
    suspend fun update(ubicacion: UbicacionLocal)
    
    @Delete
    suspend fun delete(ubicacion: UbicacionLocal)
    
    @Query("DELETE FROM ubicaciones_cache")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM ubicaciones_cache")
    suspend fun count(): Int
}
