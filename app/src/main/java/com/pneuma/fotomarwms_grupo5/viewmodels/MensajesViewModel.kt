package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import com.pneuma.fotomarwms_grupo5.model.Mensaje
import com.pneuma.fotomarwms_grupo5.model.UserRole
import com.pneuma.fotomarwms_grupo5.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

/**
 * Estado de la UI para la pantalla de Mensajes
 */
data class MensajesUiState(
    val mensajes: List<Mensaje> = emptyList(),
    val tareas: List<String> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel que gestiona la lógica de mensajes
 *
 * Responsabilidades:
 * - Cargar mensajes del jefe
 * - Cargar tareas pendientes
 * - Marcar mensajes como leídos
 */
class MensajesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MensajesUiState())
    val uiState: StateFlow<MensajesUiState> = _uiState.asStateFlow()

    init {
        cargarMensajes()
    }

    /**
     * Carga los mensajes desde el backend
     *
     * TODO: Conectar con API cuando esté disponible
     */
    private fun cargarMensajes() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        // Datos de prueba
        val jefe = Usuario(2, "Jefe de Bodega", "jefe@fotomar.cl", UserRole.JEFE)
        val sistema = Usuario(0, "Sistema", "sistema@fotomar.cl", UserRole.ADMIN)

        val mensajesPrueba = listOf(
            Mensaje(
                id = 1,
                emisor = jefe,
                titulo = "Inventario Cámaras",
                contenido = "Recordatorio: El inventario de cámaras Canon debe completarse antes del viernes. Continúa con la sección B mañana.",
                fecha = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000), // hace 2 horas
                leido = false,
                importante = true
            ),
            Mensaje(
                id = 2,
                emisor = jefe,
                titulo = "Buen trabajo",
                contenido = "Excelente trabajo en el conteo de ayer. Continúa con la sección B mañana.",
                fecha = Date(System.currentTimeMillis() - 7 * 60 * 60 * 1000), // hace 7 horas
                leido = true,
                importante = false
            ),
            Mensaje(
                id = 3,
                emisor = sistema,
                titulo = "Nueva solicitud",
                contenido = "Nueva solicitud de egreso pendiente de procesamiento.",
                fecha = Date(System.currentTimeMillis() - 6 * 60 * 60 * 1000), // hace 6 horas
                leido = true,
                importante = false
            )
        )

        val tareasPrueba = listOf(
            "Conteo físico Sección A",
            "Ubicar productos recibidos"
        )

        _uiState.value = MensajesUiState(
            mensajes = mensajesPrueba,
            tareas = tareasPrueba,
            isLoading = false
        )
    }

    /**
     * Marca un mensaje como leído
     *
     * @param idMensaje ID del mensaje a marcar
     */
    fun marcarComoLeido(idMensaje: Int) {
        // TODO: Llamar al endpoint para marcar como leído

        // Actualizar estado local
        val mensajesActualizados = _uiState.value.mensajes.map { mensaje ->
            if (mensaje.id == idMensaje) {
                mensaje.copy(leido = true)
            } else {
                mensaje
            }
        }

        _uiState.value = _uiState.value.copy(mensajes = mensajesActualizados)
    }

    /**
     * Refresca la lista de mensajes
     */
    fun refresh() {
        cargarMensajes()
    }
}