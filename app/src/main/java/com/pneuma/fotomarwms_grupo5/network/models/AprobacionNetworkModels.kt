package com.pneuma.fotomarwms_grupo5.network.models

// Para POST /api/aprobaciones
data class AprobacionRequest(
    val tipoMovimiento: String, // "INGRESO", "EGRESO", "REUBICACION"
    val sku: String,
    val cantidad: Int,
    val motivo: String,
    val idUbicacionOrigen: Int? = null,
    val idUbicacionDestino: Int? = null
)

// Para PUT /api/aprobaciones/{id}/aprobar
data class AprobarRequest(
    val observaciones: String?
)

// Para PUT /api/aprobaciones/{id}/rechazar
data class RechazarRequest(
    val observaciones: String
)