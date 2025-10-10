package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de ubicaciones en bodega
 * Maneja ubicaciones, asignación de productos y consultas por piso
 */
class UbicacionViewModel : ViewModel() {

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
    fun getAllUbicaciones() {
        viewModelScope.launch {
            try {
                _ubicacionesState.value = UiState.Loading

                // TODO: Conectar con backend
                // val ubicaciones = ubicacionRepository.getAll()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(600)

                val mockUbicaciones = generateMockUbicaciones()
                _ubicacionesState.value = UiState.Success(mockUbicaciones)

            } catch (e: Exception) {
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
    fun getUbicacionesByPiso(piso: Piso) {
        viewModelScope.launch {
            try {
                _ubicacionesState.value = UiState.Loading
                _pisoSeleccionado.value = piso

                // TODO: Conectar con backend
                // val ubicaciones = ubicacionRepository.getByPiso(piso.codigo)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(400)

                val mockUbicaciones = generateMockUbicaciones().filter {
                    it.piso == piso.codigo
                }

                _ubicacionesState.value = UiState.Success(mockUbicaciones)

            } catch (e: Exception) {
                _ubicacionesState.value = UiState.Error(
                    message = "Error al obtener ubicaciones del piso ${piso.codigo}: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene el detalle de una ubicación específica con sus productos
     * Conecta con: GET /api/ubicaciones/{codigo}
     * @param codigo Código de ubicación (ej: A-12)
     */
    fun getUbicacionDetail(codigo: String) {
        viewModelScope.launch {
            try {
                _ubicacionDetailState.value = UiState.Loading

                // TODO: Conectar con backend
                // val ubicacion = ubicacionRepository.getByCodigo(codigo)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(400)

                val mockUbicacion = Ubicacion(
                    idUbicacion = 12,
                    codigoUbicacion = codigo,
                    piso = codigo[0],
                    numero = codigo.substringAfter("-").toIntOrNull() ?: 1,
                    productos = listOf(
                        ProductoEnUbicacion("CA30001", "Cámara Canon EOS R5", 10),
                        ProductoEnUbicacion("FL30001", "Flash Canon Speedlite", 5)
                    )
                )

                _selectedUbicacion.value = mockUbicacion
                _ubicacionDetailState.value = UiState.Success(mockUbicacion)

            } catch (e: Exception) {
                _ubicacionDetailState.value = UiState.Error(
                    message = "Error al obtener ubicación: ${e.message}"
                )
            }
        }
    }

    // ========== ASIGNACIÓN DE PRODUCTOS A UBICACIONES ==========

    /**
     * Asigna un producto a una ubicación
     * Conecta con: POST /api/ubicaciones/asignar
     *
     * IMPORTANTE:
     * - JEFE/SUPERVISOR: Asignación inmediata
     * - OPERADOR: Requiere crear solicitud de aprobación (usar AprobacionViewModel)
     *
     * @param sku SKU del producto
     * @param codigoUbicacion Código de ubicación destino
     * @param cantidad Cantidad a asignar
     */
    fun asignarProductoAUbicacion(
        sku: String,
        codigoUbicacion: String,
        cantidad: Int
    ) {
        viewModelScope.launch {
            try {
                _asignacionState.value = UiState.Loading

                // TODO: Conectar con backend
                // val request = AsignarUbicacionRequest(sku, codigoUbicacion, cantidad)
                // ubicacionRepository.asignarProducto(request)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                _asignacionState.value = UiState.Success(true)

                // Recargar ubicación para ver el cambio
                getUbicacionDetail(codigoUbicacion)

            } catch (e: Exception) {
                _asignacionState.value = UiState.Error(
                    message = "Error al asignar producto: ${e.message}"
                )
            }
        }
    }

    // ========== UTILIDADES ==========

    /**
     * Genera un código de ubicación formateado
     * @param piso Letra del piso (A, B, C)
     * @param numero Número de posición (1-60)
     * @return Código formateado (ej: A-12, B-05)
     */
    fun formatCodigoUbicacion(piso: Char, numero: Int): String {
        return "$piso-${numero.toString().padStart(2, '0')}"
    }

    /**
     * Valida si un código de ubicación es válido
     * @param codigo Código a validar
     * @return true si es válido (formato: LETRA-NUMERO, donde LETRA=A-C y NUMERO=1-60)
     */
    fun isValidCodigo(codigo: String): Boolean {
        val regex = Regex("^[ABC]-([1-9]|[1-5][0-9]|60)$")
        return regex.matches(codigo)
    }

    /**
     * Limpia el estado de asignación
     */
    fun clearAsignacionState() {
        _asignacionState.value = UiState.Idle
    }

    /**
     * Limpia la ubicación seleccionada
     */
    fun clearSelectedUbicacion() {
        _selectedUbicacion.value = null
        _ubicacionDetailState.value = UiState.Idle
    }

    /**
     * Limpia el filtro de piso
     */
    fun clearPisoFilter() {
        _pisoSeleccionado.value = null
        getAllUbicaciones()
    }

    // ========== MOCK DATA HELPER ==========

    /**
     * Genera ubicaciones mock para testing
     * En producción esto vendrá del backend
     */
    private fun generateMockUbicaciones(): List<Ubicacion> {
        val ubicaciones = mutableListOf<Ubicacion>()
        var id = 1

        // Generar ubicaciones para cada piso
        listOf('A', 'B', 'C').forEach { piso ->
            for (numero in 1..60) {
                val codigo = formatCodigoUbicacion(piso, numero)
                ubicaciones.add(
                    Ubicacion(
                        idUbicacion = id++,
                        codigoUbicacion = codigo,
                        piso = piso,
                        numero = numero,
                        productos = if (numero % 5 == 0) {
                            // Algunas ubicaciones con productos mock
                            listOf(
                                ProductoEnUbicacion(
                                    sku = "CA30001",
                                    descripcion = "Producto ejemplo",
                                    cantidad = 10
                                )
                            )
                        } else {
                            emptyList()
                        }
                    )
                )
            }
        }

        return ubicaciones
    }
}