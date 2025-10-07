package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import com.pneuma.fotomarwms_grupo5.model.Estadisticas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la UI para el Dashboard del Administrador
 */
data class DashboardAdminUiState(
    val usuariosActivos: Int = 0,
    val totalProductos: Int = 0,
    val isLoading: Boolean = false
)

/**
 * ViewModel que gestiona la lógica del Dashboard del Administrador
 *
 * Responsabilidades:
 * - Cargar estadísticas generales del sistema
 * - Proveer datos para reportes
 */
class DashboardAdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardAdminUiState())
    val uiState: StateFlow<DashboardAdminUiState> = _uiState.asStateFlow()

    init {
        // Cargar datos iniciales
        cargarDatos()
    }

    /**
     * Carga las estadísticas del sistema desde el backend
     *
     * TODO: Conectar con API cuando esté disponible
     * Por ahora usa datos de prueba
     */
    private fun cargarDatos() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        // Datos de prueba
        _uiState.value = DashboardAdminUiState(
            usuariosActivos = 24,
            totalProductos = 1247,
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