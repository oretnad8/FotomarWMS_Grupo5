package com.pneuma.fotomarwms_grupo5.models

/**
 * Modelo de datos para Ubicación en bodega
 * Representa una posición física donde se almacenan productos
 * Estructura: 3 pisos (A, B, C) × 60 posiciones = 180 ubicaciones totales
 */
data class Ubicacion(
    val idUbicacion: Int,
    val codigoUbicacion: String, // Formato: LETRA-NUMERO (ej: A-12, B-45, C-03)
    val piso: Char, // 'A', 'B', o 'C'
    val numero: Int, // 1 a 60
    val productos: List<ProductoEnUbicacion>? = null // Productos en esta ubicación
)

/**
 * Producto almacenado en una ubicación específica
 */
data class ProductoEnUbicacion(
    val sku: String,
    val descripcion: String,
    val cantidad: Int
)

/**
 * Request para asignar un producto a una ubicación
 * Usado tanto por jefe (directo) como por operador (requiere aprobación)
 */
data class AsignarUbicacionRequest(
    val sku: String,
    val codigoUbicacion: String,
    val cantidad: Int
)

/**
 * Enum para los pisos de la bodega
 */
enum class Piso(val codigo: Char) {
    A('A'),
    B('B'),
    C('C');

    companion object {
        fun fromChar(c: Char): Piso? = values().find { it.codigo == c }
    }
}