package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.pneuma.fotomarwms_grupo5.db.AppDatabase
import com.pneuma.fotomarwms_grupo5.db.entities.MensajeLocal

/**
 * ViewModel para sistema de mensajería
 * Maneja mensajes entre Jefe/Supervisor y Operadores
 * También gestiona notificaciones automáticas del sistema
 */
class MensajeViewModel(application: Application) : AndroidViewModel(application) {
    // ========== ESTADOS DE MENSAJES ==========

    // Obtener el DAO de mensajes pendientes
    private val mensajeDao = AppDatabase.getDatabase(application).mensajeDao()
    private val _mensajesState = MutableStateFlow<UiState<List<Mensaje>>>(UiState.Idle)
    val mensajesState: StateFlow<UiState<List<Mensaje>>> = _mensajesState.asStateFlow()

    private val _selectedMensaje = MutableStateFlow<Mensaje?>(null)
    val selectedMensaje: StateFlow<Mensaje?> = _selectedMensaje.asStateFlow()

    private val _resumenState = MutableStateFlow<UiState<ResumenMensajes>>(UiState.Idle)
    val resumenState: StateFlow<UiState<ResumenMensajes>> = _resumenState.asStateFlow()

    private val _enviarMensajeState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val enviarMensajeState: StateFlow<UiState<Boolean>> = _enviarMensajeState.asStateFlow()

    private val _mensajesEnviadosState = MutableStateFlow<UiState<List<Mensaje>>>(UiState.Idle)
    val mensajesEnviadosState: StateFlow<UiState<List<Mensaje>>> = _mensajesEnviadosState.asStateFlow()

    // ========== CONSULTA DE MENSAJES ==========

