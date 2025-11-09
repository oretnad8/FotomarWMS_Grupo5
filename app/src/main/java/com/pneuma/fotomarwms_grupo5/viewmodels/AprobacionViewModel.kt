package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.db.AppDatabase
import com.pneuma.fotomarwms_grupo5.db.entities.SolicitudMovimientoLocal
import com.pneuma.fotomarwms_grupo5.models.*
import com.pneuma.fotomarwms_grupo5.network.RetrofitClient
import com.pneuma.fotomarwms_grupo5.network.AprobarRequest
import com.pneuma.fotomarwms_grupo5.network.RechazarRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de aprobaciones
 * Maneja solicitudes de movimientos (INGRESO/EGRESO/REUBICACION)
 * y su flujo de aprobación/rechazo
 * USA MICROSERVICIOS REALES - SIN MOCKS
 */
class AprobacionViewModel(application: Application) : AndroidViewModel(application) {

    private val solicitudMovimientoDao = AppDatabase.getDatabase(application).solicitudMovimientoDao()
    private val apiService = RetrofitClient.aprobacionesService

    // ========== ESTADOS ==========

    private val _aprobacionesState = MutableStateFlow<UiState<List<Aprobacion>>>(UiState.Idle)
    val aprobacionesState: StateFlow<UiState<List<Aprobacion>>> = _aprobacionesState.asStateFlow()

    private val _selectedAprobacion = MutableStateFlow<Aprobacion?>(null)
    val selectedAprobacion: StateFlow<Aprobacion?> = _selectedAprobacion.asStateFlow()

    private val _aprobacionDetailState = MutableStateFlow<UiState<Aprobacion>>(UiState.Idle)
    val aprobacionDetailState: StateFlow<UiState<Aprobacion>> = _aprobacionDetailState.asStateFlow()

    private val _createSolicitudState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val createSolicitudState: StateFlow<UiState<Boolean>> = _createSolicitudState.asStateFlow()

    private val _respuestaState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val respuestaState: StateFlow<UiState<Boolean>> = _respuestaState.asStateFlow()

    private val _misSolicitudesState = MutableStateFlow<UiState<List<Aprobacion>>>(UiState.Idle)
    val misSolicitudesState: StateFlow<UiState<List<Aprobacion>>> = _misSolicitudesState.asStateFlow()

    private val _estadoFiltro = MutableStateFlow<EstadoAprobacion?>(null)
    val estadoFiltro: StateFlow<EstadoAprobacion?> = _estadoFiltro.asStateFlow()

    // ========== CONSULTA DE APROBACIONES ==========

