package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import com.pneuma.fotomarwms_grupo5.model.UserRole
import com.pneuma.fotomarwms_grupo5.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la UI para el perfil del usuario
 */
data class PerfilUiState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = false
)

/**
 * ViewModel que gestiona el perfil del usuario
 *
 * Responsabilidades:
 * - Cargar información del usuario actual
 * - Actualizar datos del perfil
 * - Cerrar sesión
 */
class PerfilViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    /**
     * Carga la información del usuario actual
     *
     * TODO: Conectar con API y usar sesión real
     */
    private fun cargarPerfil() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        // Usuario de prueba (debería venir de la sesión)
        val usuarioPrueba = Usuario(
            id = 4,
            nombre = "Juan Operador",
            email = "operador@fotomar.cl",
            rol = UserRole.OPERADOR,
            activo = true
        )

        _uiState.value = PerfilUiState(
            usuario = usuarioPrueba,
            isLoading = false
        )
    }

    /**
     * Actualiza la información del perfil
     *
     * @param nombre Nuevo nombre
     * @param email Nuevo email
     */
    fun actualizarPerfil(nombre: String, email: String) {
        // TODO: Enviar actualización al backend
    }

    /**
     * Cambia la contraseña del usuario
     *
     * @param passwordActual Contraseña actual
     * @param passwordNueva Nueva contraseña
     */
    fun cambiarPassword(passwordActual: String, passwordNueva: String) {
        // TODO: Enviar cambio de contraseña al backend
    }
}