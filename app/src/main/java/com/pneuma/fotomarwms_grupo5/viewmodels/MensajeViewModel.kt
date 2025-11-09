package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.db.AppDatabase
import com.pneuma.fotomarwms_grupo5.db.entities.MensajeLocal
import com.pneuma.fotomarwms_grupo5.models.*
import com.pneuma.fotomarwms_grupo5.network.RetrofitClient
import com.pneuma.fotomarwms_grupo5.network.EnviarMensajeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de mensajes
 * USA MICROSERVICIOS REALES - SIN MOCKS
 */
class MensajeViewModel(application: Application) : AndroidViewModel(application) {

    private val mensajeDao = AppDatabase.getDatabase(application).mensajeDao()
    private val apiService = RetrofitClient.mensajesService

    // ========== ESTADOS ==========

    private val _mensajesState = MutableStateFlow<UiState<List<Mensaje>>>(UiState.Idle)
    val mensajesState: StateFlow<UiState<List<Mensaje>>> = _mensajesState.asStateFlow()

    private val _mensajesEnviadosState = MutableStateFlow<UiState<List<Mensaje>>>(UiState.Idle)
    val mensajesEnviadosState: StateFlow<UiState<List<Mensaje>>> = _mensajesEnviadosState.asStateFlow()

    private val _resumenState = MutableStateFlow<UiState<ResumenMensajes>>(UiState.Idle)
    val resumenState: StateFlow<UiState<ResumenMensajes>> = _resumenState.asStateFlow()

    private val _enviarState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val enviarState: StateFlow<UiState<Boolean>> = _enviarState.asStateFlow()

    // ========== CONSULTA DE MENSAJES ==========

