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
import com.pneuma.fotomarwms_grupo5.viewmodels.AuthViewModel
import com.pneuma.fotomarwms_grupo5.viewmodels.ProductoViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla de Búsqueda de Productos
 *
 * Funcionalidades:
 * - Búsqueda por cámara (escanear código de barras o LPN)
 * - Búsqueda manual por SKU, descripción o ubicación
 * - Resultados en cards separadas con botón "Ver Detalles"
 * - Filtros y ordenamiento
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaScreen(
    authViewModel: AuthViewModel,
    productoViewModel: ProductoViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val searchState by productoViewModel.searchState.collectAsStateWithLifecycle()
    val searchQuery by productoViewModel.searchQuery.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado local
    var localSearchQuery by remember { mutableStateOf("") }
    var showScanDialog by remember { mutableStateOf(false) }

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentUser = currentUser,
                currentRoute = "busqueda",
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
                    title = "Buscar Producto",
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
                // ========== SECCIÓN DE BÚSQUEDA ==========
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Botón grande de cámara
                    Card(
                        onClick = {
                            showScanDialog = true
                            // TODO: Abrir cámara para escanear
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Escanear",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Escanear Código",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = "Toca para usar la cámara",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider con texto
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "o realizar búsqueda manual",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de búsqueda manual
                    SearchTextField(
                        value = localSearchQuery,
                        onValueChange = { localSearchQuery = it },
                        placeholder = "SKU, descripción, código de barras...",
                        onSearch = {
                            productoViewModel.searchProductos(localSearchQuery)
                        },
                        onClear = {
                            localSearchQuery = ""
                            productoViewModel.clearSearch()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón de búsqueda
                    PrimaryButton(
                        text = "Buscar",
                        onClick = {
                            productoViewModel.searchProductos(localSearchQuery)
                        },
                        icon = Icons.Default.Search,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider()

                // ========== RESULTADOS DE BÚSQUEDA ==========
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (val state = searchState) {
                        is UiState.Idle -> {
                            EmptyState(
                                icon = Icons.Default.Search,
                                title = "Busca un producto",
                                message = "Usa la cámara para escanear un código o realiza una búsqueda manual"
                            )
                        }

                        is UiState.Loading -> {
                            LoadingState(message = "Buscando productos...")
                        }

                        is UiState.Success -> {
                            if (state.data.isEmpty()) {
                                EmptyState(
                                    icon = Icons.Default.SearchOff,
                                    title = "Sin resultados",
                                    message = "No se encontraron productos con \"$searchQuery\"",
                                    actionButton = {
                                        SecondaryButton(
                                            text = "Nueva búsqueda",
                                            onClick = {
                                                localSearchQuery = ""
                                                productoViewModel.clearSearch()
                                            }
                                        )
                                    }
                                )
                            } else {
                                Column {
                                    // Header de resultados
                                    Text(
                                        text = "${state.data.size} resultado(s) encontrado(s)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    // Lista de productos
                                    LazyColumn(
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(state.data) { producto ->
                                            ProductoCard(
                                                producto = producto,
                                                onClick = {
                                                    onNavigateToDetail(producto.sku)
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
                                    productoViewModel.searchProductos(localSearchQuery)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de información sobre escaneo
    InfoDialog(
        title = "Escanear Código",
        message = "La funcionalidad de escaneo con cámara estará disponible próximamente. Por ahora, utiliza la búsqueda manual.",
        onDismiss = { showScanDialog = false },
        showDialog = showScanDialog
    )
}