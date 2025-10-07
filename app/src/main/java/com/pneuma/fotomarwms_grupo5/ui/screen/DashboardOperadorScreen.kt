package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pneuma.fotomarwms_grupo5.model.Mensaje
import com.pneuma.fotomarwms_grupo5.model.Tarea
import com.pneuma.fotomarwms_grupo5.model.PrioridadTarea
import com.pneuma.fotomarwms_grupo5.navigation.Screen
import com.pneuma.fotomarwms_grupo5.viewmodels.DashboardOperadorViewModel
import kotlinx.coroutines.launch

/**
 * Dashboard principal para usuarios con rol OPERADOR
 *
 * Muestra:
 * - Mensajes del jefe de bodega
 * - Tareas pendientes asignadas
 * - Acciones rápidas (Buscar producto, Solicitar movimiento)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardOperadorScreen(
    navController: NavController,
    viewModel: DashboardOperadorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Drawer lateral con menú contextual
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerOperador(
                onNavigate = { screen ->
                    scope.launch {
                        drawerState.close()
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("FotomarWMS") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Saludo personalizado
                item {
                    Text(
                        text = "Bienvenido, ${uiState.nombreUsuario}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Mensaje del Jefe (si existe)
                item {
                    if (uiState.mensajeDelJefe != null) {
                        MensajeDelJefeCard(mensaje = uiState.mensajeDelJefe!!)
                    }
                }

                // Tareas Pendientes
                item {
                    Text(
                        text = "Tareas Pendientes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (uiState.tareasPendientes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "No hay tareas pendientes",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(uiState.tareasPendientes) { tarea ->
                        TareaCard(tarea = tarea)
                    }
                }

                // Acciones Rápidas
                item {
                    Text(
                        text = "Acciones Rápidas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AccionRapidaCard(
                            titulo = "Buscar Producto",
                            icono = Icons.Default.Search,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Busqueda.route) }
                        )

                        AccionRapidaCard(
                            titulo = "Solicitar Movimiento",
                            icono = Icons.Default.MoveToInbox,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.SolicitudMovimiento.route) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card que muestra un mensaje importante del jefe de bodega
 */
@Composable
fun MensajeDelJefeCard(mensaje: Mensaje) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD) // Azul claro
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = "Mensaje",
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Mensaje del Jefe",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mensaje.contenido,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hace ${mensaje.fecha} horas",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Card que representa una tarea asignada
 */
@Composable
fun TareaCard(tarea: Tarea) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (tarea.prioridad) {
                PrioridadTarea.ALTA -> Color(0xFFFFEBEE) // Rojo claro
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de prioridad
            Icon(
                imageVector = when (tarea.prioridad) {
                    PrioridadTarea.ALTA -> Icons.Default.PriorityHigh
                    else -> Icons.Default.Circle
                },
                contentDescription = "Prioridad",
                tint = when (tarea.prioridad) {
                    PrioridadTarea.ALTA -> Color.Red
                    else -> Color(0xFFFFA726)
                },
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.descripcion,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                if (tarea.ubicacion != null) {
                    Text(
                        text = "Ubicación: ${tarea.ubicacion}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Card para acciones rápidas del dashboard
 */
@Composable
fun AccionRapidaCard(
    titulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = titulo,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Drawer lateral con opciones de navegación para operador
 */
@Composable
fun DrawerOperador(onNavigate: (Screen) -> Unit) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Menú Operador",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DrawerItem(
                text = "Buscar Producto",
                icon = Icons.Default.Search,
                onClick = { onNavigate(Screen.Busqueda) }
            )

            DrawerItem(
                text = "Solicitar Movimiento",
                icon = Icons.Default.MoveToInbox,
                onClick = { onNavigate(Screen.SolicitudMovimiento) }
            )

            DrawerItem(
                text = "Mensajes",
                icon = Icons.Default.Message,
                onClick = { onNavigate(Screen.Mensajes) }
            )

            DrawerItem(
                text = "Inventario",
                icon = Icons.Default.Inventory,
                onClick = { onNavigate(Screen.Inventario) }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            DrawerItem(
                text = "Mi Perfil",
                icon = Icons.Default.Person,
                onClick = { onNavigate(Screen.Perfil) }
            )
        }
    }
}

@Composable
fun DrawerItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(text) },
        icon = { Icon(icon, contentDescription = text) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}