    /**
     * Obtiene todos los mensajes recibidos
     * GET http://fotomarwms.ddns.net:8086/api/mensajes
     */
    fun getMensajes() {
        viewModelScope.launch {
            try {
                _mensajesState.value = UiState.Loading

                val response = apiService.getMensajes()
                
                if (response.isSuccessful && response.body() != null) {
                    _mensajesState.value = UiState.Success(response.body()!!)
                } else {
                    _mensajesState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _mensajesState.value = UiState.Error(
                    message = "Error al obtener mensajes: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene solo mensajes no leídos
     * GET http://fotomarwms.ddns.net:8086/api/mensajes?soloNoLeidos=true
     */
    fun getMensajesNoLeidos() {
        viewModelScope.launch {
            try {
                _mensajesState.value = UiState.Loading

                val response = apiService.getMensajesNoLeidos()
                
                if (response.isSuccessful && response.body() != null) {
                    _mensajesState.value = UiState.Success(response.body()!!)
                } else {
                    _mensajesState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _mensajesState.value = UiState.Error(
                    message = "Error al obtener mensajes no leídos: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene solo mensajes importantes
     * GET http://fotomarwms.ddns.net:8086/api/mensajes?soloImportantes=true
     */
    fun getMensajesImportantes() {
        viewModelScope.launch {
            try {
                _mensajesState.value = UiState.Loading

                val response = apiService.getMensajesImportantes()
                
                if (response.isSuccessful && response.body() != null) {
                    _mensajesState.value = UiState.Success(response.body()!!)
                } else {
                    _mensajesState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _mensajesState.value = UiState.Error(
                    message = "Error al obtener mensajes importantes: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene resumen de mensajes
     * GET http://fotomarwms.ddns.net:8086/api/mensajes/resumen
     */
    fun getResumen() {
        viewModelScope.launch {
            try {
                _resumenState.value = UiState.Loading

                val response = apiService.getResumen()
                
                if (response.isSuccessful && response.body() != null) {
                    _resumenState.value = UiState.Success(response.body()!!)
                } else {
                    _resumenState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _resumenState.value = UiState.Error(
                    message = "Error al obtener resumen: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene mensajes enviados
     * GET http://fotomarwms.ddns.net:8086/api/mensajes/enviados
     */
    fun getMensajesEnviados() {
        viewModelScope.launch {
            try {
                _mensajesEnviadosState.value = UiState.Loading

                val response = apiService.getMensajesEnviados()
                
                if (response.isSuccessful && response.body() != null) {
                    _mensajesEnviadosState.value = UiState.Success(response.body()!!)
                } else {
                    _mensajesEnviadosState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _mensajesEnviadosState.value = UiState.Error(
                    message = "Error al obtener mensajes enviados: ${e.message}"
                )
            }
        }
    }

    // ========== ACCIONES ==========

    /**
     * Marca un mensaje como leído
     * POST http://fotomarwms.ddns.net:8086/api/mensajes/{id}/leer
     */
    fun marcarLeido(id: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.marcarLeido(id)
                
                if (response.isSuccessful) {
                    // Recargar mensajes
                    getMensajes()
                }

            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }

    /**
     * Toggle importante
     * POST http://fotomarwms.ddns.net:8086/api/mensajes/{id}/importante
     */
    fun toggleImportante(id: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.toggleImportante(id)
                
                if (response.isSuccessful) {
                    // Recargar mensajes
                    getMensajes()
                }

            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }

    /**
     * Envía un mensaje
     * POST http://fotomarwms.ddns.net:8086/api/mensajes/enviar
     * Patrón local-first
     */
    fun enviarMensaje(destinatarioId: Int, asunto: String, contenido: String, importante: Boolean = false) {
        viewModelScope.launch {
            try {
                _enviarState.value = UiState.Loading

                // 1. Guardar localmente
                val mensajeLocal = MensajeLocal(
                    destinatarioId = destinatarioId,
                    asunto = asunto,
                    contenido = contenido,
                    importante = importante,
                    timestamp = System.currentTimeMillis()
                )
                val localId = mensajeDao.insertarMensajePendiente(mensajeLocal)

                try {
                    // 2. Enviar al backend
                    val request = EnviarMensajeRequest(destinatarioId, asunto, contenido, importante)
                    val response = apiService.enviarMensaje(request)
                    
                    if (response.isSuccessful && response.code() == 200) {
                        // 3. Eliminar local si éxito
                        mensajeDao.deleteById(localId)
                        _enviarState.value = UiState.Success(true)
                    } else {
                        _enviarState.value = UiState.Error(
                            "Guardado localmente. Error backend: ${response.code()}"
                        )
                    }
                } catch (e: Exception) {
                    _enviarState.value = UiState.Error(
                        "Guardado localmente. Se sincronizará después."
                    )
                }

            } catch (e: Exception) {
                _enviarState.value = UiState.Error(
                    message = "Error al enviar mensaje: ${e.message}"
                )
            }
        }
    }

    /**
     * Envía mensaje broadcast
     * POST http://fotomarwms.ddns.net:8086/api/mensajes/broadcast
     */
    fun enviarBroadcast(asunto: String, contenido: String, importante: Boolean = false) {
        viewModelScope.launch {
            try {
                _enviarState.value = UiState.Loading

                // 1. Guardar localmente
                val mensajeLocal = MensajeLocal(
                    destinatarioId = -1, // Broadcast
                    asunto = asunto,
                    contenido = contenido,
                    importante = importante,
                    timestamp = System.currentTimeMillis()
                )
                val localId = mensajeDao.insertarMensajePendiente(mensajeLocal)

                try {
                    // 2. Enviar al backend
                    val request = EnviarMensajeRequest(-1, asunto, contenido, importante)
                    val response = apiService.enviarBroadcast(request)
                    
                    if (response.isSuccessful && response.code() == 200) {
                        // 3. Eliminar local si éxito
                        mensajeDao.deleteById(localId)
                        _enviarState.value = UiState.Success(true)
                    } else {
                        _enviarState.value = UiState.Error(
                            "Guardado localmente. Error backend: ${response.code()}"
                        )
                    }
                } catch (e: Exception) {
                    _enviarState.value = UiState.Error(
                        "Guardado localmente. Se sincronizará después."
                    )
                }

            } catch (e: Exception) {
                _enviarState.value = UiState.Error(
                    message = "Error al enviar broadcast: ${e.message}"
                )
            }
        }
    }

    // ========== UTILIDADES ==========

    fun clearMensajes() {
        _mensajesState.value = UiState.Idle
    }

    fun clearEnviarState() {
        _enviarState.value = UiState.Idle
    }
}
