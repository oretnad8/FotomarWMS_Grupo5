package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.network.Pedido
import com.pneuma.fotomarwms_grupo5.network.EstadoPedido
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.BackTopBar
import com.pneuma.fotomarwms_grupo5.viewmodels.PedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosListScreen(
    viewModel: PedidosViewModel,
    onNavigateToPicking: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val titles = listOf("Pendientes", "En Curso", "Finalizadas")

    val pedidosPendientes by viewModel.pedidosPendientes.collectAsStateWithLifecycle()
    val misPedidos by viewModel.misPedidos.collectAsStateWithLifecycle()
    val pedidosFinalizados by viewModel.pedidosFinalizados.collectAsStateWithLifecycle()
    
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar datos según el tab seleccionado
    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            0 -> viewModel.loadPedidosPendientes()
            1 -> viewModel.loadPedidosEnPicking()
            2 -> viewModel.loadPedidosFinalizados()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            Column {
                BackTopBar(
                    title = "Notas de Venta",
                    onBackClick = onNavigateBack
                )
                TabRow(selectedTabIndex = selectedTab) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(text = title) }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            val currentList = when (selectedTab) {
                0 -> pedidosPendientes
                1 -> misPedidos
                2 -> pedidosFinalizados
                else -> emptyList()
            }
            
            val emptyMessage = when (selectedTab) {
                0 -> "No hay notas de venta pendientes"
                1 -> "No tienes pedidos en curso"
                2 -> "No hay pedidos finalizados recientes"
                else -> ""
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (currentList.isEmpty()) {
                Text(
                    text = emptyMessage,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentList) { pedido ->
                        PedidoCard(
                            pedido = pedido,
                            onClick = { onNavigateToPicking(pedido.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCard(
    pedido: Pedido,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Nota de Venta #${pedido.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    pedido.estado?.let { 
                        StatusPedidoBadge(estado = it)
                    }
                }
                Text(
                    text = pedido.displayCliente,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = pedido.fecha ?: "--/--/----",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
    
            }
            
            IconButton(onClick = onClick) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Ver Detalle")
            }
        }
    }
}

@Composable
fun StatusPedidoBadge(estado: EstadoPedido) {
    val (backgroundColor, textColor) = when (estado) {
        EstadoPedido.PENDIENTE -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
        EstadoPedido.ACEPTADO -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        EstadoPedido.EN_PICKING -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
        EstadoPedido.PICKING_COMPLETADO -> Color(0xFFE1BEE7) to Color(0xFF6A1B9A)
        EstadoPedido.VALIDADO -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
        EstadoPedido.EN_TRANSPORTE -> Color(0xFFE0F7FA) to Color(0xFF0097A7)
        EstadoPedido.ENTREGADO -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        EstadoPedido.CANCELADO -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
    }


    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = estado.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
