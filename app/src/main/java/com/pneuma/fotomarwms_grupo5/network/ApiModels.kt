package com.pneuma.fotomarwms_grupo5.network

import com.google.gson.annotations.SerializedName

// ========== AUTH MODELS ==========

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val type: String,
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String
)

// ========== USUARIO MODELS ==========

data class UsuarioRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String // "ADMIN", "JEFE", "SUPERVISOR", "OPERADOR"
)

data class UsuarioResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val activo: Boolean
)

// ========== PRODUCTO MODELS ==========

data class ProductoRequest(
    val sku: String,
    val descripcion: String,
    val stock: Int,
    val codigoBarrasIndividual: String?,
    val lpn: String?,
    val lpnDesc: String?,
    val fechaVencimiento: String?
)

data class ProductoResponse(
    val sku: String,
    val descripcion: String,
    val stock: Int,
    val codigoBarrasIndividual: String?,
    val lpn: String?,
    val lpnDesc: String?,
    val fechaVencimiento: String?,
    val vencimientoCercano: Boolean = false,
    val ubicaciones: List<ProductoUbicacionResponse>? = null
)

data class ProductoUbicacionResponse(
    val idUbicacion: Int,
    val codigoUbicacion: String,
    val cantidad: Int
)

// ========== UBICACION MODELS ==========

data class UbicacionResponse(
    // La anotación @SerializedName le dice a Gson cómo mapear el JSON a tu variable.
    // Es la forma más segura de evitar errores de nombres.
    @SerializedName("idUbicacion")
    val idUbicacion: Int,

    @SerializedName("codigoUbicacion")
    val codigoUbicacion: String, // El campo ahora coincide con el JSON

    @SerializedName("piso")
    val piso: String,

    @SerializedName("numero")
    val numero: Int,

    // Es buena práctica definir todos los campos que vienen en el JSON
    @SerializedName("productos")
    val productos: List<ProductoEnUbicacion>?,

    @SerializedName("totalProductos")
    val totalProductos: Int,

    @SerializedName("cantidadTotal")
    val cantidadTotal: Int
)

// NUEVA data class para los productos anidados dentro de una ubicación.
// Esto es necesario para que Gson pueda parsear la lista "productos".
data class ProductoEnUbicacion(
    @SerializedName("sku")
    val sku: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("cantidadEnUbicacion")
    val cantidadEnUbicacion: Int
)

data class AsignarUbicacionRequest(
    val sku: String,
    val codigoUbicacion: String,
    val cantidad: Int
)

// ========== APROBACION MODELS ==========

data class AprobacionRequest(
    val tipoMovimiento: String, // "INGRESO", "EGRESO", "REUBICACION"
    val sku: String,
    val cantidad: Int,
    val motivo: String,
    val idUbicacionOrigen: Int? = null,
    val idUbicacionDestino: Int? = null
)

data class AprobacionResponse(
    val id: Int,
    val tipoMovimiento: String,
    val sku: String,
    val cantidad: Int,
    val motivo: String,
    val estado: String, // "PENDIENTE", "APROBADO", "RECHAZADO"
    val idUbicacionOrigen: Int?,
    val idUbicacionDestino: Int?,
    val fechaSolicitud: String,
    val observaciones: String?
)

data class AprobarRequest(
    val observaciones: String?
)

data class RechazarRequest(
    val observaciones: String?
)

// ========== MENSAJE MODELS ==========

data class MensajeRequest(
    val idDestinatario: Int?,
    val titulo: String,
    val contenido: String,
    val importante: Boolean
)

data class MensajeResponse(
    val id: Int,
    val idRemitente: Int,
    val nombreRemitente: String,
    val idDestinatario: Int?,
    val titulo: String,
    val contenido: String,
    val importante: Boolean,
    val leido: Boolean,
    val fechaEnvio: String
)

data class ResumenMensajesResponse(
    val totalMensajes: Int,
    val mensajesNoLeidos: Int,
    val mensajesImportantes: Int
)

// ========== INVENTARIO MODELS ==========

data class ConteoRequest(
    val sku: String,
    val idUbicacion: Int,
    val cantidadFisica: Int
)

data class ProgresoInventarioResponse(
    val totalUbicaciones: Int,
    val ubicacionesContadas: Int,
    val ubicacionesPendientes: Int,
    val porcentajeCompletado: Double,
    val totalDiferenciasRegistradas: Int,
    val totalFaltantes: Int,
    val totalSobrantes: Int,
    val ubicacionesConDiferencias: Int
)

data class DiferenciaInventarioResponse(
    val sku: String,
    val descripcion: String,
    val idUbicacion: Int,
    val codigoUbicacion: String,
    val cantidadSistema: Int,
    val cantidadFisica: Int,
    val diferencia: Int
)

// ========== GENERIC API RESPONSE ==========

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class ApiError(
    val error: String,
    val message: String,
    val status: Int
)
