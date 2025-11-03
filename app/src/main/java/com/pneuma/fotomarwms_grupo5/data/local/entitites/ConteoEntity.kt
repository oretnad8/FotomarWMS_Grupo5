package com.pneuma.fotomarwms_grupo5.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conteos")
data class ConteoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sku: String,
    val idUbicacion: Int, // Podría ser el serverId de la ubicación
    val cantidadFisica: Int,
    val timestamp: Long = System.currentTimeMillis(), // Para saber cuándo se hizo
    val needsSync: Boolean = true // Por defecto, necesita subirse
)