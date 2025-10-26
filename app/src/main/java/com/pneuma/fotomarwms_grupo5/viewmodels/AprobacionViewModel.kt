package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application // <-- IMPORTA
import androidx.lifecycle.AndroidViewModel // <-- CAMBIA A ESTE
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.db.AppDatabase // <-- IMPORTA
import com.pneuma.fotomarwms_grupo5.db.entities.SolicitudMovimientoLocal // <-- IMPORTA
import com.pneuma.fotomarwms_grupo5.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de aprobaciones
 * Maneja solicitudes de movimientos (INGRESO/EGRESO/REUBICACION)
 * y su flujo de aprobación/rechazo
 */
class AprobacionViewModel(application: Application) : AndroidViewModel(application) {
    // ========== ESTADOS DE APROBACIONES ==========


    // Obtenemos el DAO directamente al crear el ViewModel
    private val solicitudMovimientoDao = AppDatabase.getDatabase(application).solicitudMovimientoDao()

    private val _createSolicitudState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val createSolicitudState: StateFlow<UiState<Boolean>> = _createSolicitudState.asStateFlow()



    private val _aprobacionesState = MutableStateFlow<UiState<List<Aprobacion>>>(UiState.Idle)
    val aprobacionesState: StateFlow<UiState<List<Aprobacion>>> = _aprobacionesState.asStateFlow()

    private val _selectedAprobacion = MutableStateFlow<Aprobacion?>(null)
    val selectedAprobacion: StateFlow<Aprobacion?> = _selectedAprobacion.asStateFlow()

    private val _aprobacionDetailState = MutableStateFlow<UiState<Aprobacion>>(UiState.Idle)
    val aprobacionDetailState: StateFlow<UiState<Aprobacion>> = _aprobacionDetailState.asStateFlow()

    //-------ESTA ERA LA ANTIGUA----------
    //private val _createSolicitudState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    //val createSolicitudState: StateFlow<UiState<Boolean>> = _createSolicitudState.asStateFlow()

    private val _respuestaState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val respuestaState: StateFlow<UiState<Boolean>> = _respuestaState.asStateFlow()

    private val _misSolicitudesState = MutableStateFlow<UiState<List<Aprobacion>>>(UiState.Idle)
    val misSolicitudesState: StateFlow<UiState<List<Aprobacion>>> = _misSolicitudesState.asStateFlow()

    private val _estadoFiltro = MutableStateFlow<EstadoAprobacion?>(null)
    val estadoFiltro: StateFlow<EstadoAprobacion?> = _estadoFiltro.asStateFlow()

    // ========== CONSULTA DE APROBACIONES ==========

