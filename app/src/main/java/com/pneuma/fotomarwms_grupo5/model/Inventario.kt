package com.pneuma.fotomarwms_grupo5.models

/**
 * Modelo de datos para el progreso del inventario
 * Muestra el estado actual del conteo físico vs sistema
 */
data class ProgresoInventario(
    val totalUbicaciones: Int,           // Total de ubicaciones en bodega (180)
    val ubicacionesContadas: Int,        // Ubicaciones ya contadas
    val ubicacionesPendientes: Int,      // Ubicaciones faltantes por contar
    val porcentajeCompletado: Double,    // % de completitud (0.0 - 100.0)
    val totalDiferenciasRegistradas: Int, // Total de registros con diferencias
    val totalFaltantes: Int,             // Total de unidades faltantes
    val totalSobrantes: Int,             // Total de unidades sobrantes
    val ubicacionesConDiferencias: Int   // Cantidad de ubicaciones con diferencias
)

/**
 * Request para registrar un conteo físico
 * Se registra la cantidad física encontrada en una ubicación específica
 */
data class ConteoRequest(
    val sku: String,
    val idUbicacion: Int,
    val cantidadFisica: Int // Cantidad real encontrada físicamente
)

/**
 * Modelo para una diferencia de inventario
 * Compara cantidad sistema vs cantidad física
 */
data class DiferenciaInventario(
    val id: Int,
    val sku: String,
    val descripcionProducto: String,
    val idUbicacion: Int,
    val codigoUbicacion: String,
    val cantidadSistema: Int,    // Lo que dice el sistema
    val cantidadFisica: Int,     // Lo que se encontró físicamente
    val diferencia: Int,          // Diferencia (físico - sistema)
    val tipoDiferencia: TipoDiferencia,
    val fechaConteo: String
)

/**
 * Enum para tipo de diferencia
 */
enum class TipoDiferencia {
    FALTANTE,  // Hay menos de lo que dice el sistema (diferencia negativa)
    SOBRANTE,  // Hay más de lo que dice el sistema (diferencia positiva)
    CORRECTO   // Coincide exactamente
}