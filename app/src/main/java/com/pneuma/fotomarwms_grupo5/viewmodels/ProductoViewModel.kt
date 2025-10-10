package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de productos
 * Maneja búsqueda, detalle, creación y actualización de productos
 */
class ProductoViewModel : ViewModel() {

    // ========== ESTADOS DE PRODUCTOS ==========

    private val _searchState = MutableStateFlow<UiState<List<Producto>>>(UiState.Idle)
    val searchState: StateFlow<UiState<List<Producto>>> = _searchState.asStateFlow()

    private val _selectedProducto = MutableStateFlow<Producto?>(null)
    val selectedProducto: StateFlow<Producto?> = _selectedProducto.asStateFlow()

    private val _productoDetailState = MutableStateFlow<UiState<Producto>>(UiState.Idle)
    val productoDetailState: StateFlow<UiState<Producto>> = _productoDetailState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

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

                // TODO: Conectar con backend
                // val response = productoRepository.search(query)

                // MOCK TEMPORAL - Datos de ejemplo
                kotlinx.coroutines.delay(800)

                val mockProductos = listOf(
                    Producto(
                        sku = "CA30001",
                        descripcion = "Cámara Canon EOS R5",
                        stock = 15,
                        codigoBarrasIndividual = "9876543210123",
                        lpn = "LPN-001",
                        lpnDesc = "Caja Cámaras Canon",
                        fechaVencimiento = null,
                        vencimientoCercano = false,
                        ubicaciones = listOf(
                            ProductoUbicacion(12, "A-12", 10),
                            ProductoUbicacion(45, "B-45", 5)
                        )
                    ),
                    Producto(
                        sku = "FL30001",
                        descripcion = "Flash Canon Speedlite 600EX",
                        stock = 8,
                        codigoBarrasIndividual = "9876543210456",
                        lpn = "LPN-004",
                        lpnDesc = "Caja Flash",
                        fechaVencimiento = null,
                        vencimientoCercano = false,
                        ubicaciones = listOf(
                            ProductoUbicacion(8, "A-08", 8)
                        )
                    ),
                    Producto(
                        sku = "AP30001",
                        descripcion = "Adaptador Canon EF-EOS R",
                        stock = 25,
                        codigoBarrasIndividual = "9876543210789",
                        lpn = "LPN-008",
                        lpnDesc = "Caja Adaptadores",
                        fechaVencimiento = "2025-12-31",
                        vencimientoCercano = false,
                        ubicaciones = listOf(
                            ProductoUbicacion(3, "A-03", 15),
                            ProductoUbicacion(60, "C-60", 10)
                        )
                    )
                )

                // Filtrar por query si existe
                val filtered = if (query.isBlank()) {
                    mockProductos
                } else {
                    mockProductos.filter {
                        it.sku.contains(query, ignoreCase = true) ||
                                it.descripcion.contains(query, ignoreCase = true) ||
                                it.codigoBarrasIndividual?.contains(query, ignoreCase = true) == true ||
                                it.lpn?.contains(query, ignoreCase = true) == true
                    }
                }

                _searchState.value = UiState.Success(filtered)

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

                // TODO: Conectar con backend
                // val producto = productoRepository.getProductoBySku(sku)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                val mockProducto = Producto(
                    sku = sku,
                    descripcion = "Producto de ejemplo - $sku",
                    stock = 20,
                    codigoBarrasIndividual = "1234567890123",
                    lpn = "LPN-001",
                    lpnDesc = "Caja ejemplo",
                    fechaVencimiento = "2026-06-30",
                    vencimientoCercano = false,
                    ubicaciones = listOf(
                        ProductoUbicacion(5, "A-05", 12),
                        ProductoUbicacion(23, "B-23", 8)
                    )
                )

                _selectedProducto.value = mockProducto
                _productoDetailState.value = UiState.Success(mockProducto)

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
     * @param request Datos del nuevo producto
     */
    fun createProducto(request: ProductoRequest) {
        viewModelScope.launch {
            try {
                // TODO: Conectar con backend
                // val response = productoRepository.createProducto(request)

                kotlinx.coroutines.delay(500)

                // Recargar lista de productos después de crear
                searchProductos(_searchQuery.value)

            } catch (e: Exception) {
                _searchState.value = UiState.Error(
                    message = "Error al crear producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Actualiza un producto existente
     * Conecta con: PUT /api/productos/{sku}
     * Solo para ADMIN, JEFE, SUPERVISOR
     * @param sku SKU del producto a actualizar
     * @param request Datos actualizados
     */
    fun updateProducto(sku: String, request: ProductoRequest) {
        viewModelScope.launch {
            try {
                // TODO: Conectar con backend
                // val response = productoRepository.updateProducto(sku, request)

                kotlinx.coroutines.delay(500)

                // Recargar detalle del producto
                getProductoDetail(sku)

            } catch (e: Exception) {
                _productoDetailState.value = UiState.Error(
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
                // TODO: Conectar con backend
                // productoRepository.deleteProducto(sku)

                kotlinx.coroutines.delay(500)

                // Recargar lista después de eliminar
                searchProductos(_searchQuery.value)

            } catch (e: Exception) {
                _searchState.value = UiState.Error(
                    message = "Error al eliminar producto: ${e.message}"
                )
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
     * Verifica si un producto está próximo a vencer (menos de 2 meses)
     * @param fechaVencimiento Fecha en formato ISO (yyyy-MM-dd)
     * @return true si quedan menos de 60 días
     */
    fun isVencimientoCercano(fechaVencimiento: String?): Boolean {
        if (fechaVencimiento == null) return false

        try {
            // TODO: Implementar lógica de comparación de fechas
            // Usar java.time.LocalDate o similar
            return false
        } catch (e: Exception) {
            return false
        }
    }
}