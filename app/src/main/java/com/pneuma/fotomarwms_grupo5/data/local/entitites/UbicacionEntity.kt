package com.pneuma.fotomarwms_grupo5.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ubicaciones")
data class UbicacionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverId: Int, // ID del servidor
    val codigoUbicacion: String,
    val piso: Char,
    val numero: Int,
    val needsSync: Boolean = false
    // Nota: La lista de productos aquí también requiere una tabla de relación.
)