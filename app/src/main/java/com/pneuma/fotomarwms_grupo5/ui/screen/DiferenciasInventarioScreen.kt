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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.models.TipoDiferencia
import com.pneuma.fotomarwms_grupo5.models.UiState
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiferenciasInventarioScreen(
    inventarioViewModel: InventarioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diferenciasState by inventarioViewModel.diferenciasState.collectAsStateWithLifecycle()
    val filtroTipo by inventarioViewModel.filtroTipoDiferencia.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        inventarioViewModel.getDiferenciasConDiscrepancia()
    }

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Diferencias de Inventario",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Filtrar por tipo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            text = "Todos",
                            selected = filtroTipo == null,
                            onClick = {
                                inventarioViewModel.clearFiltroTipo()
                                inventarioViewModel.getDiferenciasConDiscrepancia()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            text = "Faltantes",
                            selected = filtroTipo == TipoDiferencia.FALTANTE,
                            onClick = {
                                inventarioViewModel.getDiferenciasByTipo(TipoDiferencia.FALTANTE)
                            },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            text = "Sobrantes",
                            selected = filtroTipo == TipoDiferencia.SOBRANTE,
                            onClick = {
                                inventarioViewModel.getDiferenciasByTipo(TipoDiferencia.SOBRANTE)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (val state = diferenciasState) {
                    is UiState.Loading -> LoadingState(message = "Cargando diferencias...")

                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            EmptyState(
                                icon = Icons.Filled.CheckCircle,
                                title = "Sin diferencias",
                                message = if (filtroTipo != null) {
                                    "No hay diferencias de tipo ${filtroTipo!!.name}"
                                } else {
                                    "No se encontraron diferencias en el inventario"
                                }
                            )
                        } else {
                            Column {
                                Text(
                                    text = "${state.data.size} diferencia(s) encontrada(s)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )

                                LazyColumn(
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(state.data) { diferencia ->
                                        DiferenciaCard(diferencia = diferencia)
                                    }
                                }
                            }
                        }
                    }

                    is UiState.Error -> ErrorState(
                        message = state.message,
                        onRetry = {
                            if (filtroTipo != null) {
                                inventarioViewModel.getDiferenciasByTipo(filtroTipo!!)
                            } else {
                                inventarioViewModel.getDiferenciasConDiscrepancia()
                            }
                        }
                    )

                    is UiState.Idle -> {}
                }
            }
        }
    }
}

private data class DiffStyle(
    val bg: Color,
    val iconColor: Color,
    val icon: ImageVector,
    val label: String
)

@Composable
private fun DiferenciaCard(
    diferencia: com.pneuma.fotomarwms_grupo5.models.DiferenciaInventario,
    modifier: Modifier = Modifier
) {
    val style = when (diferencia.tipoDiferencia) {
        TipoDiferencia.FALTANTE -> DiffStyle(
            bg = Color(0xFFFFEBEE),
            iconColor = Color(0xFFD32F2F),
            icon = Icons.Filled.Remove,
            label = "FALTANTE"
        )
        TipoDiferencia.SOBRANTE -> DiffStyle(
            bg = Color(0xFFE8F5E9),
            iconColor = Color(0xFF388E3C),
            icon = Icons.Filled.Add,
            label = "SOBRANTE"
        )
        TipoDiferencia.CORRECTO -> DiffStyle(
            bg = Color(0xFFE3F2FD),
            iconColor = Color(0xFF1976D2),
            icon = Icons.Filled.Check,
            label = "CORRECTO"
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = style.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = style.icon,
                        contentDescription = null,
                        tint = style.iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = diferencia.sku,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = style.iconColor
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = style.iconColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = style.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = style.iconColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = diferencia.descripcionProducto,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = style.iconColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Ubicación: ${diferencia.codigoUbicacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = style.iconColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuantityColumn(
                    label = "Sistema",
                    value = diferencia.cantidadSistema.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = style.iconColor
                )

                QuantityColumn(
                    label = "Físico",
                    value = diferencia.cantidadFisica.toString(),
                    color = style.iconColor
                )

                Icon(
                    imageVector = Icons.Filled.DragHandle,
                    contentDescription = null,
                    tint = style.iconColor
                )

                QuantityColumn(
                    label = "Diferencia",
                    value = if (diferencia.diferencia > 0) "+${diferencia.diferencia}" else diferencia.diferencia.toString(),
                    color = style.iconColor
                )
            }
        }
    }
}

@Composable
private fun QuantityColumn(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
