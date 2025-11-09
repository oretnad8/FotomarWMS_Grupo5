package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.db.AppDatabase
import com.pneuma.fotomarwms_grupo5.db.entities.ConteoLocal
import com.pneuma.fotomarwms_grupo5.models.*
import com.pneuma.fotomarwms_grupo5.network.RetrofitClient
import com.pneuma.fotomarwms_grupo5.network.RegistrarConteoRequest
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
                    _progresoState.value = UiState.Success(response.body()!!)
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
    fun registrarConteo(sku: String, ubicacion: String, cantidadContada: Int) {
        viewModelScope.launch {
            try {
                _conteoState.value = UiState.Loading

                // 1. Guardar localmente
                val conteoLocal = ConteoLocal(
                    sku = sku,
                    ubicacion = ubicacion,
                    cantidadContada = cantidadContada,
                    timestamp = System.currentTimeMillis()
                )
                val localId = conteoDao.insertarConteoPendiente(conteoLocal)

                try {
                    // 2. Enviar al backend
                    val request = RegistrarConteoRequest(sku, ubicacion, cantidadContada)
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
     * POST http://fotomarwms.ddns.net:8087/api/inventario/conteos-batch
     */
    fun registrarConteosBatch(conteos: List<RegistrarConteoRequest>) {
        viewModelScope.launch {
            try {
                _conteoState.value = UiState.Loading

                val response = apiService.registrarConteosBatch(conteos)
                
                if (response.isSuccessful && response.code() == 200) {
                    _conteoState.value = UiState.Success(true)
                    // Actualizar progreso
                    getProgreso()
                } else {
                    _conteoState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
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
                    _diferenciasState.value = UiState.Success(response.body()!!)
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
        viewModelScope.launch {
            try {
                _diferenciasState.value = UiState.Loading

                val response = apiService.getDiferenciasConProblemas()
                
                if (response.isSuccessful && response.body() != null) {
                    _diferenciasState.value = UiState.Success(response.body()!!)
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
                    val filtered = response.body()!!.filter { it.tipoDiferencia == tipo }
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
}