    /**
     * Obtiene todas las solicitudes de aprobación
     * Conecta con: GET /api/aprobaciones
     * Solo para JEFE/SUPERVISOR
     */
    fun getAllAprobaciones() {
        viewModelScope.launch {
            try {
                _aprobacionesState.value = UiState.Loading

                // TODO: Conectar con backend
                // val aprobaciones = aprobacionRepository.getAll()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(600)

                val mockAprobaciones = generateMockAprobaciones()
                _aprobacionesState.value = UiState.Success(mockAprobaciones)

            } catch (e: Exception) {
                _aprobacionesState.value = UiState.Error(
                    message = "Error al obtener aprobaciones: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene aprobaciones filtradas por estado
     * Conecta con: GET /api/aprobaciones?estado={PENDIENTE|APROBADO|RECHAZADO}
     * @param estado Estado para filtrar
     */
    fun getAprobacionesByEstado(estado: EstadoAprobacion) {
        viewModelScope.launch {
            try {
                _aprobacionesState.value = UiState.Loading
                _estadoFiltro.value = estado

                // TODO: Conectar con backend
                // val aprobaciones = aprobacionRepository.getByEstado(estado.name)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(400)

                val mockAprobaciones = generateMockAprobaciones().filter {
                    it.estado == estado
                }

                _aprobacionesState.value = UiState.Success(mockAprobaciones)

            } catch (e: Exception) {
                _aprobacionesState.value = UiState.Error(
                    message = "Error al obtener aprobaciones: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene una aprobación específica por ID
     * Conecta con: GET /api/aprobaciones/{id}
     * @param id ID de la aprobación
     */
    fun getAprobacionById(id: Int) {
        viewModelScope.launch {
            try {
                _aprobacionDetailState.value = UiState.Loading

                // TODO: Conectar con backend
                // val aprobacion = aprobacionRepository.getById(id)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(300)

                val mockAprobacion = Aprobacion(
                    id = id,
                    tipoMovimiento = TipoMovimiento.INGRESO,
                    sku = "CA30001",
                    cantidad = 10,
                    motivo = "Reposición de stock",
                    estado = EstadoAprobacion.PENDIENTE,
                    solicitante = "Juan Pérez",
                    idSolicitante = 5,
                    aprobador = null,
                    idAprobador = null,
                    observaciones = null,
                    fechaSolicitud = "2025-10-08T11:30:00",
                    fechaRespuesta = null,
                    idUbicacionOrigen = null,
                    idUbicacionDestino = null,
                    ubicacionOrigen = null,
                    ubicacionDestino = null
                )

                _selectedAprobacion.value = mockAprobacion
                _aprobacionDetailState.value = UiState.Success(mockAprobacion)

            } catch (e: Exception) {
                _aprobacionDetailState.value = UiState.Error(
                    message = "Error al obtener aprobación: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene las solicitudes realizadas por el usuario actual
     * Conecta con: GET /api/aprobaciones/mis-solicitudes
     * Para que OPERADORES vean el estado de sus solicitudes
     */
    fun getMisSolicitudes() {
        viewModelScope.launch {
            try {
                _misSolicitudesState.value = UiState.Loading

                // TODO: Conectar con backend
                // val solicitudes = aprobacionRepository.getMisSolicitudes()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                val mockSolicitudes = generateMockAprobaciones().take(3)
                _misSolicitudesState.value = UiState.Success(mockSolicitudes)

            } catch (e: Exception) {
                _misSolicitudesState.value = UiState.Error(
                    message = "Error al obtener mis solicitudes: ${e.message}"
                )
            }
        }
    }

    // ========== CREAR SOLICITUDES ==========

    /**
     * Crea una nueva solicitud de INGRESO
     * Conecta con: POST /api/aprobaciones
     * @param sku SKU del producto
     * @param cantidad Cantidad a ingresar
     * @param motivo Razón del ingreso
     */
    // --- INGRESO ---
    fun createSolicitudIngreso(sku: String, cantidad: Int, motivo: String) {
        viewModelScope.launch { // Ejecutar en segundo plano
            try {
                _createSolicitudState.value = UiState.Loading
                val solicitudLocal = SolicitudMovimientoLocal(
                    tipoMovimiento = TipoMovimiento.INGRESO.name,
                    sku = sku,
                    cantidad = cantidad,
                    motivo = motivo,
                    idUbicacionOrigen = null,
                    idUbicacionDestino = null
                    // idLocal y timestamp usan sus valores por defecto
                )
                // 1. GUARDAR LOCALMENTE PRIMERO
                val idGenerado = solicitudMovimientoDao.insertarSolicitud(solicitudLocal)
                println("Solicitud ${idGenerado} guardada localmente.") // Mensaje de prueba

                // 2. (FUTURO) INTENTAR ENVIAR AL BACKEND
                // val exitoBackend = enviarAlBackend(solicitudLocal) // Función imaginaria

                // 3. (FUTURO) SI BACKEND RESPONDE OK (200), BORRAR LOCALMENTE
                // if (exitoBackend) {
                //     borrarSolicitudLocalmente(idGenerado)
                // } else {
                //     // Marcar como fallido o dejar pendiente para reintentar
                // }

                _createSolicitudState.value = UiState.Success(true) // Éxito (al menos local)
            } catch (e: Exception) {
                _createSolicitudState.value = UiState.Error("Error al guardar localmente: ${e.message}")
            }
        }
    }

    /**
     * Crea una nueva solicitud de EGRESO
     * Conecta con: POST /api/aprobaciones
     * @param sku SKU del producto
     * @param cantidad Cantidad a egresar
     * @param motivo Razón del egreso
     */
    // --- EGRESO (similar) ---
    fun createSolicitudEgreso(sku: String, cantidad: Int, motivo: String) {
        viewModelScope.launch {
            try {
                _createSolicitudState.value = UiState.Loading
                // --- REEMPLAZA EL COMENTARIO CON ESTO: ---
                val solicitudLocal = SolicitudMovimientoLocal(
                    tipoMovimiento = TipoMovimiento.EGRESO.name, // <-- Indica que es Egreso
                    sku = sku,                  // <-- Usa el sku que recibe la función
                    cantidad = cantidad,        // <-- Usa la cantidad que recibe la función
                    motivo = motivo,             // <-- Usa el motivo que recibe la función
                    idUbicacionOrigen = null, // <-- Para Egreso, no hay origen específico (se asume almacén general) o destino. Ponemos null.
                    idUbicacionDestino = null  // <-- Para Egreso, no hay destino específico. Ponemos null.
                )
                // --- FIN DEL REEMPLAZO ---

                val idGenerado = solicitudMovimientoDao.insertarSolicitud(solicitudLocal)
                println("✅ Solicitud EGRESO ${idGenerado} guardada localmente.")
                // (FUTURO: Lógica Backend + Borrado si OK)
                _createSolicitudState.value = UiState.Success(true)
            } catch (e: Exception) { /* ... manejo error ... */ }
        }
    }

    /**
     * Crea una nueva solicitud de REUBICACION
     * Conecta con: POST /api/aprobaciones
     * @param sku SKU del producto
     * @param cantidad Cantidad a reubicar
     * @param motivo Razón de la reubicación
     * @param idUbicacionOrigen ID de ubicación origen
     * @param idUbicacionDestino ID de ubicación destino
     */
    // --- REUBICACION (similar) ---
    fun createSolicitudReubicacion(sku: String, cantidad: Int, motivo: String, idUbicacionOrigen: Int, idUbicacionDestino: Int) {
        viewModelScope.launch {
            try {
                _createSolicitudState.value = UiState.Loading
                val solicitudLocal = SolicitudMovimientoLocal(
                    tipoMovimiento = TipoMovimiento.REUBICACION.name, // <-- Tipo REUBICACION
                    sku = sku,                     // <-- Usa el sku que recibe la función
                    cantidad = cantidad,           // <-- Usa la cantidad que recibe la función
                    motivo = motivo,               // <-- Usa el motivo que recibe la función
                    idUbicacionOrigen = idUbicacionOrigen, // <-- Usa el origen que recibe la función
                    idUbicacionDestino = idUbicacionDestino // <-- Usa el destino que recibe la función
                )
                // --- Fin de la creación del objeto ---

                // 1. GUARDA EN SQLITE
                val idGenerado = solicitudMovimientoDao.insertarSolicitud(solicitudLocal)
                println("✅ Solicitud REUBICACION ${idGenerado} guardada localmente.") // Mensaje de prueba
                // 2. (FUTURO) LLAMADA AL BACKEND...
                // 3. (FUTURO) BORRAR SI BACKEND OK...
                _createSolicitudState.value = UiState.Success(true)
            } catch (e: Exception) { /* ... manejo error ... */ }
        }
    }

    // ========== APROBAR/RECHAZAR SOLICITUDES ==========

    /**
     * Aprueba una solicitud
     * Conecta con: PUT /api/aprobaciones/{id}/aprobar
     * Solo para JEFE/SUPERVISOR
     * @param id ID de la solicitud
     * @param observaciones Comentarios opcionales
     */
    fun aprobarSolicitud(id: Int, observaciones: String? = null) {
        viewModelScope.launch {
            try {
                _respuestaState.value = UiState.Loading

                val request = RespuestaAprobacionRequest(observaciones)

                // TODO: Conectar con backend
                // aprobacionRepository.aprobar(id, request)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                _respuestaState.value = UiState.Success(true)

                // Recargar lista de aprobaciones
                if (_estadoFiltro.value != null) {
                    getAprobacionesByEstado(_estadoFiltro.value!!)
                } else {
                    getAllAprobaciones()
                }

            } catch (e: Exception) {
                _respuestaState.value = UiState.Error(
                    message = "Error al aprobar solicitud: ${e.message}"
                )
            }
        }
    }

    /**
     * Rechaza una solicitud
     * Conecta con: PUT /api/aprobaciones/{id}/rechazar
     * Solo para JEFE/SUPERVISOR
     * @param id ID de la solicitud
     * @param observaciones Razón del rechazo (obligatorio)
     */
    fun rechazarSolicitud(id: Int, observaciones: String) {
        viewModelScope.launch {
            try {
                _respuestaState.value = UiState.Loading

                val request = RespuestaAprobacionRequest(observaciones)

                // TODO: Conectar con backend
                // aprobacionRepository.rechazar(id, request)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                _respuestaState.value = UiState.Success(true)

                // Recargar lista de aprobaciones
                if (_estadoFiltro.value != null) {
                    getAprobacionesByEstado(_estadoFiltro.value!!)
                } else {
                    getAllAprobaciones()
                }

            } catch (e: Exception) {
                _respuestaState.value = UiState.Error(
                    message = "Error al rechazar solicitud: ${e.message}"
                )
            }
        }
    }

    // ========== UTILIDADES ==========

    /**
     * Limpia el estado de creación de solicitud
     */
    fun clearCreateState() {
        _createSolicitudState.value = UiState.Idle
    }

    /**
     * Limpia el estado de respuesta
     */
    fun clearRespuestaState() {
        _respuestaState.value = UiState.Idle
    }

    /**
     * Limpia el filtro de estado
     */
    fun clearEstadoFilter() {
        _estadoFiltro.value = null
        getAllAprobaciones()
    }

    /**
     * Limpia la aprobación seleccionada
     */
    fun clearSelectedAprobacion() {
        _selectedAprobacion.value = null
        _aprobacionDetailState.value = UiState.Idle
    }

    // ========== MOCK DATA HELPER ==========

    private fun generateMockAprobaciones(): List<Aprobacion> {
        return listOf(
            Aprobacion(
                id = 1,
                tipoMovimiento = TipoMovimiento.INGRESO,
                sku = "CAM001",
                cantidad = 10,
                motivo = "Reposición de stock",
                estado = EstadoAprobacion.PENDIENTE,
                solicitante = "Juan Pérez",
                idSolicitante = 5,
                aprobador = null,
                idAprobador = null,
                observaciones = null,
                fechaSolicitud = "2025-10-08T11:30:00",
                fechaRespuesta = null,
                idUbicacionOrigen = null,
                idUbicacionDestino = null,
                ubicacionOrigen = null,
                ubicacionDestino = null
            ),
            Aprobacion(
                id = 2,
                tipoMovimiento = TipoMovimiento.EGRESO,
                sku = "LENS001",
                cantidad = 3,
                motivo = "Venta cliente corporativo",
                estado = EstadoAprobacion.PENDIENTE,
                solicitante = "María García",
                idSolicitante = 6,
                aprobador = null,
                idAprobador = null,
                observaciones = null,
                fechaSolicitud = "2025-10-08T07:15:00",
                fechaRespuesta = null,
                idUbicacionOrigen = null,
                idUbicacionDestino = null,
                ubicacionOrigen = null,
                ubicacionDestino = null
            )
        )
    }
}