package com.pneuma.fotomarwms_grupo5.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pneuma.fotomarwms_grupo5.models.EstadoAprobacion
import com.pneuma.fotomarwms_grupo5.models.TipoMovimiento

@Entity(tableName = "aprobaciones")
data class AprobacionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverId: Int, // ID del servidor (0 si es nueva y no sincronizada)
    val tipoMovimiento: TipoMovimiento,
    val sku: String,
    val cantidad: Int,
    val motivo: String,
    val estado: EstadoAprobacion,
    val solicitante: String,
    val idSolicitante: Int,
    val aprobador: String?,
    val idAprobador: Int?,
    val observaciones: String?,
    val fechaSolicitud: String,
    val fechaRespuesta: String?,
    val idUbicacionOrigen: Int?,
    val idUbicacionDestino: Int?,
    val ubicacionOrigen: String?,
    val ubicacionDestino: String?,
    val needsSync: Boolean = false // True si es una nueva solicitud local o actualizada localmente
)