package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.models.*
import com.pneuma.fotomarwms_grupo5.network.ProductoRequest
import com.pneuma.fotomarwms_grupo5.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de productos
 * Maneja búsqueda, detalle, creación y actualización de productos
 * Usa ProductoRepository con patrón local-first
 */
class ProductoViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    // ========== ESTADOS DE PRODUCTOS ==========

    private val _searchState = MutableStateFlow<UiState<List<Producto>>>(UiState.Idle)
    val searchState: StateFlow<UiState<List<Producto>>> = _searchState.asStateFlow()

    private val _selectedProducto = MutableStateFlow<Producto?>(null)
    val selectedProducto: StateFlow<Producto?> = _selectedProducto.asStateFlow()

    private val _productoDetailState = MutableStateFlow<UiState<Producto>>(UiState.Idle)
    val productoDetailState: StateFlow<UiState<Producto>> = _productoDetailState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _createState = MutableStateFlow<UiState<Producto>>(UiState.Idle)
    val createState: StateFlow<UiState<Producto>> = _createState.asStateFlow()

    private val _updateState = MutableStateFlow<UiState<Producto>>(UiState.Idle)
    val updateState: StateFlow<UiState<Producto>> = _updateState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    // ========== BÚSQUEDA DE PRODUCTOS ==========

    /**
     * Busca productos por texto
     * Conecta con: GET /api/productos/search?q={query}
     * Busca por: SKU, descripción, código de barras, LPN, ubicación
     * @param query Texto de búsqueda (opcional, si está vacío devuelve todos)
     */
    fun searchProductos(query: String = "") {
        viewModelScope.launch {
            try {
                _searchState.value = UiState.Loading
                _searchQuery.value = query

                val result = productoRepository.searchProductos(query.ifBlank { null })
                
                _searchState.value = if (result.isSuccess) {
                    UiState.Success(result.getOrNull()!!)
                } else {
                    UiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Error al buscar productos"
                    )
                }

            } catch (e: Exception) {
                _searchState.value = UiState.Error(
                    message = "Error al buscar productos: ${e.message}"
                )
            }
        }
    }

    /**
     * Busca un producto específico por código de barras o LPN
     * Usado cuando se escanea con la cámara
     * @param codigo Código de barras o LPN escaneado
     */
    fun searchByBarcode(codigo: String) {
        searchProductos(codigo)
    }

    /**
     * Obtiene el detalle completo de un producto por SKU
     * Conecta con: GET /api/productos/{sku}
     * @param sku SKU del producto
     */
    fun getProductoDetail(sku: String) {
        viewModelScope.launch {
            try {
                _productoDetailState.value = UiState.Loading

                val result = productoRepository.getProductoBySku(sku)
                
                if (result.isSuccess) {
                    val producto = result.getOrNull()!!
                    _selectedProducto.value = producto
                    _productoDetailState.value = UiState.Success(producto)
                } else {
                    _productoDetailState.value = UiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Error al obtener producto"
                    )
                }

            } catch (e: Exception) {
                _productoDetailState.value = UiState.Error(
                    message = "Error al obtener producto: ${e.message}"
                )
            }
        }
    }

    // ========== GESTIÓN DE PRODUCTOS (ADMIN/JEFE) ==========

    /**
     * Crea un nuevo producto
     * Conecta con: POST /api/productos
     * Solo para ADMIN, JEFE, SUPERVISOR
     * Usa patrón local-first: guarda local → envía backend → elimina si OK
     * @param request Datos del nuevo producto
     */
    fun createProducto(request: ProductoRequest) {
        viewModelScope.launch {
            try {
                _createState.value = UiState.Loading

                val result = productoRepository.createProducto(request)
                
                if (result.isSuccess) {
                    _createState.value = UiState.Success(result.getOrNull()!!)
                    // Recargar lista de productos después de crear
                    searchProductos(_searchQuery.value)
                } else {
                    // Puede estar guardado localmente aunque falle el backend
                    _createState.value = UiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Error al crear producto"
                    )
                }

            } catch (e: Exception) {
                _createState.value = UiState.Error(
                    message = "Error al crear producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Actualiza un producto existente
     * Conecta con: PUT /api/productos/{sku}
     * Solo para ADMIN, JEFE, SUPERVISOR
     * Usa patrón local-first: guarda local → envía backend → elimina si OK
     * @param sku SKU del producto a actualizar
     * @param request Datos actualizados
     */
    fun updateProducto(sku: String, request: ProductoRequest) {
        viewModelScope.launch {
            try {
                _updateState.value = UiState.Loading

                val result = productoRepository.updateProducto(sku, request)
                
                if (result.isSuccess) {
                    _updateState.value = UiState.Success(result.getOrNull()!!)
                    // Recargar detalle del producto
                    getProductoDetail(sku)
                } else {
                    // Puede estar guardado localmente aunque falle el backend
                    _updateState.value = UiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Error al actualizar producto"
                    )
                }

            } catch (e: Exception) {
                _updateState.value = UiState.Error(
                    message = "Error al actualizar producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Elimina un producto
     * Conecta con: DELETE /api/productos/{sku}
     * Solo para ADMIN, JEFE
     * @param sku SKU del producto a eliminar
     */
    fun deleteProducto(sku: String) {
        viewModelScope.launch {
            try {
                _deleteState.value = UiState.Loading

                val result = productoRepository.deleteProducto(sku)
                
                if (result.isSuccess) {
                    _deleteState.value = UiState.Success(Unit)
                    // Recargar lista después de eliminar
                    searchProductos(_searchQuery.value)
                } else {
                    _deleteState.value = UiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Error al eliminar producto"
                    )
                }

            } catch (e: Exception) {
                _deleteState.value = UiState.Error(
                    message = "Error al eliminar producto: ${e.message}"
                )
            }
        }
    }

    // ========== SINCRONIZACIÓN ==========

    /**
     * Sincroniza todos los productos pendientes con el backend
     * Útil para sincronizar después de recuperar conexión
     */
    fun syncPendientes() {
        viewModelScope.launch {
            try {
                val result = productoRepository.syncPendientes()
                
                if (result.isSuccess) {
                    val syncCount = result.getOrNull() ?: 0
                    // Opcional: Mostrar mensaje de éxito con cantidad sincronizada
                    // Recargar lista si hay cambios
                    if (syncCount > 0) {
                        searchProductos(_searchQuery.value)
                    }
                }
            } catch (e: Exception) {
                // Error silencioso, se reintentará después
            }
        }
    }

    // ========== UTILIDADES ==========

    /**
     * Limpia el estado de búsqueda
     */
    fun clearSearch() {
        _searchState.value = UiState.Idle
        _searchQuery.value = ""
    }

    /**
     * Limpia el producto seleccionado
     */
    fun clearSelectedProducto() {
        _selectedProducto.value = null
        _productoDetailState.value = UiState.Idle
    }

    /**
     * Limpia el estado de creación
     */
    fun clearCreateState() {
        _createState.value = UiState.Idle
    }

    /**
     * Limpia el estado de actualización
     */
    fun clearUpdateState() {
        _updateState.value = UiState.Idle
    }

    /**
     * Limpia el estado de eliminación
     */
    fun clearDeleteState() {
        _deleteState.value = UiState.Idle
    }

    /**
     * Verifica si un producto está próximo a vencer (menos de 2 meses)
     * @param fechaVencimiento Fecha en formato ISO (yyyy-MM-dd)
     * @return true si quedan menos de 60 días
     */
    fun isVencimientoCercano(fechaVencimiento: String?): Boolean {
        if (fechaVencimiento == null) return false

        try {
            // Implementación simple de comparación de fechas
            val parts = fechaVencimiento.split("-")
            if (parts.size != 3) return false
            
            val year = parts[0].toIntOrNull() ?: return false
            val month = parts[1].toIntOrNull() ?: return false
            val day = parts[2].toIntOrNull() ?: return false
            
            // Obtener fecha actual
            val currentDate = java.util.Calendar.getInstance()
            val currentYear = currentDate.get(java.util.Calendar.YEAR)
            val currentMonth = currentDate.get(java.util.Calendar.MONTH) + 1
            val currentDay = currentDate.get(java.util.Calendar.DAY_OF_MONTH)
            
            // Calcular diferencia aproximada en días
            val yearDiff = (year - currentYear) * 365
            val monthDiff = (month - currentMonth) * 30
            val dayDiff = day - currentDay
            
            val totalDays = yearDiff + monthDiff + dayDiff
            
            return totalDays in 1..60
        } catch (e: Exception) {
            return false
        }
    }
}
