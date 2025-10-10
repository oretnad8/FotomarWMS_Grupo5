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
import com.pneuma.fotomarwms_grupo5.viewmodels.MensajeViewModel
import kotlinx.coroutines.launch

/**
 * Dashboard para rol OPERADOR
 *
 * Funcionalidades principales:
 * - Mensajes del jefe de bodega
 * - Tareas pendientes asignadas
 * - Acciones rápidas (buscar productos, solicitar movimientos)
 * - Estado de mis solicitudes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardOperadorScreen(
    authViewModel: AuthViewModel,
    mensajeViewModel: MensajeViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val resumenMensajes by mensajeViewModel.resumenState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cargar resumen al iniciar
    LaunchedEffect(Unit) {
        mensajeViewModel.getResumenMensajes()
    }

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentUser = currentUser,
                currentRoute = "dashboard_operador",
                onNavigate = { route ->
                    scope.launch {
                        drawerState.close()
                        onNavigate(route)
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    onNavigate("login")
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Dashboard Operador",
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    actions = {
                        // Badge de mensajes no leídos
                        when (val state = resumenMensajes) {
                            is UiState.Success -> {
                                if (state.data.totalNoLeidos > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge {
                                                Text(state.data.totalNoLeidos.toString())
                                            }
                                        }
                                    ) {
                                        IconButton(onClick = { onNavigate("mensajes") }) {
                                            Icon(
                                                Icons.Default.Email,
                                                contentDescription = "Mensajes"
                                            )
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // ========== SALUDO ==========
                Text(
                    text = "Bienvenido, Operador",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = currentUser?.nombre ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // ========== MENSAJE DEL JEFE ==========
                when (val state = resumenMensajes) {
                    is UiState.Success -> {
                        if (state.data.ultimoMensaje != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                onClick = { onNavigate("mensajes") }
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "📬 Mensaje del Jefe",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = state.data.ultimoMensaje.titulo,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = state.data.ultimoMensaje.contenido.take(100) + "...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Hace 2 horas", // TODO: Calcular tiempo real
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }

                // ========== TAREAS PENDIENTES ==========
                Text(
                    text = "Tareas Pendientes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Tarea 1
                TaskCard(
                    icon = Icons.Default.Checklist,
                    title = "Conteo físico - Sección A",
                    description = "Realizar conteo completo de productos en estantes A-1 a A-5",
                    priority = "ALTA",
                    onClick = { onNavigate("inventario") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tarea 2
                TaskCard(
                    icon = Icons.Default.LocationOn,
                    title = "Ubicar productos recibidos",
                    description = "Asignar ubicación a 15 productos nuevos en bodega",
                    priority = "MEDIA",
                    onClick = { onNavigate("gestion_ubicaciones") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ========== ACCIONES RÁPIDAS ==========
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Buscar Producto
                    QuickActionCard(
                        icon = Icons.Default.Search,
                        text = "Buscar Producto",
                        onClick = { onNavigate("busqueda") },
                        modifier = Modifier.weight(1f)
                    )

                    // Solicitar Movimiento
                    QuickActionCard(
                        icon = Icons.Default.AddCircle,
                        text = "Solicitar Movimiento",
                        onClick = { onNavigate("solicitud_movimiento") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Ver Mis Solicitudes
                    QuickActionCard(
                        icon = Icons.Default.List,
                        text = "Mis Solicitudes",
                        onClick = { onNavigate("mis_solicitudes") },
                        modifier = Modifier.weight(1f)
                    )

                    // Inventario
                    QuickActionCard(
                        icon = Icons.Default.Inventory,
                        text = "Inventario",
                        onClick = { onNavigate("inventario") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de tarea pendiente
 */
@Composable
private fun TaskCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    priority: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (priority) {
        "ALTA" -> Color(0xFFD32F2F)
        "MEDIA" -> Color(0xFFF57C00)
        else -> Color(0xFF388E3C)
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = priorityColor
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = priorityColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = priority,
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Tarjeta de acción rápida compacta
 */
@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}