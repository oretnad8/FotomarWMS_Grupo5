package com.pneuma.fotomarwms_grupo5.data.local.dao

import androidx.room.*
import com.pneuma.fotomarwms_grupo5.data.local.entities.AprobacionEntity
import com.pneuma.fotomarwms_grupo5.models.EstadoAprobacion
import kotlinx.coroutines.flow.Flow

@Dao
interface AprobacionDao {
    // Insertar una nueva solicitud creada localmente
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSolicitudLocal(aprobacion: AprobacionEntity): Long // Devuelve el localId

    // Actualizar/Insertar aprobaciones desde el servidor
    @Upsert
    suspend fun upsertAprobaciones(aprobaciones: List<AprobacionEntity>)

    @Query("SELECT * FROM aprobaciones ORDER BY fechaSolicitud DESC")
    fun getAllAprobaciones(): Flow<List<AprobacionEntity>>

    @Query("SELECT * FROM aprobaciones WHERE estado = :estado ORDER BY fechaSolicitud DESC")
    fun getAprobacionesByEstado(estado: EstadoAprobacion): Flow<List<AprobacionEntity>>

    @Query("SELECT * FROM aprobaciones WHERE localId = :localId LIMIT 1")
    suspend fun getAprobacionByLocalId(localId: Long): AprobacionEntity?

    @Query("SELECT * FROM aprobaciones WHERE serverId = :serverId LIMIT 1")
    suspend fun getAprobacionByServerId(serverId: Int): AprobacionEntity?

    // Obtener solicitudes creadas localmente y pendientes de subir
    @Query("SELECT * FROM aprobaciones WHERE needsSync = 1 AND serverId = 0")
    suspend fun getSolicitudesLocalesPendientes(): List<AprobacionEntity>

    // Actualizar una solicitud local con el serverId después de subirla
    @Query("UPDATE aprobaciones SET serverId = :serverId, needsSync = 0 WHERE localId = :localId")
    suspend fun marcarSolicitudComoSincronizada(localId: Long, serverId: Int)

    // Obtener aprobaciones actualizadas localmente (ej: estado cambiado) y pendientes de subir
    @Query("SELECT * FROM aprobaciones WHERE needsSync = 1 AND serverId != 0")
    suspend fun getAprobacionesActualizadasPendientes(): List<AprobacionEntity>

    // Marcar una aprobación actualizada como sincronizada
    @Query("UPDATE aprobaciones SET needsSync = 0 WHERE serverId = :serverId")
    suspend fun marcarAprobacionActualizadaComoSincronizada(serverId: Int)

    @Query("DELETE FROM aprobaciones")
    suspend fun deleteAll()
}