package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.models.Piso
import com.pneuma.fotomarwms_grupo5.models.UiState
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.AuthViewModel
import com.pneuma.fotomarwms_grupo5.viewmodels.UbicacionViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla de Gestión de Ubicaciones
 *
 * Funcionalidades:
 * - Vista de todas las ubicaciones por piso (A, B, C)
 * - Filtro por piso
 * - Vista en cuadrícula de ubicaciones
 * - Jefe: Asignación directa de productos
 * - Operador: Solicitar cambio de ubicación (requiere aprobación)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUbicacionesScreen(
    authViewModel: AuthViewModel,
    ubicacionViewModel: UbicacionViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val ubicacionesState by ubicacionViewModel.ubicacionesState.collectAsStateWithLifecycle()
    val pisoSeleccionado by ubicacionViewModel.pisoSeleccionado.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cargar ubicaciones al iniciar
    LaunchedEffect(Unit) {
        ubicacionViewModel.getAllUbicaciones()
    }

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentUser = currentUser,
                currentRoute = "gestion_ubicaciones",
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
                    title = "Ubicaciones",
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // ========== FILTROS POR PISO ==========
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Filtrar por piso",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Botón Todos
                            FilterChip(
                                text = "Todos",
                                selected = pisoSeleccionado == null,
                                onClick = {
                                    ubicacionViewModel.clearPisoFilter()
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Botón Piso A
                            FilterChip(
                                text = "Piso A",
                                selected = pisoSeleccionado == Piso.A,
                                onClick = {
                                    ubicacionViewModel.getUbicacionesByPiso(Piso.A)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Botón Piso B
                            FilterChip(
                                text = "Piso B",
                                selected = pisoSeleccionado == Piso.B,
                                onClick = {
                                    ubicacionViewModel.getUbicacionesByPiso(Piso.B)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Botón Piso C
                            FilterChip(
                                text = "Piso C",
                                selected = pisoSeleccionado == Piso.C,
                                onClick = {
                                    ubicacionViewModel.getUbicacionesByPiso(Piso.C)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // ========== LISTA DE UBICACIONES ==========
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (val state = ubicacionesState) {
                        is UiState.Loading -> {
                            LoadingState(message = "Cargando ubicaciones...")
                        }

                        is UiState.Success -> {
                            if (state.data.isEmpty()) {
                                EmptyState(
                                    icon = Icons.Default.LocationOff,
                                    title = "Sin ubicaciones",
                                    message = "No se encontraron ubicaciones para este piso"
                                )
                            } else {
                                Column {
                                    // Header con contador
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${state.data.size} ubicación(es)",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = if (pisoSeleccionado != null)
                                                "Piso ${pisoSeleccionado!!.codigo}"
                                            else
                                                "Todos los pisos",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Grid de ubicaciones
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        contentPadding = PaddingValues(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(state.data) { ubicacion ->
                                            UbicacionCard(
                                                ubicacion = ubicacion,
                                                onClick = {
                                                    onNavigateToDetail(ubicacion.codigoUbicacion)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is UiState.Error -> {
                            ErrorState(
                                message = state.message,
                                onRetry = {
                                    if (pisoSeleccionado != null) {
                                        ubicacionViewModel.getUbicacionesByPiso(pisoSeleccionado!!)
                                    } else {
                                        ubicacionViewModel.getAllUbicaciones()
                                    }
                                }
                            )
                        }

                        is UiState.Idle -> {
                            EmptyState(
                                icon = Icons.Default.LocationOn,
                                title = "Ubicaciones",
                                message = "Selecciona un piso para ver las ubicaciones"
                            )
                        }
                    }
                }
            }
        }
    }
}