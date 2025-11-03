package com.pneuma.fotomarwms_grupo5.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pneuma.fotomarwms_grupo5.models.TipoMensaje

@Entity(tableName = "mensajes")
data class MensajeEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverId: Int, // ID del servidor
    val titulo: String,
    val contenido: String,
    val importante: Boolean = false,
    val leido: Boolean = false,
    val idRemitente: Int?,
    val remitente: String,
    val idDestinatario: Int?,
    val destinatario: String?,
    val fecha: String,
    val tipo: TipoMensaje = TipoMensaje.NORMAL,
    val needsSync: Boolean = false // Para marcar como leído/no leído y sincronizar
)