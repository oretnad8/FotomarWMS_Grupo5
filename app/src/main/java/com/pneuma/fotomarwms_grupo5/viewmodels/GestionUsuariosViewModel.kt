package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import com.pneuma.fotomarwms_grupo5.model.UserRole
import com.pneuma.fotomarwms_grupo5.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la UI para gestión de usuarios
 */
data class GestionUsuariosUiState(
    val usuarios: List<Usuario> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel que gestiona la administración de usuarios
 *
 * Responsabilidades:
 * - Cargar lista de usuarios
 * - Crear nuevos usuarios
 * - Activar/desactivar usuarios
 * - Actualizar roles
 */
class GestionUsuariosViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GestionUsuariosUiState())
    val uiState: StateFlow<GestionUsuariosUiState> = _uiState.asStateFlow()

    init {
        cargarUsuarios()
    }

    /**
     * Carga todos los usuarios del sistema
     *
     * TODO: Conectar con API cuando esté disponible
     */
    private fun cargarUsuarios() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        // Usuarios de prueba
        val usuariosPrueba = listOf(
            Usuario(1, "Admin Principal", "admin@fotomar.cl", UserRole.ADMIN, true),
            Usuario(2, "Jefe de Bodega", "jefe@fotomar.cl", UserRole.JEFE, true),
            Usuario(3, "Supervisor García", "supervisor@fotomar.cl", UserRole.SUPERVISOR, true),
            Usuario(4, "Juan Operador", "operador1@fotomar.cl", UserRole.OPERADOR, true),
            Usuario(5, "María Operadora", "operador2@fotomar.cl", UserRole.OPERADOR, true),
            Usuario(6, "Pedro Operador", "operador3@fotomar.cl", UserRole.OPERADOR, false)
        )

        _uiState.value = GestionUsuariosUiState(
            usuarios = usuariosPrueba,
            isLoading = false
        )
    }

    /**
     * Crea un nuevo usuario
     *
     * @param nombre Nombre del usuario
     * @param email Email del usuario
     * @param rol Rol asignado
     */
    fun crearUsuario(nombre: String, email: String, rol: UserRole) {
        // TODO: Enviar solicitud al backend

        // Simulación de creación
        val nuevoUsuario = Usuario(
            id = _uiState.value.usuarios.size + 1,
            nombre = nombre,
            email = email,
            rol = rol,
            activo = true
        )

        _uiState.value = _uiState.value.copy(
            usuarios = _uiState.value.usuarios + nuevoUsuario
        )
    }

    /**
     * Activa o desactiva un usuario
     *
     * @param usuario Usuario a modificar
     */
    fun toggleUsuarioActivo(usuario: Usuario) {
        // TODO: Enviar actualización al backend

        val usuariosActualizados = _uiState.value.usuarios.map {
            if (it.id == usuario.id) {
                it.copy(activo = !it.activo)
            } else {
                it
            }
        }

        _uiState.value = _uiState.value.copy(usuarios = usuariosActualizados)
    }

    /**
     * Refresca la lista de usuarios
     */
    fun refresh() {
        cargarUsuarios()
    }
}