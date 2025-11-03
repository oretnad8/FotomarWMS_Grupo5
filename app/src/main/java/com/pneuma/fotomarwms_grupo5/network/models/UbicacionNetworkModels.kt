package com.pneuma.fotomarwms_grupo5.network.models

// Para POST /api/ubicaciones/asignar
data class AsignarProductoRequest(
    val sku: String,
    val codigoUbicacion: String,
    val cantidad: Int
)

// Respuesta de POST /api/ubicaciones/asignar
data class AsignarProductoResponse(
    val mensaje: String,
    val sku: String,
    val codigoUbicacion: String,
    val cantidadAsignada: Int,
    val stockRestante: Int
)