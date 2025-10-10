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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.models.UiState
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.AuthViewModel
import com.pneuma.fotomarwms_grupo5.viewmodels.InventarioViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla de Inventario
 *
 * Funcionalidades:
 * - Ver progreso del inventario actual
 * - Registrar conteo físico de productos
 * - Ver diferencias entre sistema y físico
 * - Finalizar inventario (solo Jefe/Supervisor)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    authViewModel: AuthViewModel,
    inventarioViewModel: InventarioViewModel,
    onNavigateToDiferencias: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val progresoState by inventarioViewModel.progresoState.collectAsStateWithLifecycle()
    val conteoState by inventarioViewModel.conteoState.collectAsStateWithLifecycle()
    val finalizarState by inventarioViewModel.finalizarState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estados de formulario de conteo
    var showConteoDialog by remember { mutableStateOf(false) }
    var sku by remember { mutableStateOf("") }
    var idUbicacion by remember { mutableStateOf("") }
    var cantidadFisica by remember { mutableStateOf("") }

    // Estados de UI
    var showFinalizarDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Cargar progreso al iniciar
    LaunchedEffect(Unit) {
        inventarioViewModel.getProgreso()
    }

    // Manejar éxito de conteo
    LaunchedEffect(conteoState) {
        when (val state = conteoState) {
            is UiState.Success -> {
                showSuccessDialog = true
                sku = ""
                idUbicacion = ""
                cantidadFisica = ""
                showConteoDialog = false
                inventarioViewModel.clearConteoState()
            }
            else -> {}
        }
    }

    // Manejar éxito de finalización
    LaunchedEffect(finalizarState) {
        when (val state = finalizarState) {
            is UiState.Success -> {
                showSuccessDialog = true
                inventarioViewModel.clearFinalizarState()
            }
            else -> {}
        }
    }

    // Diálogos
    SuccessDialog(
        message = "Operación completada exitosamente",
        onDismiss = {
            showSuccessDialog = false
            inventarioViewModel.getProgreso()
        },
        showDialog = showSuccessDialog
    )

    ConfirmDialog(
        title = "Finalizar Inventario",
        message = "¿Estás seguro de finalizar el inventario? Esta acción ajustará todos los stocks del sistema según los conteos físicos registrados.",
        onConfirm = {
            inventarioViewModel.finalizarInventario()
            showFinalizarDialog = false
        },
        onDismiss = { showFinalizarDialog = false },
        showDialog = showFinalizarDialog
    )

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentUser = currentUser,
                currentRoute = "inventario",
                onNavigate = { route ->
                    scope.launch {
                        drawerState.close()
                        onNavigateBack()
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    onNavigateBack()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Inventario",
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            floatingActionButton = {
                ActionFab(
                    onClick = { showConteoDialog = true },
                    icon = Icons.Default.Add,
                    contentDescription = "Registrar conteo"
                )
            }
        ) { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = progresoState) {
                    is UiState.Loading -> {
                        LoadingState(message = "Cargando progreso...")
                    }

                    is UiState.Success -> {
                        val progreso = state.data

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            // ========== PROGRESO GENERAL ==========
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Progreso del Inventario",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    CircularProgressIndicatorWithPercentage(
                                        percentage = progreso.porcentajeCompletado.toFloat(),
                                        size = 140
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        StatColumn(
                                            label = "Contadas",
                                            value = progreso.ubicacionesContadas.toString(),
                                            color = Color(0xFF4CAF50)
                                        )

                                        StatColumn(
                                            label = "Pendientes",
                                            value = progreso.ubicacionesPendientes.toString(),
                                            color = Color(0xFFF57C00)
                                        )

                                        StatColumn(
                                            label = "Total",
                                            value = progreso.totalUbicaciones.toString(),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            // ========== DIFERENCIAS ==========
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Diferencias Encontradas",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        TextButton(onClick = onNavigateToDiferencias) {
                                            Text("Ver todas")
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = null
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    DifferenceRow(
                                        label = "Ubicaciones con diferencias",
                                        value = progreso.ubicacionesConDiferencias.toString(),
                                        icon = Icons.Default.WarningAmber,
                                        color = Color(0xFFF57C00)
                                    )

                                    DifferenceRow(
                                        label = "Productos faltantes",
                                        value = progreso.totalFaltantes.toString(),
                                        icon = Icons.Default.Remove,
                                        color = Color(0xFFD32F2F)
                                    )

                                    DifferenceRow(
                                        label = "Productos sobrantes",
                                        value = progreso.totalSobrantes.toString(),
                                        icon = Icons.Default.Add,
                                        color = Color(0xFF388E3C)
                                    )
                                }
                            }

                            // ========== BOTÓN FINALIZAR ==========
                            if (authViewModel.isJefeOrAbove()) {
                                PrimaryButton(
                                    text = "Finalizar Inventario",
                                    onClick = { showFinalizarDialog = true },
                                    icon = Icons.Default.Check,
                                    enabled = progreso.porcentajeCompletado == 100.0 &&
                                            finalizarState !is UiState.Loading
                                )

                                if (progreso.porcentajeCompletado < 100.0) {
                                    Text(
                                        text = "Completa el 100% del inventario para poder finalizarlo",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    is UiState.Error -> {
                        ErrorState(
                            message = state.message,
                            onRetry = { inventarioViewModel.getProgreso() }
                        )
                    }

                    is UiState.Idle -> {}
                }
            }
        }
    }

    // ========== DIÁLOGO DE REGISTRO DE CONTEO ==========
    if (showConteoDialog) {
        ConteoDialog(
            sku = sku,
            onSkuChange = { sku = it },
            idUbicacion = idUbicacion,
            onIdUbicacionChange = { idUbicacion = it },
            cantidadFisica = cantidadFisica,
            onCantidadFisicaChange = { cantidadFisica = it },
            onConfirm = {
                if (sku.isNotBlank() && idUbicacion.isNotBlank() && cantidadFisica.isNotBlank()) {
                    inventarioViewModel.registrarConteo(
                        sku = sku,
                        idUbicacion = idUbicacion.toInt(),
                        cantidadFisica = cantidadFisica.toInt()
                    )
                }
            },
            onDismiss = {
                showConteoDialog = false
                sku = ""
                idUbicacion = ""
                cantidadFisica = ""
            },
            isLoading = conteoState is UiState.Loading
        )
    }
}

@Composable
private fun StatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
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

@Composable
private fun DifferenceRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ConteoDialog(
    sku: String,
    onSkuChange: (String) -> Unit,
    idUbicacion: String,
    onIdUbicacionChange: (String) -> Unit,
    cantidadFisica: String,
    onCantidadFisicaChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Inventory, contentDescription = null)
        },
        title = {
            Text("Registrar Conteo")
        },
        text = {
            Column {
                AppTextField(
                    value = sku,
                    onValueChange = onSkuChange,
                    label = "SKU",
                    placeholder = "AP30001"
                )
                Spacer(modifier = Modifier.height(8.dp))
                NumberTextField(
                    value = idUbicacion,
                    onValueChange = onIdUbicacionChange,
                    label = "ID Ubicación",
                    placeholder = "12"
                )
                Spacer(modifier = Modifier.height(8.dp))
                NumberTextField(
                    value = cantidadFisica,
                    onValueChange = onCantidadFisicaChange,
                    label = "Cantidad Física",
                    placeholder = "0"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading && sku.isNotBlank() &&
                        idUbicacion.isNotBlank() && cantidadFisica.isNotBlank()
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}