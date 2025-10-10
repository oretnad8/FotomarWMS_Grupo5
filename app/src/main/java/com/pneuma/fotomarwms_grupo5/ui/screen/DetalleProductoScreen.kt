package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.models.UiState
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.ProductoViewModel

/**
 * Pantalla de Detalle de Producto
 *
 * Muestra información completa del producto:
 * - SKU, descripción, stock
 * - Códigos de barras (individual y LPN)
 * - Fecha de vencimiento (si aplica)
 * - Lista de ubicaciones donde se encuentra
 * - Historial de movimientos (TODO)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    sku: String,
    productoViewModel: ProductoViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToUbicacion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val productoDetailState by productoViewModel.productoDetailState.collectAsStateWithLifecycle()
    val selectedProducto by productoViewModel.selectedProducto.collectAsStateWithLifecycle()

    // Cargar detalle del producto al iniciar
    LaunchedEffect(sku) {
        productoViewModel.getProductoDetail(sku)
    }

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Detalle del Producto",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = productoDetailState) {
                is UiState.Loading -> {
                    LoadingState(message = "Cargando producto...")
                }

                is UiState.Success -> {
                    val producto = state.data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // ========== INFORMACIÓN PRINCIPAL ==========
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // SKU
                                Text(
                                    text = producto.sku,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                // Descripción
                                Text(
                                    text = producto.descripcion,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Badge de stock
                                StockBadge(stock = producto.stock)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== CÓDIGOS ==========
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Códigos",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Código de barras individual
                                DetailRow(
                                    label = "Código Individual",
                                    value = producto.codigoBarrasIndividual ?: "No disponible"
                                )

                                // LPN
                                DetailRow(
                                    label = "LPN (Código de Caja)",
                                    value = producto.lpn ?: "No disponible"
                                )

                                if (producto.lpnDesc != null) {
                                    DetailRow(
                                        label = "Descripción LPN",
                                        value = producto.lpnDesc
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== FECHA DE VENCIMIENTO ==========
                        if (producto.fechaVencimiento != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (producto.vencimientoCercano)
                                        MaterialTheme.colorScheme.errorContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (producto.vencimientoCercano)
                                            Icons.Default.Warning
                                        else
                                            Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = if (producto.vencimientoCercano)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = if (producto.vencimientoCercano)
                                                "⚠️ Vencimiento Cercano"
                                            else
                                                "Fecha de Vencimiento",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (producto.vencimientoCercano)
                                                MaterialTheme.colorScheme.error
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = producto.fechaVencimiento,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // ========== UBICACIONES ==========
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Ubicaciones",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (producto.ubicaciones.isNullOrEmpty()) {
                                    Text(
                                        text = "Sin ubicación asignada",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    producto.ubicaciones.forEach { ubicacion ->
                                        Card(
                                            onClick = {
                                                onNavigateToUbicacion(ubicacion.codigoUbicacion)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = ubicacion.codigoUbicacion,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        text = "Cantidad: ${ubicacion.cantidadEnUbicacion}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        modifier = Modifier.padding(top = 4.dp)
                                                    )
                                                }

                                                Icon(
                                                    imageVector = Icons.Default.ChevronRight,
                                                    contentDescription = "Ver ubicación"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== ACCIONES ==========
                        // TODO: Agregar botones de acción según el rol
                        // - Jefe: Editar, Eliminar, Registrar movimiento
                        // - Operador: Solicitar movimiento
                    }
                }

                is UiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = {
                            productoViewModel.getProductoDetail(sku)
                        }
                    )
                }

                is UiState.Idle -> {
                    // No mostrar nada en estado Idle
                }
            }
        }
    }
}

/**
 * Fila de detalle (label + value)
 */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}