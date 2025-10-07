package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pneuma.fotomarwms_grupo5.model.*
import com.pneuma.fotomarwms_grupo5.viewmodels.AprobacionesViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla de Aprobaciones para Jefe de Bodega
 *
 * Permite:
 * - Ver solicitudes pendientes de ingreso/egreso
 * - Aprobar o rechazar con observaciones
 * - Filtrar por tipo (Todas, Pendientes, Aprobadas, Rechazadas)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AprobacionesScreen(
    navController: NavController,
    viewModel: AprobacionesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estados para filtros
    var filtroSeleccionado by remember { mutableStateOf(EstadoAprobacion.PENDIENTE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aprobaciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Pestañas de filtro
            TabRow(selectedTabIndex = filtroSeleccionado.ordinal) {
                Tab(
                    selected = filtroSeleccionado == EstadoAprobacion.PENDIENTE,
                    onClick = { filtroSeleccionado = EstadoAprobacion.PENDIENTE },
                    text = { Text("Pendientes") }
                )
                Tab(
                    selected = filtroSeleccionado == EstadoAprobacion.APROBADO,
                    onClick = { filtroSeleccionado = EstadoAprobacion.APROBADO },
                    text = { Text("Aprobado") }
                )
                Tab(
                    selected = filtroSeleccionado == EstadoAprobacion.RECHAZADO,
                    onClick = { filtroSeleccionado = EstadoAprobacion.RECHAZADO },
                    text = { Text("Rechazado") }
                )
            }

            // Lista de aprobaciones filtradas
            val aprobacionesFiltradas = uiState.aprobaciones.filter {
                it.estado == filtroSeleccionado
            }

            if (aprobacionesFiltradas.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Sin solicitudes",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay solicitudes ${filtroSeleccionado.name.lowercase()}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(aprobacionesFiltradas) { aprobacion ->
                        AprobacionCard(
                            aprobacion = aprobacion,
                            onAprobar = { viewModel.aprobarSolicitud(aprobacion.id) },
                            onRechazar = { viewModel.rechazarSolicitud(aprobacion.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card que muestra una solicitud de aprobación
 */
@Composable
fun AprobacionCard(
    aprobacion: Aprobacion,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con tipo de movimiento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (aprobacion.tipoMovimiento) {
                            TipoMovimiento.INGRESO -> Icons.Default.ArrowUpward
                            TipoMovimiento.EGRESO -> Icons.Default.ArrowDownward
                            TipoMovimiento.REUBICACION -> Icons.Default.SwapHoriz
                        },
                        contentDescription = aprobacion.tipoMovimiento.name,
                        tint = when (aprobacion.tipoMovimiento) {
                            TipoMovimiento.INGRESO -> Color(0xFF66BB6A)
                            TipoMovimiento.EGRESO -> Color(0xFFEF5350)
                            TipoMovimiento.REUBICACION -> Color(0xFF42A5F5)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = aprobacion.tipoMovimiento.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Badge de estado
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (aprobacion.estado) {
                        EstadoAprobacion.PENDIENTE -> Color(0xFFFFA726)
                        EstadoAprobacion.APROBADO -> Color(0xFF66BB6A)
                        EstadoAprobacion.RECHAZADO -> Color(0xFFEF5350)
                    }
                ) {
                    Text(
                        text = aprobacion.estado.name,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Información del producto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Producto: ${aprobacion.producto.sku}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = aprobacion.producto.descripcion,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Cantidad: ${aprobacion.cantidad}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Motivo
            Text(
                text = "Motivo: ${aprobacion.motivo}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Solicitante y fecha
            Text(
                text = "Solicitado por: ${aprobacion.solicitante.nombre}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Fecha: ${dateFormat.format(aprobacion.fechaSolicitud)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Botones de acción (solo para pendientes)
            if (aprobacion.estado == EstadoAprobacion.PENDIENTE) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRechazar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF5350)
                        )
                    ) {
                        Text("Rechazar")
                    }
                    Button(
                        onClick = onAprobar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF66BB6A)
                        )
                    ) {
                        Text("Aprobar")
                    }
                }
            }
        }
    }
}