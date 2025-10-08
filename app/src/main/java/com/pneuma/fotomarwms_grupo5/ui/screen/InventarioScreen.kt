package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.background
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
import com.pneuma.fotomarwms_grupo5.model.DiferenciaInventario
import com.pneuma.fotomarwms_grupo5.viewmodels.InventarioViewModel

/**
 * Pantalla de inventario con cuadre de diferencias
 *
 * Permite:
 * - Ver progreso del inventario por piso
 * - Registrar conteo físico vs sistema
 * - Detectar y registrar diferencias
 * - Requiere aprobación para ajustes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    navController: NavController,
    viewModel: InventarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estado para el diálogo de conteo
    var mostrarDialogoConteo by remember { mutableStateOf(false) }
    var pisoSeleccionado by remember { mutableStateOf("A") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón para iniciar nuevo conteo con cámara
                    IconButton(onClick = { mostrarDialogoConteo = true }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Iniciar conteo")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título y descripción
            item {
                Text(
                    text = "Control de Inventario",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Realiza el conteo físico y registra las diferencias encontradas",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Progreso general
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Progreso Total",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${uiState.porcentajeCompletado}%",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            CircularProgressIndicator(
                                progress = uiState.porcentajeCompletado / 100f,
                                modifier = Modifier.size(64.dp),
                                strokeWidth = 6.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = uiState.porcentajeCompletado / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Progreso por piso
            item {
                Text(
                    text = "Progreso por Piso",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(uiState.progresosPiso) { progreso ->
                ProgresoPisoCard(
                    piso = progreso.piso,
                    porcentaje = progreso.porcentaje,
                    contados = progreso.contados,
                    total = progreso.total,
                    onClick = { pisoSeleccionado = progreso.piso; mostrarDialogoConteo = true }
                )
            }

            // Diferencias encontradas
            if (uiState.diferencias.isNotEmpty()) {
                item {
                    Text(
                        text = "Diferencias Encontradas (${uiState.diferencias.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(uiState.diferencias) { diferencia ->
                    DiferenciaCard(diferencia = diferencia)
                }
            }

            // Botón para finalizar inventario
            if (uiState.porcentajeCompletado == 100f) {
                item {
                    Button(
                        onClick = { viewModel.finalizarInventario() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finalizar Inventario")
                    }
                }
            }
        }
    }

    // Diálogo para iniciar conteo
    if (mostrarDialogoConteo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConteo = false },
            title = { Text("Iniciar Conteo - Piso $pisoSeleccionado") },
            text = { Text("¿Deseas escanear productos o ingresar manualmente?") },
            confirmButton = {
                TextButton(onClick = {
                    // TODO: Abrir escáner
                    mostrarDialogoConteo = false
                }) {
                    Text("Escanear")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // TODO: Abrir formulario manual
                    mostrarDialogoConteo = false
                }) {
                    Text("Manual")
                }
            }
        )
    }
}

/**
 * Card de progreso por piso
 */
@Composable
fun ProgresoPisoCard(
    piso: String,
    porcentaje: Float,
    contados: Int,
    total: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge del piso
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = when (piso) {
                            "A" -> Color(0xFF42A5F5)
                            "B" -> Color(0xFF66BB6A)
                            "C" -> Color(0xFFFFA726)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Piso $piso",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Piso $piso",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$contados/$total",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = porcentaje / 100f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${porcentaje.toInt()}% completado",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Card de diferencia encontrada
 */
@Composable
fun DiferenciaCard(diferencia: DiferenciaInventario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE) // Rojo claro
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Diferencia",
                        tint = Color(0xFFEF5350)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = diferencia.sku,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Badge(
                    containerColor = if (diferencia.diferencia > 0)
                        Color(0xFF66BB6A)
                    else
                        Color(0xFFEF5350)
                ) {
                    Text(
                        text = if (diferencia.diferencia > 0)
                            "+${diferencia.diferencia}"
                        else
                            diferencia.diferencia.toString(),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = diferencia.descripcion,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Sistema",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = diferencia.cantidadSistema.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "vs",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Físico",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = diferencia.cantidadFisico.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (diferencia.ubicacion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ubicación: ${diferencia.ubicacion}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}