package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.pneuma.fotomarwms_grupo5.navigation.Screen
import com.pneuma.fotomarwms_grupo5.viewmodels.DashboardAdminViewModel
import kotlinx.coroutines.launch

/**
 * Dashboard principal para usuarios con rol ADMINISTRADOR
 *
 * Muestra:
 * - Estadísticas generales del sistema
 * - Acciones administrativas (gestionar usuarios, descargar reportes)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    navController: NavController,
    viewModel: DashboardAdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerAdmin(
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
                        text = "Bienvenido, Administrador",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Estadísticas del sistema
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EstadisticaAdminCard(
                            titulo = "Usuarios Activos",
                            valor = uiState.usuariosActivos.toString(),
                            icono = Icons.Default.People,
                            color = Color(0xFF42A5F5),
                            modifier = Modifier.weight(1f)
                        )

                        EstadisticaAdminCard(
                            titulo = "Productos",
                            valor = uiState.totalProductos.toString(),
                            icono = Icons.Default.Inventory,
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
                        AccionAdminButton(
                            texto = "Gestionar Usuarios",
                            icono = Icons.Default.ManageAccounts,
                            onClick = { navController.navigate(Screen.GestionUsuarios.route) }
                        )

                        AccionAdminButton(
                            texto = "Descargar Reportes",
                            icono = Icons.Default.Download,
                            onClick = { /* TODO: Implementar descarga de reportes */ }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card de estadística para el administrador
 */
@Composable
fun EstadisticaAdminCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
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
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = valor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = titulo,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Botón de acción para el administrador
 */
@Composable
fun AccionAdminButton(
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
 * Drawer lateral con opciones de navegación para administrador
 */
@Composable
fun DrawerAdmin(onNavigate: (Screen) -> Unit) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Menú Administrador",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DrawerItem(
                text = "Gestionar Usuarios",
                icon = Icons.Default.ManageAccounts,
                onClick = { onNavigate(Screen.GestionUsuarios) }
            )

            DrawerItem(
                text = "Configuración",
                icon = Icons.Default.Settings,
                onClick = { onNavigate(Screen.Configuracion) }
            )

            DrawerItem(
                text = "Reportes",
                icon = Icons.Default.Assessment,
                onClick = { /* TODO */ }
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