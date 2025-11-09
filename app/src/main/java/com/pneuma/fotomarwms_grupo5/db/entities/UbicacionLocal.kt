package com.pneuma.fotomarwms_grupo5.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad local para cachear ubicaciones
 * Permite acceso offline a la lista de ubicaciones
 */
@Entity(tableName = "ubicaciones_cache")
data class UbicacionLocal(
    @PrimaryKey
    val codigo: String, // ej: "A-12", "B-45", "C-60"
    val piso: String, // "A", "B", "C"
    val numero: Int, // 1-60
    val timestamp: Long = System.currentTimeMillis()
)
