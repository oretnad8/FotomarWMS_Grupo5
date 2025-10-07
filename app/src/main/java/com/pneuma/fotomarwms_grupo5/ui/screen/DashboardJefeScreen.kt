package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.pneuma.fotomarwms_grupo5.model.Alerta
import com.pneuma.fotomarwms_grupo5.navigation.Screen
import com.pneuma.fotomarwms_grupo5.viewmodels.DashboardJefeViewModel
import kotlinx.coroutines.launch

/**
 * Dashboard principal para usuarios con rol JEFE DE BODEGA
 *
 * Muestra:
 * - Alertas del sistema (stock bajo, vencimientos, solicitudes pendientes)
 * - Estadísticas de pendientes y aprobados
 * - Acciones rápidas (ver aprobaciones, enviar mensajes)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardJefeScreen(
    navController: NavController,
    viewModel: DashboardJefeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerJefe(
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
                        text = "Bienvenido, Jefe de Bodega",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Alertas del sistema
                item {
                    if (uiState.alertas.isNotEmpty()) {
                        AlertasCard(alertas = uiState.alertas)
                    }
                }

                // Estadísticas en cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EstadisticaCard(
                            titulo = "Pendientes",
                            valor = uiState.solicitudesPendientes.toString(),
                            icono = Icons.Default.Pending,
                            color = Color(0xFFFFA726),
                            modifier = Modifier.weight(1f)
                        )

                        EstadisticaCard(
                            titulo = "Aprobados Hoy",
                            valor = uiState.aprobadosHoy.toString(),
                            icono = Icons.Default.CheckCircle,
                            color = Color(0xFF66BB6A),
                            modifier = Modifier.weight(1f)
                        )
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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AccionRapidaJefeButton(
                            texto = "Ver Aprobaciones",
                            icono = Icons.Default.Approval,
                            onClick = { navController.navigate(Screen.Aprobaciones.route) }
                        )

                        AccionRapidaJefeButton(
                            texto = "Enviar Mensaje",
                            icono = Icons.Default.Message,
                            onClick = { navController.navigate(Screen.Mensajes.route) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card que muestra las alertas del sistema
 */
@Composable
fun AlertasCard(alertas: List<Alerta>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF9C4) // Amarillo claro
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alertas",
                    tint = Color(0xFFF57C00),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Alertas",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF57C00)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            alertas.forEach { alerta ->
                Text(
                    text = "• ${alerta.mensaje}",
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Card de estadística con ícono y valor
 */
@Composable
fun EstadisticaCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = valor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = titulo,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Botón de acción rápida para el jefe
 */
@Composable
fun AccionRapidaJefeButton(
    texto: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = texto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Drawer lateral con opciones de navegación para jefe
 */
@Composable
fun DrawerJefe(onNavigate: (Screen) -> Unit) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Menú Jefe de Bodega",
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
                text = "Gestionar Ubicaciones",
                icon = Icons.Default.LocationOn,
                onClick = { onNavigate(Screen.GestionUbicaciones) }
            )

            DrawerItem(
                text = "Registro Directo",
                icon = Icons.Default.AddBox,
                onClick = { onNavigate(Screen.RegistroDirecto) }
            )

            DrawerItem(
                text = "Aprobaciones",
                icon = Icons.Default.Approval,
                onClick = { onNavigate(Screen.Aprobaciones) }
            )

            DrawerItem(
                text = "Inventario",
                icon = Icons.Default.Inventory,
                onClick = { onNavigate(Screen.Inventario) }
            )

            DrawerItem(
                text = "Mensajes",
                icon = Icons.Default.Message,
                onClick = { onNavigate(Screen.Mensajes) }
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