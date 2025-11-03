package com.pneuma.fotomarwms_grupo5.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pneuma.fotomarwms_grupo5.models.Rol

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0, // Clave primaria local
    val serverId: Int, // ID del servidor (importante para sincronización)
    val nombre: String,
    val email: String,
    val rol: Rol,
    val activo: Boolean = true,
    val needsSync: Boolean = false // Indica si hay cambios locales pendientes de subir
) {
    // Puedes añadir un constructor secundario o función para convertir desde tu modelo de red
}