package com.pneuma.fotomarwms_grupo5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pneuma.fotomarwms_grupo5.navigation.Screen
import com.pneuma.fotomarwms_grupo5.ui.theme.TestTheme
import com.pneuma.fotomarwms_grupo5.ui.screen.AprobacionesScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.BusquedaScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.DashboardAdminScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.DashboardJefeScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.DashboardOperadorScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.DetalleProductoScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.GestionUbicacionesScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.GestionUsuariosScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.InventarioScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.LoginScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.MensajesScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.PerfilScreen
import com.pneuma.fotomarwms_grupo5.ui.screen.SolicitudMovimientoScreen

/**
 * Actividad principal de FotomarWMS
 *
 * Configura el sistema de navegación para todas las pantallas de la aplicación
 * basándose en la arquitectura MVVM con Jetpack Compose
 */
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                // Controlador de navegación
                val navController = rememberNavController()

                // Layout base con NavHost
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route, // Inicia en Login
                        modifier = Modifier.padding(paddingValues = innerPadding)
                    ) {
                        // ============ AUTENTICACIÓN ============

                        /** Pantalla de Login */
                        composable(route = Screen.Login.route) {
                            LoginScreen(navController = navController)
                        }

                        // ============ DASHBOARDS POR ROL ============

                        /** Dashboard para OPERADOR */
                        composable(route = Screen.DashboardOperador.route) {
                            DashboardOperadorScreen(navController = navController)
                        }

                        /** Dashboard para JEFE DE BODEGA */
                        composable(route = Screen.DashboardJefe.route) {
                            DashboardJefeScreen(navController = navController)
                        }

                        /** Dashboard para ADMINISTRADOR */
                        composable(route = Screen.DashboardAdmin.route) {
                            DashboardAdminScreen(navController = navController)
                        }

                        // ============ BÚSQUEDA Y CONSULTAS ============

                        /** Pantalla de búsqueda de productos */
                        composable(route = Screen.Busqueda.route) {
                            BusquedaScreen(navController = navController)
                        }

                        /** Detalle de producto específico (recibe SKU como parámetro) */
                        composable(
                            route = Screen.DetalleProducto.ROUTE_PATTERN,
                            arguments = listOf(
                                navArgument("sku") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val sku = backStackEntry.arguments?.getString("sku") ?: ""
                            DetalleProductoScreen(
                                navController = navController,
                                sku = sku
                            )
                        }

                        // ============ MOVIMIENTOS ============

                        /** Solicitud de movimiento (OPERADOR) */
                        composable(route = Screen.SolicitudMovimiento.route) {
                            SolicitudMovimientoScreen(navController = navController)
                        }

                        /** Registro directo de movimientos (JEFE) */
                        composable(route = Screen.RegistroDirecto.route) {
                            // TODO: Crear RegistroDirectoScreen
                            // Por ahora redirige a la misma pantalla de solicitud
                            SolicitudMovimientoScreen(navController = navController)
                        }

                        // ============ APROBACIONES ============

                        /** Pantalla de aprobaciones (solo JEFE) */
                        composable(route = Screen.Aprobaciones.route) {
                            AprobacionesScreen(navController = navController)
                        }

                        // ============ MENSAJES ============

                        /** Mensajes del jefe y notificaciones del sistema */
                        composable(route = Screen.Mensajes.route) {
                            MensajesScreen(navController = navController)
                        }

                        // ============ GESTIÓN DE UBICACIONES ============

                        /** Gestión de ubicaciones de bodega */
                        composable(route = Screen.GestionUbicaciones.route) {
                            GestionUbicacionesScreen(navController = navController)
                        }

                        // ============ INVENTARIO ============

                        /** Pantalla de inventario con cuadre de diferencias */
                        composable(route = Screen.Inventario.route) {
                            InventarioScreen(navController = navController)
                        }

                        // ============ PERFIL Y CONFIGURACIÓN ============

                        /** Perfil del usuario actual */
                        composable(route = Screen.Perfil.route) {
                            PerfilScreen(navController = navController)
                        }

                        /** Configuración de la aplicación (solo ADMIN) */
                        composable(route = Screen.Configuracion.route) {
                            PlaceholderScreen(
                                navController = navController,
                                titulo = "Configuración"
                            )
                        }

                        /** Gestión de usuarios (solo ADMIN) */
                        composable(route = Screen.GestionUsuarios.route) {
                            GestionUsuariosScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Pantalla placeholder para funcionalidades pendientes
 * Muestra el título y un botón para volver
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    navController: androidx.navigation.NavController,
    titulo: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
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
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Construction,
                    contentDescription = "En construcción",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$titulo - En Desarrollo",
                    style = MaterialTheme.typography.titleLarge
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esta funcionalidad estará disponible próximamente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}