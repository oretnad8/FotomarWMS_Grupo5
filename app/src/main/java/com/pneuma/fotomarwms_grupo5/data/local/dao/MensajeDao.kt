package com.pneuma.fotomarwms_grupo5.data.local.dao

import androidx.room.*
import com.pneuma.fotomarwms_grupo5.data.local.entities.MensajeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MensajeDao {
    @Upsert
    suspend fun upsertMensajes(mensajes: List<MensajeEntity>)

    @Query("SELECT * FROM mensajes ORDER BY fecha DESC")
    fun getAllMensajes(): Flow<List<MensajeEntity>>

    @Query("SELECT * FROM mensajes WHERE leido = 0 ORDER BY fecha DESC")
    fun getMensajesNoLeidos(): Flow<List<MensajeEntity>>

    @Query("SELECT * FROM mensajes WHERE importante = 1 ORDER BY fecha DESC")
    fun getMensajesImportantes(): Flow<List<MensajeEntity>>

    @Query("UPDATE mensajes SET leido = 1, needsSync = 1 WHERE serverId = :serverId")
    suspend fun marcarComoLeido(serverId: Int)

    // Obtener mensajes cuyo estado 'leido' cambió y necesita sincronizarse
    @Query("SELECT serverId, leido FROM mensajes WHERE needsSync = 1")
    suspend fun getEstadosLeidoPendientes(): List<MensajeEstadoSync> // Data class simple

    // Marcar mensajes como sincronizados después de actualizar estado en backend
    @Query("UPDATE mensajes SET needsSync = 0 WHERE serverId IN (:serverIds)")
    suspend fun marcarEstadosLeidoComoSincronizados(serverIds: List<Int>)

    @Query("DELETE FROM mensajes")
    suspend fun deleteAll()
}

// Data class auxiliar para obtener solo el estado de 'leido' pendiente
data class MensajeEstadoSync(val serverId: Int, val leido: Boolean)