package com.pneuma.fotomarwms_grupo5.network.models

// Para POST /api/usuarios
data class UsuarioRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String // Enviar como String: "ADMIN", "JEFE", "SUPERVISOR", "OPERADOR"
)

// Para PUT /api/usuarios/{id}
data class UsuarioUpdateRequest(
    val nombre: String? = null,
    val email: String? = null,
    val password: String? = null,
    val rol: String? = null
)