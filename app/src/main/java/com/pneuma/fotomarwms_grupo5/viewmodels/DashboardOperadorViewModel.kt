package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import com.pneuma.fotomarwms_grupo5.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

/**
 * Estado de la UI para el Dashboard del Operador
 */
data class DashboardOperadorUiState(
    val nombreUsuario: String = "Operador",
    val mensajeDelJefe: Mensaje? = null,
    val tareasPendientes: List<Tarea> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel que gestiona la lógica del Dashboard del Operador
 *
 * Responsabilidades:
 * - Cargar mensajes del jefe
 * - Cargar tareas asignadas
 * - Actualizar datos en tiempo real (cuando se conecte al backend)
 */
class DashboardOperadorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardOperadorUiState())
    val uiState: StateFlow<DashboardOperadorUiState> = _uiState.asStateFlow()

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

        // Datos de prueba - Mensaje del jefe
        val mensajePrueba = Mensaje(
            id = 1,
            emisor = Usuario(2, "Jefe de Bodega", "jefe@fotomar.cl", UserRole.JEFE),
            destinatario = Usuario(4, "Operador", "operador@fotomar.cl", UserRole.OPERADOR),
            titulo = "Inventario Cámaras",
            contenido = "Recordatorio: Verificar inventario de cámaras Canon en bodega A-2",
            fecha = Date(),
            importante = true
        )

        // Datos de prueba - Tareas pendientes
        val tareasPrueba = listOf(
            Tarea(
                id = 1,
                descripcion = "Conteo físico - Sección A",
                ubicacion = "Bodega A-2",
                prioridad = PrioridadTarea.ALTA,
                fechaAsignacion = Date(),
                asignadoA = Usuario(4, "Operador", "operador@fotomar.cl", UserRole.OPERADOR)
            ),
            Tarea(
                id = 2,
                descripcion = "Ubicar productos recibidos",
                prioridad = PrioridadTarea.NORMAL,
                fechaAsignacion = Date(),
                asignadoA = Usuario(4, "Operador", "operador@fotomar.cl", UserRole.OPERADOR)
            )
        )

        // Actualizar estado
        _uiState.value = DashboardOperadorUiState(
            nombreUsuario = "Operador",
            mensajeDelJefe = mensajePrueba,
            tareasPendientes = tareasPrueba,
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