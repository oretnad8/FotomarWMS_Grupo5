package com.pneuma.fotomarwms_grupo5.network.models

// Para POST /api/mensajes
data class MensajeRequest(
    val idDestinatario: Int? = null, // null para enviar a todos
    val titulo: String,
    val contenido: String,
    val importante: Boolean? = false
)

// Respuesta de GET /api/mensajes/resumen
data class MensajeResumenDTO(
    val totalMensajes: Long,
    val mensajesNoLeidos: Long,
    val mensajesImportantes: Long,
    val mensajesImportantesNoLeidos: Long
)