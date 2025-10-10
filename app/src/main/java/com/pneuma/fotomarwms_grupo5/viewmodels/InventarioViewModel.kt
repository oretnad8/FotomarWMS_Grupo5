package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de inventario
 * Maneja conteo físico, progreso, diferencias y finalización de inventario
 */
class InventarioViewModel : ViewModel() {

    // ========== ESTADOS DE INVENTARIO ==========

    private val _progresoState = MutableStateFlow<UiState<ProgresoInventario>>(UiState.Idle)
    val progresoState: StateFlow<UiState<ProgresoInventario>> = _progresoState.asStateFlow()

    private val _conteoState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val conteoState: StateFlow<UiState<Boolean>> = _conteoState.asStateFlow()

    private val _diferenciasState = MutableStateFlow<UiState<List<DiferenciaInventario>>>(UiState.Idle)
    val diferenciasState: StateFlow<UiState<List<DiferenciaInventario>>> = _diferenciasState.asStateFlow()

    private val _finalizarState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val finalizarState: StateFlow<UiState<Boolean>> = _finalizarState.asStateFlow()

    private val _filtroTipoDiferencia = MutableStateFlow<TipoDiferencia?>(null)
    val filtroTipoDiferencia: StateFlow<TipoDiferencia?> = _filtroTipoDiferencia.asStateFlow()

    // ========== CONSULTA DE PROGRESO ==========

