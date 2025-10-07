package com.pneuma.fotomarwms_grupo5.model

import java.util.Date

/**
 * Enumeración de roles de usuario en el sistema
 */
enum class UserRole {
    ADMIN,      // Crea usuarios, configuración del sistema
    JEFE,       // Aprueba sin restricciones, gestiona ubicaciones
    SUPERVISOR, // Aprueba pero notifica a jefe
    OPERADOR    // Solo solicita, requiere aprobación
}

/**
 * Representa un usuario del sistema WMS
 */
data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: UserRole,
    val activo: Boolean = true
)

/**
 * Representa un producto en el inventario
 * SKU formato: 2 letras + 5 números (ej: AP30001)
 */
data class Producto(
    val sku: String,                    // Código único del producto
    val descripcion: String,            // Descripción del producto
    val stock: Int = 0,                 // Cantidad actual en inventario
    val codigoBarrasIndividual: String? = null,  // Código de barras unitario
    val lpn: String? = null,            // License Plate Number (código de caja)
    val lpnDesc: String? = null,        // Descripción del LPN
    val fechaVencimiento: Date? = null, // Fecha de vencimiento (opcional)
    val vencimientoCercano: Boolean = false,     // Alerta si quedan <2 meses
    val ubicaciones: List<ProductoUbicacion> = emptyList() // Ubicaciones del producto
)

/**
 * Representa la relación N:M entre Producto y Ubicación
 */
data class ProductoUbicacion(
    val idUbicacion: Int,
    val codigoUbicacion: String,        // Ej: A13, B25
    val cantidadEnUbicacion: Int
)

/**
 * Representa una ubicación física en la bodega
 * Estructura: Piso (A-C) + Número (1-60)
 */
data class Ubicacion(
    val idUbicacion: Int,
    val codigoUbicacion: String,        // Ej: A13, B25
    val piso: Char,                     // A, B o C
    val numero: Int,                    // 1 a 60
    val productos: List<Producto> = emptyList()
)

/**
 * Estados posibles de una solicitud de aprobación
 */
enum class EstadoAprobacion {
    PENDIENTE,
    APROBADO,
    RECHAZADO
}

/**
 * Tipos de movimiento en bodega
 */
enum class TipoMovimiento {
    INGRESO,
    EGRESO,
    REUBICACION
}

/**
 * Representa una solicitud de movimiento que requiere aprobación
 */
data class Aprobacion(
    val id: Int,
    val tipoMovimiento: TipoMovimiento,
    val producto: Producto,
    val cantidad: Int,
    val motivo: String,
    val solicitante: Usuario,
    val fechaSolicitud: Date,
    val estado: EstadoAprobacion = EstadoAprobacion.PENDIENTE,
    val aprobador: Usuario? = null,
    val fechaAprobacion: Date? = null,
    val observaciones: String? = null,
    val ubicacionOrigen: Ubicacion? = null,
    val ubicacionDestino: Ubicacion? = null
)

/**
 * Representa un mensaje del jefe a operadores o notificación del sistema
 */
data class Mensaje(
    val id: Int,
    val emisor: Usuario,                // Jefe o Sistema
    val destinatario: Usuario? = null,  // null = mensaje para todos
    val titulo: String,
    val contenido: String,
    val fecha: Date,
    val leido: Boolean = false,
    val importante: Boolean = false     // Para marcar como urgente
)

/**
 * Representa una tarea asignada a un operador
 */
data class Tarea(
    val id: Int,
    val descripcion: String,
    val ubicacion: String? = null,
    val estado: EstadoTarea = EstadoTarea.PENDIENTE,
    val prioridad: PrioridadTarea = PrioridadTarea.NORMAL,
    val fechaAsignacion: Date,
    val asignadoA: Usuario
)

enum class EstadoTarea {
    PENDIENTE,
    EN_PROGRESO,
    COMPLETADA
}

enum class PrioridadTarea {
    BAJA,
    NORMAL,
    ALTA
}

/**
 * Representa una alerta del sistema
 */
data class Alerta(
    val tipo: TipoAlerta,
    val mensaje: String,
    val cantidad: Int? = null
)

enum class TipoAlerta {
    STOCK_BAJO,
    VENCIMIENTO_CERCANO,
    SOLICITUDES_PENDIENTES
}

/**
 * Datos de estadísticas para el dashboard
 */
data class Estadisticas(
    val usuariosActivos: Int = 0,
    val totalProductos: Int = 0,
    val solicitudesPendientes: Int = 0,
    val aprobadosHoy: Int = 0
)

/**
 * Representa el progreso de inventario por piso
 */
data class ProgresoPiso(
    val piso: String,
    val porcentaje: Float,
    val contados: Int,
    val total: Int
)

/**
 * Representa una diferencia encontrada durante el inventario
 */
data class DiferenciaInventario(
    val sku: String,
    val descripcion: String,
    val cantidadSistema: Int,
    val cantidadFisico: Int,
    val diferencia: Int,
    val ubicacion: String
)