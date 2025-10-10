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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.AuthViewModel
import com.pneuma.fotomarwms_grupo5.viewmodels.AprobacionViewModel
import com.pneuma.fotomarwms_grupo5.viewmodels.MensajeViewModel
import kotlinx.coroutines.launch

/**
 * Dashboard para rol JEFE DE BODEGA
 *
 * Funcionalidades principales:
 * - Alertas del sistema (stock bajo, solicitudes pendientes)
 * - Contadores de aprobaciones pendientes y aprobadas del día
 * - Acciones rápidas (ver aprobaciones, enviar mensajes)
 * - Alertas de productos con vencimiento cercano
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardJefeScreen(
    authViewModel: AuthViewModel,
    aprobacionViewModel: AprobacionViewModel,
    mensajeViewModel: MensajeViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentUser = currentUser,
                currentRoute = "dashboard_jefe",
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
                    title = "Dashboard Jefe",
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
                    text = "Bienvenido, Jefe de Bodega",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // ========== ALERTAS ==========
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF9C4) // Amarillo claro
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF57F17),
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚠️ Alertas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF57F17)
                            )
                            Text(
                                text = "3 productos con stock bajo",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "5 solicitudes pendientes",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // ========== CONTADORES ==========
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pendientes
                    CounterCard(
                        icon = Icons.Default.PendingActions,
                        title = "Pendientes",
                        count = 5,
                        backgroundColor = Color(0xFFFFF3E0),
                        iconColor = Color(0xFFF57C00),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("aprobaciones") }
                    )

                    // Aprobados Hoy
                    CounterCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Aprobados Hoy",
                        count = 18,
                        backgroundColor = Color(0xFFE8F5E9),
                        iconColor = Color(0xFF388E3C),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("aprobaciones") }
                    )
                }

                // ========== ACCIONES RÁPIDAS ==========
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Ver Aprobaciones
                QuickActionButton(
                    icon = Icons.Default.CheckCircle,
                    text = "Ver Aprobaciones",
                    onClick = { onNavigate("aprobaciones") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Enviar Mensaje
                QuickActionButton(
                    icon = Icons.Default.Send,
                    text = "Enviar Mensaje",
                    onClick = { onNavigate("enviar_mensaje") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Registro Directo
                QuickActionButton(
                    icon = Icons.Default.Assignment,
                    text = "Registro Directo",
                    onClick = { onNavigate("registro_directo") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Buscar Producto
                QuickActionButton(
                    icon = Icons.Default.Search,
                    text = "Buscar Producto",
                    onClick = { onNavigate("busqueda") }
                )
            }
        }
    }
}

/**
 * Tarjeta contador con ícono
 */
@Composable
private fun CounterCard(
    icon: ImageVector,
    title: String,
    count: Int,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
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
                tint = iconColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Botón de acción rápida con ícono
 */
@Composable
private fun QuickActionButton(
    icon: ImageVector,
    text: String,
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
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}