    /**
     * Obtiene todas las solicitudes de aprobación
     * GET http://fotomarwms.ddns.net:8085/api/aprobaciones
     */
    fun getAllAprobaciones() {
        viewModelScope.launch {
            try {
                _aprobacionesState.value = UiState.Loading

                val response = apiService.getAllAprobaciones()
                
                if (response.isSuccessful && response.body() != null) {
                    _aprobacionesState.value = UiState.Success(response.body()!!)
                } else {
                    _aprobacionesState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _aprobacionesState.value = UiState.Error(
                    message = "Error al obtener aprobaciones: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene aprobaciones filtradas por estado
     * GET http://fotomarwms.ddns.net:8085/api/aprobaciones?estado={PENDIENTE|APROBADO|RECHAZADO}
     */
    fun getAprobacionesByEstado(estado: EstadoAprobacion) {
        viewModelScope.launch {
            try {
                _aprobacionesState.value = UiState.Loading
                _estadoFiltro.value = estado

                val response = apiService.getAprobacionesByEstado(estado.name)
                
                if (response.isSuccessful && response.body() != null) {
                    _aprobacionesState.value = UiState.Success(response.body()!!)
                } else {
                    _aprobacionesState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _aprobacionesState.value = UiState.Error(
                    message = "Error al filtrar aprobaciones: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene el detalle de una aprobación
     * GET http://fotomarwms.ddns.net:8085/api/aprobaciones/{id}
     */
    fun getAprobacionDetail(id: Int) {
        viewModelScope.launch {
            try {
                _aprobacionDetailState.value = UiState.Loading

                val response = apiService.getAprobacionById(id)
                
                if (response.isSuccessful && response.body() != null) {
                    val aprobacion = response.body()!!
                    _selectedAprobacion.value = aprobacion
                    _aprobacionDetailState.value = UiState.Success(aprobacion)
                } else {
                    _aprobacionDetailState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _aprobacionDetailState.value = UiState.Error(
                    message = "Error al obtener detalle: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene las solicitudes del usuario actual
     * GET http://fotomarwms.ddns.net:8085/api/aprobaciones/mis-solicitudes
     */
    fun getMisSolicitudes() {
        viewModelScope.launch {
            try {
                _misSolicitudesState.value = UiState.Loading

                val response = apiService.getMisSolicitudes()
                
                if (response.isSuccessful && response.body() != null) {
                    _misSolicitudesState.value = UiState.Success(response.body()!!)
                } else {
                    _misSolicitudesState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _misSolicitudesState.value = UiState.Error(
                    message = "Error al obtener mis solicitudes: ${e.message}"
                )
            }
        }
    }

    // ========== CREACIÓN DE SOLICITUDES ==========

    /**
     * Crea solicitud de INGRESO
     * POST http://fotomarwms.ddns.net:8085/api/aprobaciones/solicitar-ingreso
     * Patrón local-first: guarda local → envía backend → elimina si OK
     */
    fun solicitarIngreso(request: SolicitudIngresoRequest) {
        viewModelScope.launch {
            try {
                _createSolicitudState.value = UiState.Loading

                // 1. Guardar localmente
                val solicitudLocal = SolicitudMovimientoLocal(
                    tipoMovimiento = "INGRESO",
                    sku = request.sku,
                    cantidad = request.cantidad,
                    ubicacionDestino = request.ubicacionDestino,
                    motivo = request.motivo,
                    timestamp = System.currentTimeMillis()
                )
                val localId = solicitudMovimientoDao.insertarSolicitud(solicitudLocal)

                try {
                    // 2. Enviar al backend
                    val response = apiService.solicitarIngreso(request)
                    
                    if (response.isSuccessful && response.code() == 200) {
                        // 3. Eliminar local si éxito
                        solicitudMovimientoDao.deleteById(localId)
                        _createSolicitudState.value = UiState.Success(true)
                    } else {
                        _createSolicitudState.value = UiState.Error(
                            "Guardado localmente. Error backend: ${response.code()}"
                        )
                    }
                } catch (e: Exception) {
                    _createSolicitudState.value = UiState.Error(
                        "Guardado localmente. Se sincronizará después."
                    )
                }

            } catch (e: Exception) {
                _createSolicitudState.value = UiState.Error(
                    message = "Error al crear solicitud: ${e.message}"
                )
            }
        }
    }

    /**
     * Crea solicitud de EGRESO
     * POST http://fotomarwms.ddns.net:8085/api/aprobaciones/solicitar-egreso
     */
    fun solicitarEgreso(request: SolicitudEgresoRequest) {
        viewModelScope.launch {
            try {
                _createSolicitudState.value = UiState.Loading

                val solicitudLocal = SolicitudMovimientoLocal(
                    tipoMovimiento = "EGRESO",
                    sku = request.sku,
                    cantidad = request.cantidad,
                    ubicacionOrigen = request.ubicacionOrigen,
                    motivo = request.motivo,
                    timestamp = System.currentTimeMillis()
                )
                val localId = solicitudMovimientoDao.insertarSolicitud(solicitudLocal)

                try {
                    val response = apiService.solicitarEgreso(request)
                    
                    if (response.isSuccessful && response.code() == 200) {
                        solicitudMovimientoDao.deleteById(localId)
                        _createSolicitudState.value = UiState.Success(true)
                    } else {
                        _createSolicitudState.value = UiState.Error(
                            "Guardado localmente. Error backend: ${response.code()}"
                        )
                    }
                } catch (e: Exception) {
                    _createSolicitudState.value = UiState.Error(
                        "Guardado localmente. Se sincronizará después."
                    )
                }

            } catch (e: Exception) {
                _createSolicitudState.value = UiState.Error(
                    message = "Error al crear solicitud: ${e.message}"
                )
            }
        }
    }

    /**
     * Crea solicitud de REUBICACION
     * POST http://fotomarwms.ddns.net:8085/api/aprobaciones/solicitar-reubicacion
     */
    fun solicitarReubicacion(request: SolicitudReubicacionRequest) {
        viewModelScope.launch {
            try {
                _createSolicitudState.value = UiState.Loading

                val solicitudLocal = SolicitudMovimientoLocal(
                    tipoMovimiento = "REUBICACION",
                    sku = request.sku,
                    cantidad = request.cantidad,
                    ubicacionOrigen = request.ubicacionOrigen,
                    ubicacionDestino = request.ubicacionDestino,
                    motivo = request.motivo,
                    timestamp = System.currentTimeMillis()
                )
                val localId = solicitudMovimientoDao.insertarSolicitud(solicitudLocal)

                try {
                    val response = apiService.solicitarReubicacion(request)
                    
                    if (response.isSuccessful && response.code() == 200) {
                        solicitudMovimientoDao.deleteById(localId)
                        _createSolicitudState.value = UiState.Success(true)
                    } else {
                        _createSolicitudState.value = UiState.Error(
                            "Guardado localmente. Error backend: ${response.code()}"
                        )
                    }
                } catch (e: Exception) {
                    _createSolicitudState.value = UiState.Error(
                        "Guardado localmente. Se sincronizará después."
                    )
                }

            } catch (e: Exception) {
                _createSolicitudState.value = UiState.Error(
                    message = "Error al crear solicitud: ${e.message}"
                )
            }
        }
    }

    // ========== APROBACIÓN/RECHAZO ==========

    /**
     * Aprueba una solicitud
     * POST http://fotomarwms.ddns.net:8085/api/aprobaciones/{id}/aprobar
     */
    fun aprobarSolicitud(id: Int, comentario: String? = null) {
        viewModelScope.launch {
            try {
                _respuestaState.value = UiState.Loading

                val request = AprobarRequest(comentario = comentario)
                val response = apiService.aprobarSolicitud(id, request)
                
                if (response.isSuccessful && response.code() == 200) {
                    _respuestaState.value = UiState.Success(true)
                    // Recargar lista
                    getAllAprobaciones()
                } else {
                    _respuestaState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _respuestaState.value = UiState.Error(
                    message = "Error al aprobar: ${e.message}"
                )
            }
        }
    }

    /**
     * Rechaza una solicitud
     * POST http://fotomarwms.ddns.net:8085/api/aprobaciones/{id}/rechazar
     */
    fun rechazarSolicitud(id: Int, motivo: String) {
        viewModelScope.launch {
            try {
                _respuestaState.value = UiState.Loading

                val request = RechazarRequest(motivo = motivo)
                val response = apiService.rechazarSolicitud(id, request)
                
                if (response.isSuccessful && response.code() == 200) {
                    _respuestaState.value = UiState.Success(true)
                    // Recargar lista
                    getAllAprobaciones()
                } else {
                    _respuestaState.value = UiState.Error(
                        message = "Error ${response.code()}: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _respuestaState.value = UiState.Error(
                    message = "Error al rechazar: ${e.message}"
                )
            }
        }
    }

    // ========== UTILIDADES ==========

    fun clearAprobaciones() {
        _aprobacionesState.value = UiState.Idle
        _estadoFiltro.value = null
    }

    fun clearSelectedAprobacion() {
        _selectedAprobacion.value = null
        _aprobacionDetailState.value = UiState.Idle
    }

    fun clearCreateState() {
        _createSolicitudState.value = UiState.Idle
    }

    fun clearRespuestaState() {
        _respuestaState.value = UiState.Idle
    }
}
