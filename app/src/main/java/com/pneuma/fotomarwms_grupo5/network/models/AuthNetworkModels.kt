package com.pneuma.fotomarwms_grupo5.network.models

// Para POST /api/auth/login
data class LoginRequest(
    val email: String,
    val password: String
)

// Respuesta de POST /api/auth/login
data class LoginResponse(
    val token: String,
    val type: String,
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String // Se recibe como String, se debe convertir a Enum en el ViewModel
)

// Respuesta de GET /api/auth/validate
data class ValidateTokenResponse(
    val valid: Boolean,
    val userId: Int,
    val email: String,
    val rol: String,
    val message: String
)