package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import com.pneuma.fotomarwms_grupo5.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

/**
 * Estado de la UI para la pantalla de Aprobaciones
 */
data class AprobacionesUiState(
    val aprobaciones: List<Aprobacion> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel que gestiona la lógica de aprobaciones
 *
 * Responsabilidades:
 * - Cargar solicitudes pendientes, aprobadas y rechazadas
 * - Aprobar o rechazar solicitudes
 * - Enviar notificaciones a operadores
 */
class AprobacionesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AprobacionesUiState())
    val uiState: StateFlow<AprobacionesUiState> = _uiState.asStateFlow()

    init {
        cargarAprobaciones()
    }

    /**
     * Carga todas las solicitudes desde el backend
     *
     * TODO: Conectar con API cuando esté disponible
     */
    private fun cargarAprobaciones() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        // Datos de prueba
        val jefe = Usuario(2, "Jefe de Bodega", "jefe@fotomar.cl", UserRole.JEFE)
        val operador = Usuario(4, "Juan Pérez", "operador@fotomar.cl", UserRole.OPERADOR)

        val aprobacionesPrueba = listOf(
            Aprobacion(
                id = 1,
                tipoMovimiento = TipoMovimiento.INGRESO,
                producto = Producto(
                    sku = "CAM001",
                    descripcion = "Canon EOS R5",
                    stock = 10
                ),
                cantidad = 10,
                motivo = "Reposición de stock",
                solicitante = operador,
                fechaSolicitud = Date(),
                estado = EstadoAprobacion.PENDIENTE
            ),
            Aprobacion(
                id = 2,
                tipoMovimiento = TipoMovimiento.EGRESO,
                producto = Producto(
                    sku = "LENS001",
                    descripcion = "Lente Sony 24-70mm",
                    stock = 5
                ),
                cantidad = 3,
                motivo = "Venta cliente corporativo",
                solicitante = Usuario(5, "María García", "maria@fotomar.cl", UserRole.OPERADOR),
                fechaSolicitud = Date(),
                estado = EstadoAprobacion.PENDIENTE
            ),
            Aprobacion(
                id = 3,
                tipoMovimiento = TipoMovimiento.INGRESO,
                producto = Producto(
                    sku = "TRI001",
                    descripcion = "Trípode Manfrotto",
                    stock = 15
                ),
                cantidad = 20,
                motivo = "Nuevo proveedor",
                solicitante = operador,
                fechaSolicitud = Date(),
                estado = EstadoAprobacion.APROBADO,
                aprobador = jefe,
                fechaAprobacion = Date()
            )
        )

        _uiState.value = AprobacionesUiState(
            aprobaciones = aprobacionesPrueba,
            isLoading = false
        )
    }

    /**
     * Aprueba una solicitud específica
     *
     * @param idAprobacion ID de la solicitud a aprobar
     */
    fun aprobarSolicitud(idAprobacion: Int) {
        // TODO: Llamar al endpoint de aprobación

        // Actualizar estado local
        val aprobacionesActualizadas = _uiState.value.aprobaciones.map { aprobacion ->
            if (aprobacion.id == idAprobacion) {
                aprobacion.copy(
                    estado = EstadoAprobacion.APROBADO,
                    fechaAprobacion = Date()
                )
            } else {
                aprobacion
            }
        }

        _uiState.value = _uiState.value.copy(aprobaciones = aprobacionesActualizadas)

        // TODO: Enviar notificación al operador
    }

    /**
     * Rechaza una solicitud específica
     *
     * @param idAprobacion ID de la solicitud a rechazar
     */
    fun rechazarSolicitud(idAprobacion: Int) {
        // TODO: Llamar al endpoint de rechazo

        // Actualizar estado local
        val aprobacionesActualizadas = _uiState.value.aprobaciones.map { aprobacion ->
            if (aprobacion.id == idAprobacion) {
                aprobacion.copy(
                    estado = EstadoAprobacion.RECHAZADO,
                    fechaAprobacion = Date()
                )
            } else {
                aprobacion
            }
        }

        _uiState.value = _uiState.value.copy(aprobaciones = aprobacionesActualizadas)

        // TODO: Enviar notificación al operador
    }

    /**
     * Refresca la lista de aprobaciones
     */
    fun refresh() {
        cargarAprobaciones()
    }
}