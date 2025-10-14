package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.pneuma.fotomarwms_grupo5.viewmodels.UbicacionViewModel

/**
 * Pantalla de Detalle de Ubicación
 *
 * Muestra:
 * - Código de ubicación
 * - Piso y número
 * - Lista de productos almacenados
 * - Capacidad y ocupación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleUbicacionScreen(
    codigo: String,
    ubicacionViewModel: UbicacionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProducto: (String) -> Unit,
    onNavigateToConteo: (Int) -> Unit,   // 👈 NUEVO CALLBACK
    modifier: Modifier = Modifier
) {
    // Estados
    val ubicacionDetailState by ubicacionViewModel.ubicacionDetailState.collectAsStateWithLifecycle()

    // Cargar detalle al iniciar
    LaunchedEffect(codigo) {
        ubicacionViewModel.getUbicacionDetail(codigo)
    }

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Ubicación $codigo",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = ubicacionDetailState) {
                is UiState.Loading -> {
                    LoadingState(message = "Cargando ubicación...")
                }

                is UiState.Success -> {
                    val ubicacion = state.data

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // ======= INFORMACIÓN DE LA UBICACIÓN =======
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = ubicacion.codigoUbicacion,
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Surface(
                                            shape = MaterialTheme.shapes.medium,
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = "Piso ${ubicacion.piso}",
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Surface(
                                            shape = MaterialTheme.shapes.medium,
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = "Posición ${ubicacion.numero}",
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            // ======= ESTADÍSTICAS =======
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    StatItem(
                                        icon = Icons.Default.Inventory,
                                        label = "Productos",
                                        value = ubicacion.productos?.size?.toString() ?: "0"
                                    )

                                    VerticalDivider(modifier = Modifier.height(60.dp))

                                    StatItem(
                                        icon = Icons.Default.Numbers,
                                        label = "Unidades",
                                        value = ubicacion.productos?.sumOf { it.cantidad }?.toString() ?: "0"
                                    )
                                }
                            }
                        }

                        item {
                            // ======= BOTÓN: CONTEO FÍSICO =======
                            Button(
                                onClick = {
                                    ubicacion.idUbicacion?.let {
                                        onNavigateToConteo(it)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Realizar conteo físico")
                            }
                        }

                        item {
                            Text(
                                text = "Productos Almacenados",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (ubicacion.productos.isNullOrEmpty()) {
                            item {
                                EmptyState(
                                    icon = Icons.Default.Inbox,
                                    title = "Ubicación Vacía",
                                    message = "No hay productos en esta ubicación"
                                )
                            }
                        } else {
                            items(ubicacion.productos) { productoEnUbicacion ->
                                Card(
                                    onClick = {
                                        onNavigateToProducto(productoEnUbicacion.sku)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = MaterialTheme.shapes.medium,
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.size(56.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Inventory,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = productoEnUbicacion.sku,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = productoEnUbicacion.descripcion,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "Cantidad: ${productoEnUbicacion.cantidad}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = "Ver producto"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = {
                            ubicacionViewModel.getUbicacionDetail(codigo)
                        }
                    )
                }

                is UiState.Idle -> {}
            }
        }
    }
}

/**
 * Item de estadística
 */
@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