    /**
     * Obtiene todos los mensajes del usuario actual
     * Conecta con: GET /api/mensajes
     */
    fun getMensajes() {
        viewModelScope.launch {
            try {
                _mensajesState.value = UiState.Loading

                // TODO: Conectar con backend
                // val mensajes = mensajeRepository.getMensajes()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                val mockMensajes = generateMockMensajes()
                _mensajesState.value = UiState.Success(mockMensajes)

            } catch (e: Exception) {
                _mensajesState.value = UiState.Error(
                    message = "Error al obtener mensajes: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene solo mensajes no leídos
     * Conecta con: GET /api/mensajes?soloNoLeidos=true
     */
    fun getMensajesNoLeidos() {
        viewModelScope.launch {
            try {
                _mensajesState.value = UiState.Loading

                // TODO: Conectar con backend
                // val mensajes = mensajeRepository.getMensajes(soloNoLeidos = true)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(400)

                val mockMensajes = generateMockMensajes().filter { !it.leido }
                _mensajesState.value = UiState.Success(mockMensajes)

            } catch (e: Exception) {
                _mensajesState.value = UiState.Error(
                    message = "Error al obtener mensajes no leídos: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene solo mensajes importantes
     * Conecta con: GET /api/mensajes?soloImportantes=true
     */
    fun getMensajesImportantes() {
        viewModelScope.launch {
            try {
                _mensajesState.value = UiState.Loading

                // TODO: Conectar con backend
                // val mensajes = mensajeRepository.getMensajes(soloImportantes = true)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(400)

                val mockMensajes = generateMockMensajes().filter { it.importante }
                _mensajesState.value = UiState.Success(mockMensajes)

            } catch (e: Exception) {
                _mensajesState.value = UiState.Error(
                    message = "Error al obtener mensajes importantes: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene el resumen de mensajes para el dashboard
     * Conecta con: GET /api/mensajes/resumen
     * Muestra cantidad de no leídos, importantes, etc.
     */
    fun getResumenMensajes() {
        viewModelScope.launch {
            try {
                _resumenState.value = UiState.Loading

                // TODO: Conectar con backend
                // val resumen = mensajeRepository.getResumen()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(300)

                val mensajes = generateMockMensajes()
                val resumen = ResumenMensajes(
                    totalNoLeidos = mensajes.count { !it.leido },
                    totalImportantes = mensajes.count { it.importante && !it.leido },
                    ultimoMensaje = mensajes.firstOrNull()
                )

                _resumenState.value = UiState.Success(resumen)

            } catch (e: Exception) {
                _resumenState.value = UiState.Error(
                    message = "Error al obtener resumen: ${e.message}"
                )
            }
        }
    }

    /**
     * Obtiene mensajes enviados por el usuario actual
     * Conecta con: GET /api/mensajes/enviados
     * Solo para JEFE/SUPERVISOR
     */
    fun getMensajesEnviados() {
        viewModelScope.launch {
            try {
                _mensajesEnviadosState.value = UiState.Loading

                // TODO: Conectar con backend
                // val mensajes = mensajeRepository.getMensajesEnviados()

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(500)

                val mockEnviados = listOf(
                    Mensaje(
                        id = 10,
                        titulo = "Reunión de equipo",
                        contenido = "Reunión general el viernes a las 10:00",
                        importante = true,
                        leido = true,
                        idRemitente = 2,
                        remitente = "Yo",
                        idDestinatario = null,
                        destinatario = "Todos",
                        fecha = "2025-10-07T14:00:00",
                        tipo = TipoMensaje.BROADCAST
                    )
                )

                _mensajesEnviadosState.value = UiState.Success(mockEnviados)

            } catch (e: Exception) {
                _mensajesEnviadosState.value = UiState.Error(
                    message = "Error al obtener mensajes enviados: ${e.message}"
                )
            }
        }
    }

    // ========== ACCIONES SOBRE MENSAJES ==========

    /**
     * Marca un mensaje como leído
     * Conecta con: PUT /api/mensajes/{id}/marcar-leido
     * @param id ID del mensaje
     */
    fun marcarComoLeido(id: Int) {
        viewModelScope.launch {
            try {
                // TODO: Conectar con backend
                // mensajeRepository.marcarLeido(id)

                // MOCK TEMPORAL - Actualizar estado local
                kotlinx.coroutines.delay(200)

                // Actualizar el mensaje en la lista
                val mensajesActuales = (_mensajesState.value as? UiState.Success)?.data
                mensajesActuales?.let { lista ->
                    val nuevaLista = lista.map { mensaje ->
                        if (mensaje.id == id) {
                            mensaje.copy(leido = true)
                        } else {
                            mensaje
                        }
                    }
                    _mensajesState.value = UiState.Success(nuevaLista)
                }

                // Actualizar resumen
                getResumenMensajes()

            } catch (e: Exception) {
                // Silenciar error, no es crítico
            }
        }
    }

    /**
     * Cambia el estado de importancia de un mensaje
     * Conecta con: PUT /api/mensajes/{id}/toggle-importante
     * Solo para JEFE/SUPERVISOR
     * @param id ID del mensaje
     */
    fun toggleImportante(id: Int) {
        viewModelScope.launch {
            try {
                // TODO: Conectar con backend
                // mensajeRepository.toggleImportante(id)

                // MOCK TEMPORAL
                kotlinx.coroutines.delay(200)

                // Actualizar el mensaje en la lista
                val mensajesActuales = (_mensajesState.value as? UiState.Success)?.data
                mensajesActuales?.let { lista ->
                    val nuevaLista = lista.map { mensaje ->
                        if (mensaje.id == id) {
                            mensaje.copy(importante = !mensaje.importante)
                        } else {
                            mensaje
                        }
                    }
                    _mensajesState.value = UiState.Success(nuevaLista)
                }

            } catch (e: Exception) {
                // Silenciar error
            }
        }
    }

    // ========== ENVIAR MENSAJES ==========

    /**
     * Envía un mensaje a un operador específico
     * Conecta con: POST /api/mensajes
     * Solo para JEFE/SUPERVISOR
     * @param idDestinatario ID del usuario destinatario
     * @param titulo Título del mensaje
     * @param contenido Cuerpo del mensaje
     * @param importante Si el mensaje es importante
     */
    fun enviarMensaje(
        idDestinatario: Int,
        titulo: String,
        contenido: String,
        importante: Boolean = false
    ) {
        viewModelScope.launch { // Ejecutar en segundo plano
            try {
                _enviarMensajeState.value = UiState.Loading

                // 1. Crear el objeto MensajeLocal
                val mensajePendiente = MensajeLocal(
                    idDestinatario = idDestinatario,
                    titulo = titulo,
                    contenido = contenido,
                    importante = importante
                    // idLocal y timestamp se generan automáticamente
                )

                // 2. Guardar en SQLite ANTES de intentar enviar
                val idGenerado = mensajeDao.insertarMensajePendiente(mensajePendiente)
                println("✅ Mensaje individual ${idGenerado} guardado localmente.") // Mensaje de prueba

                // 3. (FUTURO - Lógica Backend)
                // Aquí iría la llamada a tu API para enviar el 'mensajePendiente'
                // val exitoBackend = miApi.enviarMensajeAlBackend(mensajePendiente)

                // 4. (FUTURO - Borrado si Backend OK)
                // if (exitoBackend) {
                //     mensajeDao.borrarMensajePendientePorId(idGenerado)
                //     println("✅ Mensaje ${idGenerado} confirmado por backend y borrado localmente.")
                //     _enviarMensajeState.value = UiState.Success(true)
                // } else {
                //     _enviarMensajeState.value = UiState.Error("Guardado localmente, pero falló el envío al servidor.")
                //     println("⚠️ Falló envío de mensaje ${idGenerado} al backend.")
                // }

                // --- Simulación TEMPORAL: Éxito con guardado local ---
                _enviarMensajeState.value = UiState.Success(true)
                // --- Fin Simulación ---

            } catch (e: Exception) {
                _enviarMensajeState.value = UiState.Error(
                    message = "Error CRÍTICO al guardar mensaje localmente: ${e.message}"
                )
                println("❌ Error CRÍTICO al guardar mensaje localmente: ${e.message}")
            }
        }
    }
    /**
     * Envía un mensaje broadcast (para todos los operadores)
     * Conecta con: POST /api/mensajes (con idDestinatario = null)
     * Solo para JEFE/SUPERVISOR
     * @param titulo Título del mensaje
     * @param contenido Cuerpo del mensaje
     * @param importante Si el mensaje es importante
     */
    fun enviarMensajeBroadcast(
        titulo: String,
        contenido: String,
        importante: Boolean = false
    ) {
        viewModelScope.launch { // Ejecutar en segundo plano
            try {
                _enviarMensajeState.value = UiState.Loading

                // 1. Crear el objeto MensajeLocal (idDestinatario es null para broadcast)
                val mensajePendiente = MensajeLocal(
                    idDestinatario = null, // null indica broadcast
                    titulo = titulo,
                    contenido = contenido,
                    importante = importante
                )

                // 2. Guardar en SQLite ANTES de intentar enviar
                val idGenerado = mensajeDao.insertarMensajePendiente(mensajePendiente)
                println("✅ Mensaje broadcast ${idGenerado} guardado localmente.") // Mensaje de prueba

                // 3. (FUTURO - Lógica Backend)
                // Aquí iría la llamada a tu API para enviar el 'mensajePendiente' (tipo broadcast)
                // val exitoBackend = miApi.enviarMensajeAlBackend(mensajePendiente)

                // 4. (FUTURO - Borrado si Backend OK)
                // if (exitoBackend) {
                //     mensajeDao.borrarMensajePendientePorId(idGenerado)
                //     println("✅ Mensaje broadcast ${idGenerado} confirmado y borrado localmente.")
                //     _enviarMensajeState.value = UiState.Success(true)
                // } else {
                //     _enviarMensajeState.value = UiState.Error("Guardado localmente, pero falló el envío broadcast.")
                //     println("⚠️ Falló envío de mensaje broadcast ${idGenerado} al backend.")
                // }

                // --- Simulación TEMPORAL: Éxito con guardado local ---
                _enviarMensajeState.value = UiState.Success(true)
                // --- Fin Simulación ---

            } catch (e: Exception) {
                _enviarMensajeState.value = UiState.Error(
                    message = "Error CRÍTICO al guardar mensaje broadcast localmente: ${e.message}"
                )
                println("❌ Error CRÍTICO al guardar mensaje broadcast localmente: ${e.message}")
            }
        }
    }

    // ========== UTILIDADES ==========

    /**
     * Selecciona un mensaje para ver su detalle
     */
    fun selectMensaje(mensaje: Mensaje) {
        _selectedMensaje.value = mensaje

        // Marcar como leído automáticamente al abrir
        if (!mensaje.leido) {
            marcarComoLeido(mensaje.id)
        }
    }

    /**
     * Limpia el mensaje seleccionado
     */
    fun clearSelectedMensaje() {
        _selectedMensaje.value = null
    }

    /**
     * Limpia el estado de envío
     */
    fun clearEnviarState() {
        _enviarMensajeState.value = UiState.Idle
    }

    // ========== MOCK DATA HELPER ==========

    private fun generateMockMensajes(): List<Mensaje> {
        return listOf(
            Mensaje(
                id = 1,
                titulo = "Verificar inventario de cámaras",
                contenido = "Recordatorio: Verificar inventario de cámaras Canon en bodega A-2. Hacer 2 horas",
                importante = false,
                leido = false,
                idRemitente = 2,
                remitente = "Jefe de Bodega",
                idDestinatario = 4,
                destinatario = "Operador",
                fecha = "2025-10-08T11:30:00",
                tipo = TipoMensaje.NORMAL
            ),
            Mensaje(
                id = 2,
                titulo = "Stock bajo",
                contenido = "3 productos con stock bajo: CA30001, FL30001, AP30002",
                importante = true,
                leido = false,
                idRemitente = null,
                remitente = "Sistema",
                idDestinatario = 2,
                destinatario = "Jefe de Bodega",
                fecha = "2025-10-08T09:00:00",
                tipo = TipoMensaje.ALERTA
            ),
            Mensaje(
                id = 3,
                titulo = "Conteo físico - Sección A",
                contenido = "Realizar conteo completo de productos en estantes A-1 a A-5. Asignado por: Fecha límite",
                importante = false,
                leido = true,
                idRemitente = 2,
                remitente = "Jefe de Bodega",
                idDestinatario = 4,
                destinatario = "Operador",
                fecha = "2025-10-07T15:45:00",
                tipo = TipoMensaje.NORMAL
            ),
            Mensaje(
                id = 4,
                titulo = "Excelente trabajo en el conteo de ayer",
                contenido = "Excelente trabajo en el conteo de ayer. Continúa con sección B mañana.",
                importante = false,
                leido = true,
                idRemitente = 2,
                remitente = "Jefe de Bodega",
                idDestinatario = 4,
                destinatario = "Operador",
                fecha = "2025-10-07T07:15:00",
                tipo = TipoMensaje.NORMAL
            ),
            Mensaje(
                id = 5,
                titulo = "Nuevo solicitud de egreso pendiente",
                contenido = "Nueva solicitud de egreso pendiente de procesamiento: LENS001",
                importante = true,
                leido = false,
                idRemitente = null,
                remitente = "Sistema",
                idDestinatario = 2,
                destinatario = "Jefe de Bodega",
                fecha = "2025-10-06T06:45:00",
                tipo = TipoMensaje.NOTIFICACION
            )
        )
    }
}