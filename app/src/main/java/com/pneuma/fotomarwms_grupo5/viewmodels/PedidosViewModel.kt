package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PedidosViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.pedidosService
    private val ubicacionesService = RetrofitClient.ubicacionesService

    private val _pedidosPendientes = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidosPendientes = _pedidosPendientes.asStateFlow()

    private val _hojaPicking = MutableStateFlow<HojaPickingResponse?>(null)
    val hojaPicking = _hojaPicking.asStateFlow()

    private val _currentPedido = MutableStateFlow<Pedido?>(null)
    val currentPedido = _currentPedido.asStateFlow()

    private val _monitoreo = MutableStateFlow<MonitoreoResponse?>(null)
    val monitoreo = _monitoreo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadPedidosPendientes()
    }

    fun loadPedidosPendientes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPedidosPendientes()
                if (response.isSuccessful) {
                    _pedidosPendientes.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar pedidos: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadHojaPicking(pedidoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getHojaPicking(pedidoId)
                if (response.isSuccessful) {
                    _hojaPicking.value = response.body()
                } else {
                    _error.value = "Error al cargar hoja de picking"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _misPedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val misPedidos = _misPedidos.asStateFlow()

    fun loadPedidosEnPicking() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Endpoint específico: /api/pedidos/picking
                val response = apiService.getPedidosEnPicking()
                if (response.isSuccessful) {
                    val all = response.body() ?: emptyList()
                    val app = getApplication<com.pneuma.fotomarwms_grupo5.FotomarWMSApplication>()
                    val userId = app.getCurrentUserId()
                    
                    // Ajuste: Si el backend devuelve TODOS los pedidos en picking, filtramos por el usuario actual.
                    // Si devuelve solo los asignados, este filtro no hará daño (si coincide el ID).
                    _misPedidos.value = all.filter { 
                        it.operadorAsignadoId == userId || it.operadorAsignadoId == null 
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar pedidos en picking: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _pedidosFinalizados = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidosFinalizados = _pedidosFinalizados.asStateFlow()

    fun loadPedidosFinalizados() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val app = getApplication<com.pneuma.fotomarwms_grupo5.FotomarWMSApplication>()
                val userId = app.getCurrentUserId()
                
                val response = apiService.getAllPedidos()
                if (response.isSuccessful) {
                    val all = response.body() ?: emptyList()
                    // Mostrar finalizados donde yo fui el operador (o todos si es jefe, pero estamos en vista genérica)
                    _pedidosFinalizados.value = all.filter {
                        (it.operadorAsignadoId == userId || it.operadorAsignadoId == null) &&
                        (it.estado == EstadoPedido.PICKING_COMPLETADO ||
                        it.estado == EstadoPedido.VALIDADO ||
                        it.estado == EstadoPedido.EN_TRANSPORTE ||
                        it.estado == EstadoPedido.ENTREGADO)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar finalizados"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPedido(pedidoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPedido(pedidoId)
                if (response.isSuccessful) {
                    _currentPedido.value = response.body()
                } else {
                    _error.value = "Error al cargar pedido: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun aceptarPedido(pedidoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.aceptarPedido(pedidoId)
                if (response.isSuccessful) {
                    loadHojaPicking(pedidoId) // Reload to get updated state/assignment if applicable
                    loadPedido(pedidoId) // Reload order status
                } else {
                    _error.value = "Error al aceptar pedido: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmarPicking(pedidoId: Int, items: List<DetallePickingCheck>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val app = getApplication<com.pneuma.fotomarwms_grupo5.FotomarWMSApplication>()
                val userId = app.getCurrentUserId()
                
                val request = ConfirmarPickingRequest(
                    operadorId = userId,
                    detalles = items
                )
                val response = apiService.confirmarPicking(pedidoId, request)

                if (response.isSuccessful) {
                    loadPedidosPendientes()
                } else {
                    _error.value = "Error al confirmar picking"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun finalizarEntrega(pedidoId: Int, transportista: String, fotoBase64: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = FinalizarEntregaRequest(transportista, fotoBase64)
                val response = apiService.finalizarEntrega(pedidoId, request)
                if (response.isSuccessful) {
                    loadPedidosPendientes()
                } else {
                    _error.value = "Error al finalizar entrega"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMonitoreo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getMonitoreo()
                if (response.isSuccessful) {
                    _monitoreo.value = response.body()
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar monitoreo"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
