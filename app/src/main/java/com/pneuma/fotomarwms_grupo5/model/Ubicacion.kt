package com.pneuma.fotomarwms_grupo5.models

/**
 * Modelo de datos para Ubicación en bodega
 * Representa una posición física donde se almacenan productos
 * Estructura actualizada: 5 pasillos × 60 posiciones × 3 pisos = 900 ubicaciones totales
 * Formato de código: P{pasillo}-{piso}-{numero} (ej: P1-A-15, P3-B-42, P5-C-01)
 */
data class Ubicacion(
    val idUbicacion: Int,
    val codigoUbicacion: String, // Formato: P{pasillo}-{piso}-{numero} (ej: P1-A-12, P3-B-45)
    val pasillo: Int, // 1 a 5
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
    val codigoUbicacion: String, // Formato: P{pasillo}-{piso}-{numero}
    val cantidad: Int
)

/**
 * Enum para los pisos de la bodega
 */
enum class Piso(val codigo: Char, val label: String) {
    A('A', "Piso A"),
    B('B', "Piso B"),
    C('C', "Piso C");

    companion object {
        fun fromChar(c: Char): Piso? = values().find { it.codigo == c }
    }
}

/**
 * Enum para los pasillos de la bodega
 */
enum class Pasillo(val numero: Int, val label: String) {
    P1(1, "Pasillo 1"),
    P2(2, "Pasillo 2"),
    P3(3, "Pasillo 3"),
    P4(4, "Pasillo 4"),
    P5(5, "Pasillo 5");

    companion object {
        fun fromNumero(n: Int): Pasillo? = values().find { it.numero == n }
    }
}

/**
 * Utilidad para formatear códigos de ubicación
 */
object UbicacionFormatter {
    /**
     * Formatea un código de ubicación al formato estándar P{pasillo}-{piso}-{numero}
     * Ejemplo: formatCodigo(1, 'A', 5) -> "P1-A-05"
     */
    fun formatCodigo(pasillo: Int, piso: Char, numero: Int): String {
        return "P$pasillo-$piso-${numero.toString().padStart(2, '0')}"
    }

    /**
     * Parsea un código escaneado (formato P1/A1) al formato estándar P1-A-01
     * Ejemplo: parseScannedCode("P1/A1") -> "P1-A-01"
     */
    fun parseScannedCode(scannedCode: String): String? {
        // Formato esperado: P{pasillo}/{piso}{numero}
        // Ejemplo: P1/A1, P3/B25, P5/C60
        val regex = Regex("""P(\d)/([ABC])(\d+)""")
        val match = regex.matchEntire(scannedCode.trim().uppercase())
        
        return match?.let {
            val pasillo = it.groupValues[1].toInt()
            val piso = it.groupValues[2][0]
            val numero = it.groupValues[3].toInt()
            
            // Validar rangos
            if (pasillo in 1..5 && piso in listOf('A', 'B', 'C') && numero in 1..60) {
                formatCodigo(pasillo, piso, numero)
            } else {
                null
            }
        }
    }

    /**
     * Extrae los componentes de un código de ubicación
     * Retorna Triple(pasillo, piso, numero) o null si el formato es inválido
     */
    fun parseCodigo(codigo: String): Triple<Int, Char, Int>? {
        val regex = Regex("""P(\d)-([ABC])-(\d+)""")
        val match = regex.matchEntire(codigo.trim().uppercase())
        
        return match?.let {
            val pasillo = it.groupValues[1].toInt()
            val piso = it.groupValues[2][0]
            val numero = it.groupValues[3].toInt()
            Triple(pasillo, piso, numero)
        }
    }
}
