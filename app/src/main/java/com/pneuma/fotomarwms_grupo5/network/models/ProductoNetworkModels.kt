package com.pneuma.fotomarwms_grupo5.network.models

// Para POST /api/productos
data class ProductoRequest(
    val sku: String,
    val descripcion: String,
    val stock: Int,
    val codigoBarrasIndividual: String?,
    val lpn: String?,
    val lpnDesc: String?,
    val fechaVencimiento: String? // Formato "AAAA-MM-DD" o null
)

// Para PUT /api/productos/{sku}
data class ProductoUpdateRequest(
    val descripcion: String?,
    val stock: Int?,
    val codigoBarrasIndividual: String?,
    val lpn: String?,
    val lpnDesc: String?,
    val fechaVencimiento: String? // Formato "AAAA-MM-DD" o null
)