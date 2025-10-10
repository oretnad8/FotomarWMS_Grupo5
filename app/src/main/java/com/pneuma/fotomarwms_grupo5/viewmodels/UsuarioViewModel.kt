package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de usuarios
 * Solo para rol ADMIN
 * Maneja creación, actualización, eliminación y activación/desactivación de usuarios
 */
class UsuarioViewModel : ViewModel() {

    // ========== ESTADOS DE USUARIOS ==========

    private val _usuariosState = MutableStateFlow<UiState<List<Usuario>>>(UiState.Idle)
    val usuariosState: StateFlow<UiState<List<Usuario>>> = _usuariosState.asStateFlow()

    private val _selectedUsuario = MutableStateFlow<Usuario?>(null)
    val selectedUsuario: StateFlow<Usuario?> = _selectedUsuario.asStateFlow()

    private val _usuarioDetailState = MutableStateFlow<UiState<Usuario>>(UiState.Idle)
    val usuarioDetailState: StateFlow<UiState<Usuario>> = _usuarioDetailState.asStateFlow()

    private val _createUsuarioState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val createUsuarioState: StateFlow<UiState<Boolean>> = _createUsuarioState.asStateFlow()

    private val _updateUsuarioState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val updateUsuarioState: StateFlow<UiState<Boolean>> = _updateUsuarioState.asStateFlow()

    private val _deleteUsuarioState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val deleteUsuarioState: StateFlow<UiState<Boolean>> = _deleteUsuarioState.asStateFlow()

    private val _rolFiltro = MutableStateFlow<Rol?>(null)
    val rolFiltro: StateFlow<Rol?> = _rolFiltro.asStateFlow()

    // ========== CONSULTA DE USUARIOS ==========

