package com.pneuma.fotomarwms_grupo5.model
/**
 * Modelo de datos para Mensaje
 * Sistema de comunicación entre Jefe/Supervisor y Operadores
 * También incluye notificaciones automáticas del sistema
 */
data class Mensaje(
    val id: Int,
    val titulo: String,
    val contenido: String,
    val importante: Boolean = false,
    val leido: Boolean = false,
    val idRemitente: Int?, // null si es mensaje del sistema
    val remitente: String, // Nombre del remitente o "Sistema"
    val idDestinatario: Int?, // null si es broadcast (para todos)
    val destinatario: String?, // Nombre del destinatario o "Todos"
    val fecha: String, // ISO format: "2025-10-08T14:30:00"
    val tipo: TipoMensaje = TipoMensaje.NORMAL
)

/**
 * Enum para tipos de mensaje
 */
enum class TipoMensaje {
    NORMAL,          // Mensaje regular del jefe
    ALERTA,          // Alerta automática del sistema
    NOTIFICACION,    // Notificación de solicitud/aprobación
    BROADCAST        // Mensaje general para todos
}

/**
 * Request para enviar un mensaje
 */
data class MensajeRequest(
    val idDestinatario: Int?, // null = mensaje para todos (broadcast)
    val titulo: String,
    val contenido: String,
    val importante: Boolean = false
)

/**
 * Resumen de mensajes para el dashboard
 */
data class ResumenMensajes(
    val totalNoLeidos: Int,
    val totalImportantes: Int,
    val ultimoMensaje: Mensaje?
)