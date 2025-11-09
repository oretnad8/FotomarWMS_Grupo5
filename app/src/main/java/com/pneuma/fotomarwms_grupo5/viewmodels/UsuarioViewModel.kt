package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.db.AppDatabase
import com.pneuma.fotomarwms_grupo5.db.entities.UsuarioLocal
import com.pneuma.fotomarwms_grupo5.models.*
import com.pneuma.fotomarwms_grupo5.network.RetrofitClient
import com.pneuma.fotomarwms_grupo5.network.CrearUsuarioRequest
import com.pneuma.fotomarwms_grupo5.network.ActualizarUsuarioRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de usuarios
 * USA MICROSERVICIOS REALES - SIN MOCKS
 */
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()
    private val apiService = RetrofitClient.usuariosService

    // ========== ESTADOS ==========

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

    // ========== CONSULTA DE USUARIOS ==========

    /**
     * Obtiene todos los usuarios
     * GET http://fotomarwms.ddns.net:8082/api/usuarios
     */
    fun getAllUsuarios() {
        viewModelScope.launch {
            try {
                _usuariosState.value = UiState.Loading

                val response = apiService.getAllUsuarios()
                
                if (response.isSuccessful && response.body() != null) {
                    _usuariosState.value = UiState.Success(response.body()!!)
                } else {
                    _usuariosState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _usuariosState.value = UiState.Error(
                    message = "Error al obtener usuarios: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene un usuario por ID
     * GET http://fotomarwms.ddns.net:8082/api/usuarios/{id}
     */
    fun getUsuarioDetail(id: Int) {
        viewModelScope.launch {
            try {
                _usuarioDetailState.value = UiState.Loading

                val response = apiService.getUsuarioById(id)
                
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!
                    _selectedUsuario.value = usuario
                    _usuarioDetailState.value = UiState.Success(usuario)
                } else {
                    _usuarioDetailState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _usuarioDetailState.value = UiState.Error(
                    message = "Error al obtener usuario: ${e.message}"
                )
            }
        }
    }

    /**
     * Filtra usuarios por rol (filtro local)
     */
    fun getUsuariosByRol(rol: String) {
        viewModelScope.launch {
            try {
                _usuariosState.value = UiState.Loading

                val response = apiService.getAllUsuarios()
                
                if (response.isSuccessful && response.body() != null) {
                    val filtered = response.body()!!.filter { it.rol == rol }
                    _usuariosState.value = UiState.Success(filtered)
                } else {
                    _usuariosState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _usuariosState.value = UiState.Error(
                    message = "Error al filtrar usuarios: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene solo usuarios activos (filtro local)
     */
    fun getUsuariosActivos() {
        viewModelScope.launch {
            try {
                _usuariosState.value = UiState.Loading

                val response = apiService.getAllUsuarios()
                
                if (response.isSuccessful && response.body() != null) {
                    val filtered = response.body()!!.filter { it.activo }
                    _usuariosState.value = UiState.Success(filtered)
                } else {
                    _usuariosState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _usuariosState.value = UiState.Error(
                    message = "Error al obtener usuarios activos: ${e.message}"
                )
            }
        }
    }

    // ========== GESTIÓN DE USUARIOS ==========

    /**
     * Crea un nuevo usuario
     * POST http://fotomarwms.ddns.net:8082/api/usuarios
     * Patrón local-first
     */
    fun createUsuario(request: CrearUsuarioRequest) {
        viewModelScope.launch {
            try {
                _createUsuarioState.value = UiState.Loading

                // 1. Guardar localmente
                val usuarioLocal = UsuarioLocal(
                    nombre = request.nombre,
                    email = request.email,
                    password = request.password,
                    rol = request.rol,
                    timestamp = System.currentTimeMillis()
                )
                val localId = usuarioDao.insertarUsuarioPendiente(usuarioLocal)

                try {
                    // 2. Enviar al backend
                    val response = apiService.createUsuario(request)
                    
                    if (response.isSuccessful && response.code() == 200) {
                        // 3. Eliminar local si éxito
                        usuarioDao.deleteById(localId)
                        _createUsuarioState.value = UiState.Success(true)
                        // Recargar lista
                        getAllUsuarios()
                    } else {
                        _createUsuarioState.value = UiState.Error(
                            "Guardado localmente. Error backend: ${response.code()}"
                        )
                    }
                } catch (e: Exception) {
                    _createUsuarioState.value = UiState.Error(
                        "Guardado localmente. Se sincronizará después."
                    )
                }

            } catch (e: Exception) {
                _createUsuarioState.value = UiState.Error(
                    message = "Error al crear usuario: ${e.message}"
                )
            }
        }
    }

    /**
     * Actualiza un usuario
     * PUT http://fotomarwms.ddns.net:8082/api/usuarios/{id}
     */
    fun updateUsuario(id: Int, request: ActualizarUsuarioRequest) {
        viewModelScope.launch {
            try {
                _updateUsuarioState.value = UiState.Loading

                val response = apiService.updateUsuario(id, request)
                
                if (response.isSuccessful && response.code() == 200) {
                    _updateUsuarioState.value = UiState.Success(true)
                    // Recargar detalle
                    getUsuarioDetail(id)
                } else {
                    _updateUsuarioState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _updateUsuarioState.value = UiState.Error(
                    message = "Error al actualizar usuario: ${e.message}"
                )
            }
        }
    }

    /**
     * Elimina un usuario
     * DELETE http://fotomarwms.ddns.net:8082/api/usuarios/{id}
     */
    fun deleteUsuario(id: Int) {
        viewModelScope.launch {
            try {
                _deleteUsuarioState.value = UiState.Loading

                val response = apiService.deleteUsuario(id)
                
                if (response.isSuccessful && response.code() == 200) {
                    _deleteUsuarioState.value = UiState.Success(true)
                    // Recargar lista
                    getAllUsuarios()
                } else {
                    _deleteUsuarioState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _deleteUsuarioState.value = UiState.Error(
                    message = "Error al eliminar usuario: ${e.message}"
                )
            }
        }
    }

    /**
     * Toggle activo/inactivo
     * POST http://fotomarwms.ddns.net:8082/api/usuarios/{id}/toggle-activo
     */
    fun toggleActivo(id: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.toggleActivo(id)
                
                if (response.isSuccessful) {
                    // Recargar lista
                    getAllUsuarios()
                }

            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }

    // ========== UTILIDADES ==========

    fun clearUsuarios() {
        _usuariosState.value = UiState.Idle
    }

    fun clearSelectedUsuario() {
        _selectedUsuario.value = null
        _usuarioDetailState.value = UiState.Idle
    }

    fun clearCreateState() {
        _createUsuarioState.value = UiState.Idle
    }

    fun clearUpdateState() {
        _updateUsuarioState.value = UiState.Idle
    }

    fun clearDeleteState() {
        _deleteUsuarioState.value = UiState.Idle
    }
}
