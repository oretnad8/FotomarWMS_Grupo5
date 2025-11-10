package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.db.AppDatabase
import com.pneuma.fotomarwms_grupo5.models.*
import com.pneuma.fotomarwms_grupo5.repository.UbicacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de ubicaciones en bodega
 * Maneja ubicaciones, asignación de productos y consultas por piso
 * Usa UbicacionRepository con patrón local-first
 */
class UbicacionViewModel(
    application: Application,
    private val ubicacionRepository: UbicacionRepository
) : AndroidViewModel(application) {

    // ========== ESTADOS DE UBICACIONES ==========

    private val _ubicacionesState = MutableStateFlow<UiState<List<Ubicacion>>>(UiState.Idle)
    val ubicacionesState: StateFlow<UiState<List<Ubicacion>>> = _ubicacionesState.asStateFlow()

    private val _selectedUbicacion = MutableStateFlow<Ubicacion?>(null)
    val selectedUbicacion: StateFlow<Ubicacion?> = _selectedUbicacion.asStateFlow()

    private val _ubicacionDetailState = MutableStateFlow<UiState<Ubicacion>>(UiState.Idle)
    val ubicacionDetailState: StateFlow<UiState<Ubicacion>> = _ubicacionDetailState.asStateFlow()

    private val _asignacionState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val asignacionState: StateFlow<UiState<Boolean>> = _asignacionState.asStateFlow()

    private val _pisoSeleccionado = MutableStateFlow<Piso?>(null)
    val pisoSeleccionado: StateFlow<Piso?> = _pisoSeleccionado.asStateFlow()

    // ========== CONSULTA DE UBICACIONES ==========

    /**
     * Obtiene todas las ubicaciones de la bodega
     * Conecta con: GET /api/ubicaciones
     */
    fun getAllUbicaciones(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _ubicacionesState.value = UiState.Loading

                val result = ubicacionRepository.getUbicaciones(piso = null, forceRefresh = forceRefresh)
                
                _ubicacionesState.value = if (result.isSuccess) {
                    UiState.Success(result.getOrNull() ?: emptyList())
                } else {
                    UiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Error al obtener ubicaciones"
                    )
                }

            } catch (e: Throwable) {
                _ubicacionesState.value = UiState.Error(
                    message = "Error al obtener ubicaciones: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene ubicaciones filtradas por piso
     * Conecta con: GET /api/ubicaciones?piso={A|B|C}
     * @param piso Piso a filtrar (A, B o C)
     */
    fun getUbicacionesByPiso(piso: Piso, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _ubicacionesState.value = UiState.Loading
                _pisoSeleccionado.value = piso

                val result = ubicacionRepository.getUbicaciones(
                    piso = piso.codigo.toString(),
                    forceRefresh = forceRefresh
                )
                
                _ubicacionesState.value = if (result.isSuccess) {
                    UiState.Success(result.getOrNull() ?: emptyList())
                } else {
                    UiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Error al obtener ubicaciones"
                    )
                }

            } catch (e: Throwable) {
                _ubicacionesState.value = UiState.Error(
                    message = "Error al obtener ubicaciones del piso ${piso.codigo}: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene el detalle de una ubicación específica
     * Conecta con: GET /api/ubicaciones/{codigo}
     * @param codigo Código de la ubicación (ej: A-12, B-45)
     */
    fun getUbicacionDetail(codigo: String) {
        viewModelScope.launch {
            try {
                _ubicacionDetailState.value = UiState.Loading

                val result = ubicacionRepository.getUbicacionByCodigo(codigo)
                
                if (result.isSuccess) {
                    val ubicacion = result.getOrNull()!!
                    _selectedUbicacion.value = ubicacion
                    _ubicacionDetailState.value = UiState.Success(ubicacion)
                } else {
                    _ubicacionDetailState.value = UiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Error al obtener ubicación"
                    )
                }

            } catch (e: Exception) {
                _ubicacionDetailState.value = UiState.Error(
                    message = "Error al obtener ubicación: ${e.message}"
                )
            }
        }
    }

    // ========== ASIGNACIÓN DE PRODUCTOS ==========

    /**
     * Asigna un producto a una ubicación
     * Conecta con: POST /api/ubicaciones/asignar
     * Usa patrón local-first: guarda local → envía backend → elimina si OK
     * @param sku SKU del producto
     * @param codigoUbicacion Código de la ubicación (ej: A-12)
     * @param cantidad Cantidad a asignar
     */
    fun asignarProducto(
        productoViewModel: ProductoViewModel,
        sku: String,
        codigoUbicacion: String,
        cantidad: Int
    ){
        viewModelScope.launch {
            try {
                _asignacionState.value = UiState.Loading

                val result = ubicacionRepository.asignarProducto(sku, codigoUbicacion, cantidad)
                
                if (result.isSuccess) {
                    _asignacionState.value = UiState.Success(true)
                    // Notificamos al otro ViewModel que debe actualizarse.
                    productoViewModel.refreshProductoDetail()
                    // Recargar detalle de la ubicación si está seleccionada
                    if (_selectedUbicacion.value?.codigoUbicacion == codigoUbicacion) {
                        getUbicacionDetail(codigoUbicacion)
                    }
                } else {
                    // Puede estar guardado localmente aunque falle el backend
                    _asignacionState.value = UiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Error al asignar producto"
                    )
                }

            } catch (e: Exception) {
                _asignacionState.value = UiState.Error(
                    message = "Error al asignar producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Asigna un producto a múltiples ubicaciones
     * @param sku SKU del producto
     * @param asignaciones Lista de pares (codigoUbicacion, cantidad)
     */
    fun asignarProductoMultiple(sku: String, asignaciones: List<Pair<String, Int>>) {
        viewModelScope.launch {
            try {
                _asignacionState.value = UiState.Loading

                var successCount = 0
                var errorCount = 0

                asignaciones.forEach { (codigo, cantidad) ->
                    val result = ubicacionRepository.asignarProducto(sku, codigo, cantidad)
                    if (result.isSuccess) {
                        successCount++
                    } else {
                        errorCount++
                    }
                }

                _asignacionState.value = if (errorCount == 0) {
                    UiState.Success(true)
                } else if (successCount > 0) {
                    UiState.Error("$successCount asignaciones exitosas, $errorCount fallidas")
                } else {
                    UiState.Error("Error al asignar producto a las ubicaciones")
                }

            } catch (e: Exception) {
                _asignacionState.value = UiState.Error(
                    message = "Error al asignar producto: ${e.message}"
                )
            }
        }
    }

    // ========== SINCRONIZACIÓN ==========

    /**
     * Sincroniza todas las asignaciones pendientes con el backend
     * Útil para sincronizar después de recuperar conexión
     */
    fun syncAsignacionesPendientes() {
        viewModelScope.launch {
            try {
                val result = ubicacionRepository.syncAsignacionesPendientes()
                
                if (result.isSuccess) {
                    val syncCount = result.getOrNull() ?: 0
                    // Recargar ubicaciones si hay cambios
                    if (syncCount > 0) {
                        _pisoSeleccionado.value?.let { piso ->
                            getUbicacionesByPiso(piso)
                        } ?: getAllUbicaciones()
                    }
                }
            } catch (e: Exception) {
                // Error silencioso, se reintentará después
            }
        }
    }

    // ========== UTILIDADES ==========

    /**
     * Limpia el estado de ubicaciones
     */
    fun clearUbicaciones() {
        _ubicacionesState.value = UiState.Idle
        _pisoSeleccionado.value = null
    }

    /**
     * Limpia la ubicación seleccionada
     */
    fun clearSelectedUbicacion() {
        _selectedUbicacion.value = null
        _ubicacionDetailState.value = UiState.Idle
    }

    /**
     * Limpia el estado de asignación
     */
    fun clearAsignacionState() {
        _asignacionState.value = UiState.Idle
    }

    /**
     * Limpia el filtro de piso y muestra todas las ubicaciones
     */
    fun clearPisoFilter() {
        _pisoSeleccionado.value = null
        getAllUbicaciones()
    }

    /**
     * Valida el formato de un código de ubicación
     * @param codigo Código a validar (debe ser formato: A-12, B-45, C-60)
     * @return true si el formato es válido
     */
    fun isCodigoValido(codigo: String): Boolean {
        val regex = Regex("^[ABC]-([0-5]?[0-9]|60)$")
        return regex.matches(codigo)
    }

    /**
     * Genera un código de ubicación a partir de piso y número
     * @param piso Piso (A, B o C)
     * @param numero Número (1-60)
     * @return Código en formato A-12, B-05, etc.
     */
    fun generarCodigo(piso: Char, numero: Int): String {
        return "$piso-${numero.toString().padStart(2, '0')}"
    }
}
