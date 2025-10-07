package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import com.pneuma.fotomarwms_grupo5.model.Alerta
import com.pneuma.fotomarwms_grupo5.model.TipoAlerta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la UI para el Dashboard del Jefe
 */
data class DashboardJefeUiState(
    val alertas: List<Alerta> = emptyList(),
    val solicitudesPendientes: Int = 0,
    val aprobadosHoy: Int = 0,
    val isLoading: Boolean = false
)

/**
 * ViewModel que gestiona la lógica del Dashboard del Jefe de Bodega
 *
 * Responsabilidades:
 * - Cargar alertas del sistema (stock bajo, vencimientos, solicitudes)
 * - Mostrar estadísticas de aprobaciones
 * - Actualizar datos en tiempo real
 */
class DashboardJefeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardJefeUiState())
    val uiState: StateFlow<DashboardJefeUiState> = _uiState.asStateFlow()

    init {
        // Cargar datos iniciales
        cargarDatos()
    }

    /**
     * Carga los datos del dashboard desde el backend
     *
     * TODO: Conectar con API cuando esté disponible
     * Por ahora usa datos de prueba
     */
    private fun cargarDatos() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        // Datos de prueba - Alertas del sistema
        val alertasPrueba = listOf(
            Alerta(
                tipo = TipoAlerta.STOCK_BAJO,
                mensaje = "3 productos con stock bajo",
                cantidad = 3
            ),
            Alerta(
                tipo = TipoAlerta.SOLICITUDES_PENDIENTES,
                mensaje = "5 solicitudes pendientes",
                cantidad = 5
            )
        )

        // Actualizar estado
        _uiState.value = DashboardJefeUiState(
            alertas = alertasPrueba,
            solicitudesPendientes = 5,
            aprobadosHoy = 18,
            isLoading = false
        )
    }

    /**
     * Refresca los datos del dashboard
     */
    fun refresh() {
        cargarDatos()
    }
}