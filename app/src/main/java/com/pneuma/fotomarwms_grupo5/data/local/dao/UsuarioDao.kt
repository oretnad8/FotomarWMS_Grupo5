package com.pneuma.fotomarwms_grupo5.data.local.dao

import androidx.room.*
import com.pneuma.fotomarwms_grupo5.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    // Insertar o reemplazar usuarios (Upsert)
    @Upsert
    suspend fun upsertUsuarios(usuarios: List<UsuarioEntity>)

    // Obtener todos los usuarios como Flow (se actualiza automáticamente)
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun getAllUsuarios(): Flow<List<UsuarioEntity>>

    // Obtener un usuario por su serverId
    @Query("SELECT * FROM usuarios WHERE serverId = :serverId LIMIT 1")
    suspend fun getUsuarioByServerId(serverId: Int): UsuarioEntity?

    // Obtener usuarios que necesitan sincronización
    @Query("SELECT * FROM usuarios WHERE needsSync = 1")
    suspend fun getUsuariosPendientes(): List<UsuarioEntity>

    // Marcar usuarios como sincronizados (después de subirlos al backend)
    @Query("UPDATE usuarios SET needsSync = 0 WHERE serverId IN (:serverIds)")
    suspend fun marcarUsuariosComoSincronizados(serverIds: List<Int>)

    // Eliminar todos los usuarios (útil para refrescar datos)
    @Query("DELETE FROM usuarios")
    suspend fun deleteAll()
}