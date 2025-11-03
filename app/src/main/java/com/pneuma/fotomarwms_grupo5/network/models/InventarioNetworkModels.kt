package com.pneuma.fotomarwms_grupo5.network.models

import java.time.LocalDateTime

// Respuesta de GET /api/inventario/progreso
data class ProgresoResponse(
    val totalUbicaciones: Long,
    val ubicacionesContadas: Long,
    val ubicacionesPendientes: Long,
    val porcentajeCompletado: Double,
    val totalDiferenciasRegistradas: Long,
    val totalFaltantes: Long,
    val totalSobrantes: Long,
    val ubicacionesConDiferencias: Long
)

// Para POST /api/inventario/conteo
data class ConteoRequest(
    val sku: String,
    val idUbicacion: Int,
    val cantidadFisica: Int
)

// Respuesta de POST /api/inventario/conteo
data class ConteoResponse(
    val mensaje: String,
    val sku: String,
    val codigoUbicacion: String,
    val cantidadSistema: Int,
    val cantidadFisica: Int,
    val diferencia: Int,
    val hayDiferencia: Boolean,
    val tipoAlerta: String
)

// Respuesta de GET /api/inventario/diferencias
data class DiferenciaResponse(
    val id: Int,
    val sku: String,
    val descripcionProducto: String,
    val idUbicacion: Int,
    val codigoUbicacion: String,
    val cantidadSistema: Int,
    val cantidadFisica: Int,
    val diferencia: Int,
    val tipoDiferencia: String,
    val fechaRegistro: String, // Recibido como String
    val nombreRegistrador: String
)

// Respuesta de POST /api/inventario/finalizar
data class FinalizarInventarioResponse(
    val mensaje: String,
    val fechaFinalizacion: String, // Recibido como String
    val totalDiferencias: Long,
    val totalFaltantes: Long,
    val totalSobrantes: Long,
    val productosAjustados: Int,
    val estado: String
)