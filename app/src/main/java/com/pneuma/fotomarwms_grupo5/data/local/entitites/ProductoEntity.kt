package com.pneuma.fotomarwms_grupo5.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val sku: String, // Usamos SKU como clave primaria
    val descripcion: String,
    val stock: Int,
    val codigoBarrasIndividual: String?,
    val lpn: String?,
    val lpnDesc: String?,
    val fechaVencimiento: String?,
    val vencimientoCercano: Boolean = false,
    val needsSync: Boolean = false
    // Nota: Las ubicaciones (List<ProductoUbicacion>) requerirán una tabla de relación separada.
    // Por simplicidad inicial, omitiremos la relación directa aquí.
)