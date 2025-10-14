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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.AuthViewModel
import com.pneuma.fotomarwms_grupo5.viewmodels.UsuarioViewModel
import kotlinx.coroutines.launch

import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.contarUsuariosActivos

import com.pneuma.fotomarwms_grupo5.models.UiState

import com.pneuma.fotomarwms_grupo5.navigation.Screen



/**
 * Dashboard para rol ADMINISTRADOR
 *
 * Funcionalidades principales:
 * - Visualización de estadísticas del sistema (usuarios activos, productos totales)
 * - Acceso rápido a gestión de usuarios
 * - Descarga de reportes del sistema
 * - Configuración general
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    authViewModel: AuthViewModel,
    usuarioViewModel: UsuarioViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //Para ver el estado de los usuarios
    val usuariosState by usuarioViewModel.usuariosState.collectAsStateWithLifecycle()

    val activeUsersText = when (val state = usuariosState) {
        is UiState.Success -> contarUsuariosActivos(state.data).toString()   // 👈 Usa la función acá
        is UiState.Loading -> "…"   // Mientras carga
        is UiState.Error -> "--"    // Si hay error
        else -> "--"
    }


    //Para saber cuantos usuarios estan activos

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentUser = currentUser,
                currentRoute = "dashboard_admin",
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
                    title = "Dashboard Administrador",
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // ========== SALUDO ==========
                Text(
                    text = "Bienvenido, ${currentUser?.nombre ?: "Administrador"}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Panel de control administrativo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // ========== TARJETAS DE ESTADÍSTICAS ==========
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tarjeta Usuarios Activos
                    StatCard(
                        icon = Icons.Default.Person,
                        title = "Usuarios Activos",
                        value = activeUsersText,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Tarjeta Productos Totales
                    StatCard(
                        icon = Icons.Default.Inventory,
                        title = "Productos",
                        value = "1,247",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                // ========== ACCIONES RÁPIDAS ==========
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Gestionar Usuarios
                ActionCard(
                    icon = Icons.Default.ManageAccounts,
                    title = "Gestionar Usuarios",
                    description = "Crear, editar o eliminar usuarios del sistema",
                    onClick = { onNavigate("gestion_usuarios") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Descargar Reportes
                ActionCard(
                    icon = Icons.Default.Assessment,
                    title = "Descargar Reportes",
                    description = "Generar reportes de inventario, movimientos y usuarios",
                    onClick = {
                        // TODO: Implementar descarga de reportes
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Configuración del Sistema
                ActionCard(
                    icon = Icons.Default.Settings,
                    title = "Configuración",
                    description = "Configurar parámetros generales del sistema",
                    onClick = { onNavigate(Screen.Configuracion.route) }

                )

                Spacer(modifier = Modifier.height(24.dp))

                // ========== INFORMACIÓN DEL SISTEMA ==========
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "ℹ️ Información del Sistema",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        InfoRow("Versión", "1.0.0")
                        InfoRow("Última actualización", "09/10/2025")
                        InfoRow("Base de datos", "MySQL 8.0")
                        InfoRow("Estado del servidor", "✅ Operativo")
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de estadística compacta
 */
@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
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
                modifier = Modifier.size(40.dp),
                tint = color
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tarjeta de acción clickeable
 */
@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Fila de información clave-valor
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}