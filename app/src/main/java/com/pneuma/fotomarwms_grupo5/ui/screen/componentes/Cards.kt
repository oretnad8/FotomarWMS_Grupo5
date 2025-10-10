package com.pneuma.fotomarwms_grupo5.ui.screen.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pneuma.fotomarwms_grupo5.models.*

/**
 * Card para mostrar un producto en resultados de búsqueda
 */
@Composable
fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // SKU y descripción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = producto.sku,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = producto.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Badge de stock
                StockBadge(stock = producto.stock)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Ubicaciones
                if (!producto.ubicaciones.isNullOrEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${producto.ubicaciones.size} ubicación(es)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Alerta de vencimiento
                if (producto.vencimientoCercano) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Vencimiento cercano",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFA726) // Naranja
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Vence pronto",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFA726)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card para mostrar un mensaje
 */
@Composable
fun MensajeCard(
    mensaje: Mensaje,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!mensaje.leido)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (mensaje.importante) BorderStroke(2.dp, Color(0xFFFFA726)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono según tipo de mensaje
            Icon(
                imageVector = when (mensaje.tipo) {
                    TipoMensaje.ALERTA -> Icons.Default.Warning
                    TipoMensaje.NOTIFICACION -> Icons.Default.Notifications
                    TipoMensaje.BROADCAST -> Icons.Default.Campaign
                    else -> Icons.Default.Email
                },
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (mensaje.importante) Color(0xFFFFA726)
                else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Título
                Text(
                    text = mensaje.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (!mensaje.leido) FontWeight.Bold else FontWeight.Normal
                )

                // Remitente y fecha
                Text(
                    text = "${mensaje.remitente} • Hace 2 horas", // TODO: Formatear fecha
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Preview del contenido
                Text(
                    text = mensaje.contenido.take(80) + if (mensaje.contenido.length > 80) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Indicador de no leído
            if (!mensaje.leido) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    }
}

/**
 * Card para mostrar una solicitud de aprobación
 */
@Composable
fun AprobacionCard(
    aprobacion: Aprobacion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Tipo de movimiento y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (aprobacion.tipoMovimiento) {
                            TipoMovimiento.INGRESO -> Icons.Default.ArrowDownward
                            TipoMovimiento.EGRESO -> Icons.Default.ArrowUpward
                            TipoMovimiento.REUBICACION -> Icons.Default.SwapHoriz
                        },
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = aprobacion.tipoMovimiento.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                EstadoBadge(estado = aprobacion.estado)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SKU y cantidad
            Text(
                text = "Producto: ${aprobacion.sku}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Cantidad: ${aprobacion.cantidad}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Motivo
            Text(
                text = aprobacion.motivo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Solicitante y fecha
            Text(
                text = "Solicitado por: ${aprobacion.solicitante}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Card para mostrar una ubicación
 */
@Composable
fun UbicacionCard(
    ubicacion: Ubicacion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Código de ubicación
            Text(
                text = ubicacion.codigoUbicacion,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cantidad de productos
            val cantidadProductos = ubicacion.productos?.size ?: 0
            Text(
                text = if (cantidadProductos > 0)
                    "$cantidadProductos producto(s)"
                else
                    "Vacía",
                style = MaterialTheme.typography.bodyMedium,
                color = if (cantidadProductos > 0)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}