    /**
     * Obtiene el progreso actual del inventario
     * Conecta con: GET /api/inventario/progreso
     * Muestra porcentaje de completitud, ubicaciones contadas, diferencias, etc.
     */
    fun getProgreso() {
        viewModelScope.launch {
            try {
                _progresoState.value = UiState.Loading

                // TODO: Conectar con backend
                // val progreso = inventarioRepository.getProgreso()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                val mockProgreso = ProgresoInventario(
                    totalUbicaciones = 180,
                    ubicacionesContadas = 45,
                    ubicacionesPendientes = 135,
                    porcentajeCompletado = 25.0,
                    totalDiferenciasRegistradas = 45,
                    totalFaltantes = 12,
                    totalSobrantes = 8,
                    ubicacionesConDiferencias = 20
                )

                _progresoState.value = UiState.Success(mockProgreso)

            } catch (e: Exception) {
                _progresoState.value = UiState.Error(
                    message = "Error al obtener progreso: ${e.message}"
                )
            }
        }
    }

    // ========== REGISTRO DE CONTEO ==========

    /**
     * Registra un conteo físico para una ubicación y producto específico
     * Conecta con: POST /api/inventario/conteo
     * @param sku SKU del producto contado
     * @param idUbicacion ID de la ubicación
     * @param cantidadFisica Cantidad real encontrada físicamente
     */
    fun registrarConteo(sku: String, idUbicacion: Int, cantidadFisica: Int) {
        viewModelScope.launch {
            try {
                _conteoState.value = UiState.Loading

                val request = ConteoRequest(
                    sku = sku,
                    idUbicacion = idUbicacion,
                    cantidadFisica = cantidadFisica
                )

                // TODO: Conectar con backend
                // inventarioRepository.registrarConteo(request)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                _conteoState.value = UiState.Success(true)

                // Actualizar progreso después del conteo
                getProgreso()

            } catch (e: Exception) {
                _conteoState.value = UiState.Error(
                    message = "Error al registrar conteo: ${e.message}"
                )
            }
        }
    }

    /**
     * Registra múltiples conteos en lote
     * Útil cuando se cuenta toda una ubicación con varios productos
     * @param conteos Lista de conteos a registrar
     */
    fun registrarConteosBatch(conteos: List<ConteoRequest>) {
        viewModelScope.launch {
            try {
                _conteoState.value = UiState.Loading

                // TODO: Conectar con backend (puede necesitar endpoint específico)
                // inventarioRepository.registrarConteosBatch(conteos)

                // MOCK TEMPORAL - Registrar uno por uno
                kotlinx.coroutines.delay(800)

                _conteoState.value = UiState.Success(true)

                // Actualizar progreso
                getProgreso()

            } catch (e: Exception) {
                _conteoState.value = UiState.Error(
                    message = "Error al registrar conteos en lote: ${e.message}"
                )
            }
        }
    }

    // ========== CONSULTA DE DIFERENCIAS ==========

    /**
     * Obtiene todas las diferencias registradas
     * Conecta con: GET /api/inventario/diferencias
     */
    fun getDiferencias() {
        viewModelScope.launch {
            try {
                _diferenciasState.value = UiState.Loading

                // TODO: Conectar con backend
                // val diferencias = inventarioRepository.getDiferencias()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(600)

                val mockDiferencias = generateMockDiferencias()
                _diferenciasState.value = UiState.Success(mockDiferencias)

            } catch (e: Exception) {
                _diferenciasState.value = UiState.Error(
                    message = "Error al obtener diferencias: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene solo las ubicaciones con diferencias
     * Conecta con: GET /api/inventario/diferencias?soloConDiferencias=true
     */
    fun getDiferenciasConDiscrepancia() {
        viewModelScope.launch {
            try {
                _diferenciasState.value = UiState.Loading

                // TODO: Conectar con backend
                // val diferencias = inventarioRepository.getDiferencias(soloConDiferencias = true)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                val mockDiferencias = generateMockDiferencias().filter {
                    it.tipoDiferencia != TipoDiferencia.CORRECTO
                }

                _diferenciasState.value = UiState.Success(mockDiferencias)

            } catch (e: Exception) {
                _diferenciasState.value = UiState.Error(
                    message = "Error al obtener diferencias: ${e.message}"
                )
            }
        }
    }

    /**
     * Filtra diferencias por tipo (FALTANTE, SOBRANTE, CORRECTO)
     * @param tipo Tipo de diferencia a filtrar
     */
    fun getDiferenciasByTipo(tipo: TipoDiferencia) {
        viewModelScope.launch {
            try {
                _diferenciasState.value = UiState.Loading
                _filtroTipoDiferencia.value = tipo

                // TODO: Podría ser un parámetro del endpoint o filtro local

                // MOCK TEMPORAL - Filtro local
                kotlinx.coroutines.delay(300)

                val mockDiferencias = generateMockDiferencias().filter {
                    it.tipoDiferencia == tipo
                }

                _diferenciasState.value = UiState.Success(mockDiferencias)

            } catch (e: Exception) {
                _diferenciasState.value = UiState.Error(
                    message = "Error al filtrar diferencias: ${e.message}"
                )
            }
        }
    }

    // ========== FINALIZAR INVENTARIO ==========

    /**
     * Finaliza el inventario y ajusta el sistema según las diferencias
     * Conecta con: POST /api/inventario/finalizar
     * Solo para JEFE/SUPERVISOR
     * Esta acción actualiza los stocks en el sistema con los conteos físicos
     */
    fun finalizarInventario() {
        viewModelScope.launch {
            try {
                _finalizarState.value = UiState.Loading

                // TODO: Conectar con backend
                // inventarioRepository.finalizarInventario()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(1000)

                _finalizarState.value = UiState.Success(true)

                // Limpiar estados después de finalizar
                _progresoState.value = UiState.Idle
                _diferenciasState.value = UiState.Idle

            } catch (e: Exception) {
                _finalizarState.value = UiState.Error(
                    message = "Error al finalizar inventario: ${e.message}"
                )
            }
        }
    }

    // ========== UTILIDADES ==========

    /**
     * Calcula el porcentaje de progreso
     * @param contadas Ubicaciones ya contadas
     * @param total Total de ubicaciones
     * @return Porcentaje (0.0 - 100.0)
     */
    fun calcularPorcentaje(contadas: Int, total: Int): Double {
        if (total == 0) return 0.0
        return (contadas.toDouble() / total.toDouble()) * 100.0
    }

    /**
     * Determina el tipo de diferencia según las cantidades
     * @param cantidadSistema Cantidad registrada en sistema
     * @param cantidadFisica Cantidad encontrada físicamente
     * @return Tipo de diferencia
     */
    fun calcularTipoDiferencia(cantidadSistema: Int, cantidadFisica: Int): TipoDiferencia {
        return when {
            cantidadFisica < cantidadSistema -> TipoDiferencia.FALTANTE
            cantidadFisica > cantidadSistema -> TipoDiferencia.SOBRANTE
            else -> TipoDiferencia.CORRECTO
        }
    }

    /**
     * Limpia el estado de conteo
     */
    fun clearConteoState() {
        _conteoState.value = UiState.Idle
    }

    /**
     * Limpia el estado de finalización
     */
    fun clearFinalizarState() {
        _finalizarState.value = UiState.Idle
    }

    /**
     * Limpia el filtro de tipo de diferencia
     */
    fun clearFiltroTipo() {
        _filtroTipoDiferencia.value = null
        getDiferencias()
    }

    // ========== MOCK DATA HELPER ==========

    private fun generateMockDiferencias(): List<DiferenciaInventario> {
        return listOf(
            DiferenciaInventario(
                id = 1,
                sku = "CA30001",
                descripcionProducto = "Cámara Canon EOS R5",
                idUbicacion = 12,
                codigoUbicacion = "A-12",
                cantidadSistema = 10,
                cantidadFisica = 8,
                diferencia = -2,
                tipoDiferencia = TipoDiferencia.FALTANTE,
                fechaConteo = "2025-10-08T10:30:00"
            ),
            DiferenciaInventario(
                id = 2,
                sku = "FL30001",
                descripcionProducto = "Flash Canon Speedlite",
                idUbicacion = 8,
                codigoUbicacion = "A-08",
                cantidadSistema = 5,
                cantidadFisica = 7,
                diferencia = 2,
                tipoDiferencia = TipoDiferencia.SOBRANTE,
                fechaConteo = "2025-10-08T10:45:00"
            ),
            DiferenciaInventario(
                id = 3,
                sku = "AP30001",
                descripcionProducto = "Adaptador Canon EF-EOS R",
                idUbicacion = 3,
                codigoUbicacion = "A-03",
                cantidadSistema = 15,
                cantidadFisica = 15,
                diferencia = 0,
                tipoDiferencia = TipoDiferencia.CORRECTO,
                fechaConteo = "2025-10-08T11:00:00"
            ),
            DiferenciaInventario(
                id = 4,
                sku = "LN30001",
                descripcionProducto = "Lente Canon RF 24-70mm",
                idUbicacion = 25,
                codigoUbicacion = "B-25",
                cantidadSistema = 12,
                cantidadFisica = 10,
                diferencia = -2,
                tipoDiferencia = TipoDiferencia.FALTANTE,
                fechaConteo = "2025-10-08T11:15:00"
            )
        )
    }
}