package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para autenticación y gestión de sesión
 * Maneja login, logout, validación de token y estado del usuario actual
 */
class AuthViewModel : ViewModel() {

    // ========== ESTADO DE AUTENTICACIÓN ==========

    private val _authState = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    // ========== FUNCIONES DE AUTENTICACIÓN ==========

    /**
     * Realiza el login con email y password
     * Conecta con: POST /api/auth/login
     * @param email Email del usuario
     * @param password Contraseña del usuario
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // TODO: Conectar con backend
                // val response = authRepository.login(LoginRequest(email, password))

                // MOCK TEMPORAL - Simula respuesta del backend
                kotlinx.coroutines.delay(1000) // Simula latencia de red

                // Simulación de diferentes usuarios según email
                val mockResponse = when {
                    email.contains("admin") -> LoginResponse(
                        token = "mock-token-admin-123",
                        type = "Bearer",
                        id = 1,
                        nombre = "Admin Sistema",
                        email = email,
                        rol = "ADMIN"
                    )
                    email.contains("jefe") -> LoginResponse(
                        token = "mock-token-jefe-456",
                        type = "Bearer",
                        id = 2,
                        nombre = "Jefe de Bodega",
                        email = email,
                        rol = "JEFE"
                    )
                    email.contains("supervisor") -> LoginResponse(
                        token = "mock-token-supervisor-789",
                        type = "Bearer",
                        id = 3,
                        nombre = "Supervisor Bodega",
                        email = email,
                        rol = "SUPERVISOR"
                    )
                    else -> LoginResponse(
                        token = "mock-token-operador-999",
                        type = "Bearer",
                        id = 4,
                        nombre = "Operador Bodega",
                        email = email,
                        rol = "OPERADOR"
                    )
                }

                // Guardar token y usuario
                _authToken.value = mockResponse.token
                _currentUser.value = Usuario(
                    id = mockResponse.id,
                    nombre = mockResponse.nombre,
                    email = mockResponse.email,
                    rol = Rol.valueOf(mockResponse.rol),
                    activo = true
                )

                _authState.value = AuthState.Authenticated(
                    usuario = _currentUser.value!!,
                    token = mockResponse.token
                )

                // TODO: Guardar token en SharedPreferences o DataStore

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    message = e.message ?: "Error desconocido en el login"
                )
            }
        }
    }

    /**
     * Cierra la sesión del usuario actual
     * Conecta con: POST /api/auth/logout
     */
    fun logout() {
        viewModelScope.launch {
            try {
                // TODO: Llamar al backend para invalidar el token
                // authRepository.logout(token = _authToken.value)

                // Limpiar estado local
                _authToken.value = null
                _currentUser.value = null
                _authState.value = AuthState.NotAuthenticated

                // TODO: Limpiar token de SharedPreferences o DataStore

            } catch (e: Exception) {
                // Incluso si falla, limpiar sesión local
                _authToken.value = null
                _currentUser.value = null
                _authState.value = AuthState.NotAuthenticated
            }
        }
    }

    /**
     * Valida el token JWT almacenado
     * Conecta con: GET /api/auth/validate
     * Se ejecuta al iniciar la app para verificar si hay sesión activa
     */
    fun validateToken() {
        viewModelScope.launch {
            try {
                // TODO: Recuperar token de SharedPreferences o DataStore
                val storedToken = _authToken.value

                if (storedToken == null) {
                    _authState.value = AuthState.NotAuthenticated
                    return@launch
                }

                _authState.value = AuthState.Loading

                // TODO: Validar token con backend
                // val isValid = authRepository.validateToken(storedToken)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)
                val isValid = true // Simular token válido

                if (isValid) {
                    // Token válido, recuperar info del usuario
                    // TODO: En producción, el backend debería devolver los datos del usuario
                    _authState.value = AuthState.Authenticated(
                        usuario = _currentUser.value ?: Usuario(
                            id = 1,
                            nombre = "Usuario Mock",
                            email = "mock@fotomar.cl",
                            rol = Rol.OPERADOR
                        ),
                        token = storedToken
                    )
                } else {
                    _authState.value = AuthState.NotAuthenticated
                }

            } catch (e: Exception) {
                _authState.value = AuthState.NotAuthenticated
            }
        }
    }

    /**
     * Verifica si el usuario actual tiene un rol específico
     * @param requiredRole Rol requerido para la acción
     * @return true si el usuario tiene el rol requerido
     */
    fun hasRole(requiredRole: Rol): Boolean {
        return _currentUser.value?.rol == requiredRole
    }

    /**
     * Verifica si el usuario actual tiene permisos de jefe o superior
     * @return true si es ADMIN, JEFE o SUPERVISOR
     */
    fun isJefeOrAbove(): Boolean {
        val rol = _currentUser.value?.rol
        return rol == Rol.ADMIN || rol == Rol.JEFE || rol == Rol.SUPERVISOR
    }

    /**
     * Obtiene el header de autorización para las peticiones
     * @return String con formato "Bearer {token}"
     */
    fun getAuthHeader(): String? {
        return _authToken.value?.let { "Bearer $it" }
    }

    /**
     * Limpia el estado de error para poder reintentar login
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.NotAuthenticated
        }
    }
}