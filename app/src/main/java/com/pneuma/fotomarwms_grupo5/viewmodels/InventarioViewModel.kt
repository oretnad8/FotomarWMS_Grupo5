package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.db.AppDatabase
import com.pneuma.fotomarwms_grupo5.db.entities.ConteoLocal
import com.pneuma.fotomarwms_grupo5.models.*
import com.pneuma.fotomarwms_grupo5.network.RetrofitClient
import com.pneuma.fotomarwms_grupo5.network.ConteoRequest
import com.pneuma.fotomarwms_grupo5.network.ProgresoInventarioResponse
import com.pneuma.fotomarwms_grupo5.network.DiferenciaInventarioResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de inventario
 * Maneja conteos, diferencias y finalización de inventario
 * USA MICROSERVICIOS REALES - SIN MOCKS
 */
class InventarioViewModel(application: Application) : AndroidViewModel(application) {

    private val conteoDao = AppDatabase.getDatabase(application).conteoDao()
    private val apiService = RetrofitClient.inventarioService

    // ========== ESTADOS ==========

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

    // ========== PROGRESO ==========

    /**
     * Obtiene el progreso del inventario
     * GET http://fotomarwms.ddns.net:8087/api/inventario/progreso
     */
    fun getProgreso() {
        viewModelScope.launch {
            try {
                _progresoState.value = UiState.Loading

                val response = apiService.getProgreso()
                
                if (response.isSuccessful && response.body() != null) {
                    val progreso = response.body()!!.toDomainModel()
                    _progresoState.value = UiState.Success(progreso)
                } else {
                    _progresoState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _progresoState.value = UiState.Error(
                    message = "Error al obtener progreso: ${e.message}"
                )
            }
        }
    }

    // ========== CONTEOS ==========

    /**
     * Registra un conteo individual
     * POST http://fotomarwms.ddns.net:8087/api/inventario/conteo
     * Patrón local-first
     */
    fun registrarConteo(sku: String, idUbicacion: Int, cantidadFisica: Int) {
        viewModelScope.launch {
            try {
                _conteoState.value = UiState.Loading

                // 1. Guardar localmente
                val conteoLocal = ConteoLocal(
                    sku = sku,
                    idUbicacion = idUbicacion,
                    cantidadFisica = cantidadFisica,
                    timestamp = System.currentTimeMillis()
                )
                val localId = conteoDao.insertarConteoPendiente(conteoLocal)

                try {
                    // 2. Enviar al backend
                    val request = ConteoRequest(
                        sku = sku,
                        idUbicacion = idUbicacion,
                        cantidadFisica = cantidadFisica
                    )
                    val response = apiService.registrarConteo(request)
                    
                    if (response.isSuccessful && response.code() == 200) {
                        // 3. Eliminar local si éxito
                        conteoDao.deleteById(localId)
                        _conteoState.value = UiState.Success(true)
                        // Actualizar progreso
                        getProgreso()
                    } else {
                        _conteoState.value = UiState.Error(
                            "Guardado localmente. Error backend: ${response.code()}"
                        )
                    }
                } catch (e: Exception) {
                    _conteoState.value = UiState.Error(
                        "Guardado localmente. Se sincronizará después."
                    )
                }

            } catch (e: Exception) {
                _conteoState.value = UiState.Error(
                    message = "Error al registrar conteo: ${e.message}"
                )
            }
        }
    }

    /**
     * Registra múltiples conteos
     * POST http://fotomarwms.ddns.net:8087/api/inventario/conteo
     * Nota: El API no tiene endpoint batch, se envía uno por uno
     */
    fun registrarConteosBatch(conteos: List<ConteoRequest>) {
        viewModelScope.launch {
            try {
                _conteoState.value = UiState.Loading

                // Enviar cada conteo individualmente
                var successCount = 0
                var errorCount = 0
                
                conteos.forEach { conteo ->
                    try {
                        val response = apiService.registrarConteo(conteo)
                        if (response.isSuccessful) {
                            successCount++
                        } else {
                            errorCount++
                        }
                    } catch (e: Exception) {
                        errorCount++
                    }
                }
                
                if (errorCount == 0) {
                    _conteoState.value = UiState.Success(true)
                    getProgreso()
                } else {
                    _conteoState.value = UiState.Error(
                        "$successCount exitosos, $errorCount fallidos"
                    )
                }

            } catch (e: Exception) {
                _conteoState.value = UiState.Error(
                    message = "Error al registrar conteos: ${e.message}"
                )
            }
        }
    }

    // ========== DIFERENCIAS ==========

    /**
     * Obtiene todas las diferencias de inventario
     * GET http://fotomarwms.ddns.net:8087/api/inventario/diferencias
     */
    fun getDiferencias() {
        viewModelScope.launch {
            try {
                _diferenciasState.value = UiState.Loading

                val response = apiService.getDiferencias()
                
                if (response.isSuccessful && response.body() != null) {
                    val diferencias = response.body()!!.map { it.toDomainModel() }
                    _diferenciasState.value = UiState.Success(diferencias)
                } else {
                    _diferenciasState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _diferenciasState.value = UiState.Error(
                    message = "Error al obtener diferencias: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene solo las diferencias (sin correctos)
     * GET http://fotomarwms.ddns.net:8087/api/inventario/diferencias?soloConDiferencias=true
     */
    fun getDiferenciasConProblemas() {
        getDiferenciasConDiscrepancia()
    }

    /**
     * Obtiene solo las diferencias (sin correctos)
     * GET http://fotomarwms.ddns.net:8087/api/inventario/diferencias?soloConDiferencias=true
     */
    fun getDiferenciasConDiscrepancia() {
        viewModelScope.launch {
            try {
                _diferenciasState.value = UiState.Loading

                val response = apiService.getDiferencias(soloConDiferencias = true)
                
                if (response.isSuccessful && response.body() != null) {
                    val diferencias = response.body()!!.map { it.toDomainModel() }
                    _diferenciasState.value = UiState.Success(diferencias)
                } else {
                    _diferenciasState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _diferenciasState.value = UiState.Error(
                    message = "Error al obtener diferencias: ${e.message}"
                )
            }
        }
    }

    /**
     * Filtra diferencias por tipo (filtro local)
     */
    fun getDiferenciasByTipo(tipo: TipoDiferencia) {
        viewModelScope.launch {
            try {
                _diferenciasState.value = UiState.Loading

                val response = apiService.getDiferencias()
                
                if (response.isSuccessful && response.body() != null) {
                    val diferencias = response.body()!!.map { it.toDomainModel() }
                    val filtered = diferencias.filter { it.tipoDiferencia == tipo }
                    _diferenciasState.value = UiState.Success(filtered)
                } else {
                    _diferenciasState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _diferenciasState.value = UiState.Error(
                    message = "Error al filtrar diferencias: ${e.message}"
                )
            }
        }
    }

    // ========== FINALIZAR ==========

    /**
     * Finaliza el inventario
     * POST http://fotomarwms.ddns.net:8087/api/inventario/finalizar
     */
    fun finalizarInventario() {
        viewModelScope.launch {
            try {
                _finalizarState.value = UiState.Loading

                val response = apiService.finalizarInventario()
                
                if (response.isSuccessful && response.code() == 200) {
                    _finalizarState.value = UiState.Success(true)
                } else {
                    _finalizarState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _finalizarState.value = UiState.Error(
                    message = "Error al finalizar inventario: ${e.message}"
                )
            }
        }
    }

    // ========== UTILIDADES ==========

    fun clearConteoState() {
        _conteoState.value = UiState.Idle
    }

    fun clearDiferencias() {
        _diferenciasState.value = UiState.Idle
    }

    fun clearFinalizarState() {
        _finalizarState.value = UiState.Idle
    }

    fun clearFiltroTipo() {
        _filtroTipoDiferencia.value = null
    }

    // ========== CONVERSIÓN ==========

    /**
     * Convierte ProgresoInventarioResponse a modelo de dominio
     */
    private fun ProgresoInventarioResponse.toDomainModel(): ProgresoInventario {
        return ProgresoInventario(
            totalUbicaciones = this.totalUbicaciones,
            ubicacionesContadas = this.ubicacionesContadas,
            ubicacionesPendientes = this.ubicacionesPendientes,
            porcentajeCompletado = this.porcentajeCompletado,
            totalDiferenciasRegistradas = this.totalDiferenciasRegistradas,
            totalFaltantes = this.totalFaltantes,
            totalSobrantes = this.totalSobrantes,
            ubicacionesConDiferencias = this.ubicacionesConDiferencias
        )
    }

    /**
     * Convierte DiferenciaInventarioResponse a modelo de dominio
     */
    private fun DiferenciaInventarioResponse.toDomainModel(): DiferenciaInventario {
        val tipoDiferencia = when {
            this.diferencia < 0 -> TipoDiferencia.FALTANTE
            this.diferencia > 0 -> TipoDiferencia.SOBRANTE
            else -> TipoDiferencia.CORRECTO
        }
        
        return DiferenciaInventario(
            id = 0, // TODO: El backend debería devolver esto
            sku = this.sku,
            descripcionProducto = this.descripcion,
            idUbicacion = this.idUbicacion,
            codigoUbicacion = this.codigoUbicacion,
            cantidadSistema = this.cantidadSistema,
            cantidadFisica = this.cantidadFisica,
            diferencia = this.diferencia,
            tipoDiferencia = tipoDiferencia,
            fechaConteo = "" // TODO: El backend debería devolver esto
        )
    }
}
