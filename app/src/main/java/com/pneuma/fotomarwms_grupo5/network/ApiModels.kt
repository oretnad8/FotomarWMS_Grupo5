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
    val codigoUbicacion: String, // Formato: P{pasillo}-{piso}-{numero}

    @SerializedName("pasillo")
    val pasillo: Int, // 1-5

    @SerializedName("piso")
    val piso: String, // A, B, C

    @SerializedName("numero")
    val numero: Int, // 1-60

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
    val observaciones: String? = null,
    val idAprobador: Int? = null
)

data class RechazarRequest(
    val observaciones: String?
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
