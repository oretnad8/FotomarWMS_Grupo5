package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.models.Mensaje
import com.pneuma.fotomarwms_grupo5.models.UiState
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.AuthViewModel
import com.pneuma.fotomarwms_grupo5.viewmodels.MensajeViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla de Mensajes
 *
 * Funcionalidades:
 * - Bandeja de entrada de mensajes
 * - Filtros: Todos, No leídos, Importantes
 * - Detalle de mensaje en diálogo
 * - Marcar como leído automáticamente
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajesScreen(
    authViewModel: AuthViewModel,
    mensajeViewModel: MensajeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val mensajesState by mensajeViewModel.mensajesState.collectAsStateWithLifecycle()
    val selectedMensaje by mensajeViewModel.selectedMensaje.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estados de filtro
    var filtroActivo by remember { mutableStateOf("todos") }

    // Estados de UI
    var showMensajeDialog by remember { mutableStateOf(false) }

    // Cargar mensajes al iniciar
    LaunchedEffect(Unit) {
        mensajeViewModel.getMensajes()
    }

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentUser = currentUser,
                currentRoute = "mensajes",
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
                    title = "Mensajes",
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
                // ========== FILTROS ==========
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Filtros",
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
                                selected = filtroActivo == "todos",
                                onClick = {
                                    filtroActivo = "todos"
                                    mensajeViewModel.getMensajes()
                                },
                                modifier = Modifier.weight(1f)
                            )

                            FilterChip(
                                text = "No leídos",
                                selected = filtroActivo == "no_leidos",
                                onClick = {
                                    filtroActivo = "no_leidos"
                                    mensajeViewModel.getMensajesNoLeidos()
                                },
                                modifier = Modifier.weight(1f)
                            )

                            FilterChip(
                                text = "Importantes",
                                selected = filtroActivo == "importantes",
                                onClick = {
                                    filtroActivo = "importantes"
                                    mensajeViewModel.getMensajesImportantes()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // ========== LISTA DE MENSAJES ==========
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (val state = mensajesState) {
                        is UiState.Loading -> {
                            LoadingState(message = "Cargando mensajes...")
                        }

                        is UiState.Success -> {
                            if (state.data.isEmpty()) {
                                EmptyState(
                                    icon = Icons.Default.MailOutline,
                                    title = "Sin mensajes",
                                    message = when (filtroActivo) {
                                        "no_leidos" -> "No tienes mensajes sin leer"
                                        "importantes" -> "No tienes mensajes importantes"
                                        else -> "No tienes mensajes"
                                    }
                                )
                            } else {
                                Column {
                                    // Header con contador
                                    Text(
                                        text = "${state.data.size} mensaje(s)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    // Lista de mensajes
                                    LazyColumn(
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(state.data) { mensaje ->
                                            MensajeCard(
                                                mensaje = mensaje,
                                                onClick = {
                                                    mensajeViewModel.selectMensaje(mensaje)
                                                    showMensajeDialog = true
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
                                    when (filtroActivo) {
                                        "no_leidos" -> mensajeViewModel.getMensajesNoLeidos()
                                        "importantes" -> mensajeViewModel.getMensajesImportantes()
                                        else -> mensajeViewModel.getMensajes()
                                    }
                                }
                            )
                        }

                        is UiState.Idle -> {
                            EmptyState(
                                icon = Icons.Default.Email,
                                title = "Mensajes",
                                message = "Cargando bandeja de entrada..."
                            )
                        }
                    }
                }
            }
        }
    }

    // ========== DIÁLOGO DE DETALLE DE MENSAJE ==========
    if (showMensajeDialog && selectedMensaje != null) {
        MensajeDetailDialog(
            mensaje = selectedMensaje!!,
            onDismiss = {
                showMensajeDialog = false
                mensajeViewModel.clearSelectedMensaje()
            }
        )
    }
}

/**
 * Diálogo para mostrar el detalle completo del mensaje
 */
@Composable
private fun MensajeDetailDialog(
    mensaje: Mensaje,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = when {
                    mensaje.importante -> Icons.Default.PriorityHigh
                    else -> Icons.Default.Email
                },
                contentDescription = null,
                tint = if (mensaje.importante)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Column {
                if (mensaje.importante) {
                    AlertBadge(
                        text = "IMPORTANTE",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Text(text = mensaje.titulo)
            }
        },
        text = {
            Column {
                // Remitente y fecha
                Text(
                    text = "De: ${mensaje.remitente}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Fecha: ${mensaje.fecha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Contenido del mensaje
                Text(
                    text = mensaje.contenido,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}