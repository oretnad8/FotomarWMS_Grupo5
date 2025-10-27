package com.pneuma.fotomarwms_grupo5.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mensajes_pendientes") // Nombre de la tabla
data class MensajeLocal(
    @PrimaryKey(autoGenerate = true)
    val idLocal: Long = 0, // ID único local

    // Datos del mensaje a enviar (basado en MensajeRequest)
    val idDestinatario: Int?, // Null si es broadcast
    val titulo: String,
    val contenido: String,
    val importante: Boolean,

    val timestamp: Long = System.currentTimeMillis() // Hora de guardado local
)