    /**
     * Obtiene todos los usuarios del sistema
     * Conecta con: GET /api/usuarios
     * Solo para ADMIN
     */
    fun getAllUsuarios() {
        viewModelScope.launch {
            try {
                _usuariosState.value = UiState.Loading

                // TODO: Conectar con backend
                // val usuarios = usuarioRepository.getAll()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(600)

                val mockUsuarios = generateMockUsuarios()
                _usuariosState.value = UiState.Success(mockUsuarios)

            } catch (e: Exception) {
                _usuariosState.value = UiState.Error(
                    message = "Error al obtener usuarios: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene un usuario específico por ID
     * Conecta con: GET /api/usuarios/{id}
     * Solo para ADMIN
     * @param id ID del usuario
     */
    fun getUsuarioById(id: Int) {
        viewModelScope.launch {
            try {
                _usuarioDetailState.value = UiState.Loading

                // TODO: Conectar con backend
                // val usuario = usuarioRepository.getById(id)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(400)

                val mockUsuario = Usuario(
                    id = id,
                    nombre = "Usuario Ejemplo $id",
                    email = "usuario$id@fotomar.cl",
                    rol = Rol.OPERADOR,
                    activo = true
                )

                _selectedUsuario.value = mockUsuario
                _usuarioDetailState.value = UiState.Success(mockUsuario)

            } catch (e: Exception) {
                _usuarioDetailState.value = UiState.Error(
                    message = "Error al obtener usuario: ${e.message}"
                )
            }
        }
    }

    /**
     * Filtra usuarios por rol
     * @param rol Rol para filtrar
     */
    fun getUsuariosByRol(rol: Rol) {
        viewModelScope.launch {
            try {
                _usuariosState.value = UiState.Loading
                _rolFiltro.value = rol

                // TODO: Puede ser filtro local o endpoint específico

                // MOCK TEMPORAL - Filtro local
                kotlinx.coroutines.delay(300)

                val mockUsuarios = generateMockUsuarios().filter { it.rol == rol }
                _usuariosState.value = UiState.Success(mockUsuarios)

            } catch (e: Exception) {
                _usuariosState.value = UiState.Error(
                    message = "Error al filtrar usuarios: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene solo usuarios activos
     */
    fun getUsuariosActivos() {
        viewModelScope.launch {
            try {
                _usuariosState.value = UiState.Loading

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(300)

                val mockUsuarios = generateMockUsuarios().filter { it.activo }
                _usuariosState.value = UiState.Success(mockUsuarios)

            } catch (e: Exception) {
                _usuariosState.value = UiState.Error(
                    message = "Error al obtener usuarios activos: ${e.message}"
                )
            }
        }
    }

    // ========== CREAR USUARIO ==========

    /**
     * Crea un nuevo usuario en el sistema
     * Conecta con: POST /api/usuarios
     * Solo para ADMIN
     * @param nombre Nombre completo del usuario
     * @param email Email del usuario (debe ser único)
     * @param password Contraseña inicial
     * @param rol Rol del usuario (ADMIN, JEFE, SUPERVISOR, OPERADOR)
     */
    fun createUsuario(
        nombre: String,
        email: String,
        password: String,
        rol: String
    ) {
        viewModelScope.launch {
            try {
                _createUsuarioState.value = UiState.Loading

                val request = UsuarioRequest(
                    nombre = nombre,
                    email = email,
                    password = password,
                    rol = rol
                )

                // TODO: Conectar con backend
                // usuarioRepository.createUsuario(request)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                _createUsuarioState.value = UiState.Success(true)

                // Recargar lista de usuarios
                getAllUsuarios()

            } catch (e: Exception) {
                _createUsuarioState.value = UiState.Error(
                    message = "Error al crear usuario: ${e.message}"
                )
            }
        }
    }

    // ========== ACTUALIZAR USUARIO ==========

    /**
     * Actualiza un usuario existente
     * Conecta con: PUT /api/usuarios/{id}
     * Solo para ADMIN
     * @param id ID del usuario a actualizar
     * @param nombre Nuevo nombre (opcional)
     * @param email Nuevo email (opcional)
     * @param password Nueva contraseña (opcional)
     * @param rol Nuevo rol (opcional)
     */
    fun updateUsuario(
        id: Int,
        nombre: String?,
        email: String?,
        password: String?,
        rol: String?
    ) {
        viewModelScope.launch {
            try {
                _updateUsuarioState.value = UiState.Loading

                // Crear request solo con campos no nulos
                val request = UsuarioRequest(
                    nombre = nombre ?: "",
                    email = email ?: "",
                    password = password ?: "",
                    rol = rol ?: ""
                )

                // TODO: Conectar con backend
                // usuarioRepository.updateUsuario(id, request)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                _updateUsuarioState.value = UiState.Success(true)

                // Recargar usuario
                getUsuarioById(id)

            } catch (e: Exception) {
                _updateUsuarioState.value = UiState.Error(
                    message = "Error al actualizar usuario: ${e.message}"
                )
            }
        }
    }

    // ========== ELIMINAR USUARIO ==========

    /**
     * Elimina un usuario del sistema
     * Conecta con: DELETE /api/usuarios/{id}
     * Solo para ADMIN
     * @param id ID del usuario a eliminar
     */
    fun deleteUsuario(id: Int) {
        viewModelScope.launch {
            try {
                _deleteUsuarioState.value = UiState.Loading

                // TODO: Conectar con backend
                // usuarioRepository.deleteUsuario(id)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                _deleteUsuarioState.value = UiState.Success(true)

                // Recargar lista de usuarios
                getAllUsuarios()

            } catch (e: Exception) {
                _deleteUsuarioState.value = UiState.Error(
                    message = "Error al eliminar usuario: ${e.message}"
                )
            }
        }
    }

    // ========== ACTIVAR/DESACTIVAR USUARIO ==========

    /**
     * Activa o desactiva un usuario
     * Conecta con: PUT /api/usuarios/{id}/toggle-activo
     * Solo para ADMIN
     * @param id ID del usuario
     */
    fun toggleActivoUsuario(id: Int) {
        viewModelScope.launch {
            try {
                _updateUsuarioState.value = UiState.Loading

                // TODO: Conectar con backend
                // usuarioRepository.toggleActivo(id)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(400)

                _updateUsuarioState.value = UiState.Success(true)

                // Recargar lista
                getAllUsuarios()

            } catch (e: Exception) {
                _updateUsuarioState.value = UiState.Error(
                    message = "Error al cambiar estado del usuario: ${e.message}"
                )
            }
        }
    }

    // ========== VALIDACIONES ==========

    /**
     * Valida que el email tenga formato correcto
     * @param email Email a validar
     * @return true si es válido
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return emailRegex.matches(email)
    }

    /**
     * Valida que la contraseña cumpla requisitos mínimos
     * @param password Contraseña a validar
     * @return true si es válida (mínimo 6 caracteres)
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Valida que el nombre no esté vacío
     * @param nombre Nombre a validar
     * @return true si es válido
     */
    fun isValidNombre(nombre: String): Boolean {
        return nombre.isNotBlank() && nombre.length >= 3
    }

    // ========== UTILIDADES ==========

    /**
     * Obtiene una lista de todos los roles disponibles
     * @return Lista de roles
     */
    fun getAllRoles(): List<Rol> {
        return Rol.values().toList()
    }

    /**
     * Convierte un rol a string en español
     * @param rol Rol a convertir
     * @return Nombre del rol en español
     */
    fun getRolDisplayName(rol: Rol): String {
        return when (rol) {
            Rol.ADMIN -> "Administrador"
            Rol.JEFE -> "Jefe de Bodega"
            Rol.SUPERVISOR -> "Supervisor"
            Rol.OPERADOR -> "Operador"
        }
    }

    /**
     * Limpia los estados de operaciones
     */
    fun clearCreateState() {
        _createUsuarioState.value = UiState.Idle
    }

    fun clearUpdateState() {
        _updateUsuarioState.value = UiState.Idle
    }

    fun clearDeleteState() {
        _deleteUsuarioState.value = UiState.Idle
    }

    /**
     * Limpia el filtro de rol
     */
    fun clearRolFilter() {
        _rolFiltro.value = null
        getAllUsuarios()
    }

    /**
     * Limpia el usuario seleccionado
     */
    fun clearSelectedUsuario() {
        _selectedUsuario.value = null
        _usuarioDetailState.value = UiState.Idle
    }

    // ========== MOCK DATA HELPER ==========

    private fun generateMockUsuarios(): List<Usuario> {
        return listOf(
            Usuario(
                id = 1,
                nombre = "Admin Sistema",
                email = "admin@fotomar.cl",
                rol = Rol.ADMIN,
                activo = true
            ),
            Usuario(
                id = 2,
                nombre = "Carlos Rodríguez",
                email = "carlos@fotomar.cl",
                rol = Rol.JEFE,
                activo = true
            ),
            Usuario(
                id = 3,
                nombre = "Ana Martínez",
                email = "ana@fotomar.cl",
                rol = Rol.SUPERVISOR,
                activo = true
            ),
            Usuario(
                id = 4,
                nombre = "Juan Pérez",
                email = "juan@fotomar.cl",
                rol = Rol.OPERADOR,
                activo = true
            ),
            Usuario(
                id = 5,
                nombre = "María García",
                email = "maria@fotomar.cl",
                rol = Rol.OPERADOR,
                activo = true
            ),
            Usuario(
                id = 6,
                nombre = "Pedro López",
                email = "pedro@fotomar.cl",
                rol = Rol.OPERADOR,
                activo = false
            )
        )
    }
}