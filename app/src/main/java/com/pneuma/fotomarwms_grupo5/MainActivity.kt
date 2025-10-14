@file:OptIn(ExperimentalAnimationApi::class)

package com.pneuma.fotomarwms_grupo5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pneuma.fotomarwms_grupo5.navigation.Screen
import com.pneuma.fotomarwms_grupo5.ui.screen.*
import com.pneuma.fotomarwms_grupo5.ui.theme.TestTheme
import com.pneuma.fotomarwms_grupo5.viewmodels.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*   // ArrowForward, Remove, Add, Check, LocationOn, DragHandle
import androidx.compose.ui.graphics.vector.ImageVector



/**
 * MainActivity - Actividad principal de la aplicación FotomarWMS
 *
 * Gestiona:
 * - Navegación entre todas las pantallas
 * - Instancias de ViewModels compartidos
 * - Tema de la aplicación
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                FotomarWMSApp()
            }
        }
    }
}


@Composable
fun FotomarWMSApp() {
    // NavController para navegación
    val navController = rememberNavController()

    // ViewModels compartidos entre pantallas
    val authViewModel: AuthViewModel = viewModel()
    val productoViewModel: ProductoViewModel = viewModel()
    val ubicacionViewModel: UbicacionViewModel = viewModel()
    val aprobacionViewModel: AprobacionViewModel = viewModel()
    val mensajeViewModel: MensajeViewModel = viewModel()
    val inventarioViewModel: InventarioViewModel = viewModel()
    val usuarioViewModel: UsuarioViewModel = viewModel()

    // Layout base
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AnimatedNavHost( //Se cambia para que sea animada
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(paddingValues = innerPadding),
            //duracion de transisciones globales aplicadas a todas las pantallas
            //Transicion para entrar
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it / 3 },  // entra desde la derecha
                    animationSpec = tween(900, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(700))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 }, // sale a la izquierda
                    animationSpec = tween(750, easing = LinearOutSlowInEasing)
                ) + fadeOut(tween(400))
            },
            //Transicion para volver atras
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 3 }, // entra desde la izquierda (al volver)
                    animationSpec = tween(900)
                ) + fadeIn(tween(700))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it / 3 },
                    animationSpec = tween(750)
                ) + fadeOut(tween(400))
            }
        ) {
            // ========== LOGIN ==========
            composable(
                route = Screen.Login.route
            ){
                LoginScreen(
                    authViewModel = authViewModel,
                    onNavigateToHome = { route ->
                        navController.navigate(route) {
                            // Limpiar el stack al hacer login
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // ========== DASHBOARDS ==========

            // Dashboard Admin
            composable(
                route = Screen.DashboardAdmin.route
            ) {
                DashboardAdminScreen(
                    authViewModel = authViewModel,
                    usuarioViewModel = usuarioViewModel,
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // Dashboard Jefe
            composable(route = Screen.DashboardJefe.route) {
                DashboardJefeScreen(
                    authViewModel = authViewModel,
                    aprobacionViewModel = aprobacionViewModel,
                    mensajeViewModel = mensajeViewModel,
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // Dashboard Supervisor (usa el mismo que Jefe)
            composable(route = Screen.DashboardSupervisor.route) {
                DashboardJefeScreen(
                    authViewModel = authViewModel,
                    aprobacionViewModel = aprobacionViewModel,
                    mensajeViewModel = mensajeViewModel,
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // Dashboard Operador
            composable(route = Screen.DashboardOperador.route) {
                DashboardOperadorScreen(
                    authViewModel = authViewModel,
                    mensajeViewModel = mensajeViewModel,
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // ========== BÚSQUEDA Y PRODUCTOS ==========

            // Búsqueda de productos
            composable(route = Screen.Busqueda.route) {
                BusquedaScreen(
                    authViewModel = authViewModel,
                    productoViewModel = productoViewModel,
                    onNavigateToDetail = { sku ->
                        navController.navigate("detalle_producto/$sku")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Detalle de producto
            composable(
                route = "detalle_producto/{sku}",
                arguments = listOf(
                    navArgument("sku") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val sku = backStackEntry.arguments?.getString("sku") ?: ""
                DetalleProductoScreen(
                    sku = sku,
                    productoViewModel = productoViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToUbicacion = { codigo ->
                        navController.navigate("detalle_ubicacion/$codigo")
                    }
                )
            }

            // ========== UBICACIONES ==========

            // Gestión de ubicaciones
            composable(route = Screen.GestionUbicaciones.route) {
                GestionUbicacionesScreen(
                    authViewModel = authViewModel,
                    ubicacionViewModel = ubicacionViewModel,
                    onNavigateToDetail = { codigo ->
                        navController.navigate("detalle_ubicacion/$codigo")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Detalle de ubicación
            composable(
                route = "detalle_ubicacion/{codigo}",
                arguments = listOf(
                    navArgument("codigo") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val codigo = backStackEntry.arguments?.getString("codigo") ?: ""
                // TODO: Crear DetalleUbicacionScreen
                // Por ahora redirige atrás
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }

            // ========== MOVIMIENTOS ==========

            // Solicitud de movimiento (Operadores)
            composable(route = Screen.SolicitudMovimiento.route) {
                SolicitudMovimientoScreen(
                    aprobacionViewModel = aprobacionViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Registro directo (Jefe/Supervisor)
            composable(route = Screen.RegistroDirecto.route) {
                RegistroDirectoScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ========== APROBACIONES ==========

            // Lista de aprobaciones
            composable(route = Screen.Aprobaciones.route) {
                AprobacionesScreen(
                    authViewModel = authViewModel,
                    aprobacionViewModel = aprobacionViewModel,
                    onNavigateToDetail = { id ->
                        navController.navigate("detalle_aprobacion/$id")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Detalle de aprobación
            composable(
                route = "detalle_aprobacion/{id}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                DetalleAprobacionScreen(
                    aprobacionId = id,
                    aprobacionViewModel = aprobacionViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Mis solicitudes (Operadores)
            composable(route = Screen.MisSolicitudes.route) {
                // Reutiliza AprobacionesScreen pero carga solo las del usuario
                AprobacionesScreen(
                    authViewModel = authViewModel,
                    aprobacionViewModel = aprobacionViewModel,
                    onNavigateToDetail = { id ->
                        navController.navigate("detalle_aprobacion/$id")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )

                // Cargar solo mis solicitudes
                LaunchedEffect(Unit) {
                    aprobacionViewModel.getMisSolicitudes()
                }
            }

            // ========== MENSAJES ==========

            // Bandeja de mensajes
            composable(route = Screen.Mensajes.route) {
                MensajesScreen(
                    authViewModel = authViewModel,
                    mensajeViewModel = mensajeViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Enviar mensaje (Jefe/Supervisor)
            composable(Screen.EnviarMensaje.route) {
                val mensajeVM: MensajeViewModel = viewModel()   // o hiltViewModel()
                val usuarioVM: UsuarioViewModel = viewModel()   // o hiltViewModel()

                EnviarMensajeScreen(
                    mensajeViewModel = mensajeVM,
                    usuarioViewModel = usuarioVM,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ========== INVENTARIO ==========

            // Pantalla principal de inventario
            composable(route = Screen.Inventario.route) {
                InventarioScreen(
                    authViewModel = authViewModel,
                    inventarioViewModel = inventarioViewModel,
                    onNavigateToDiferencias = {
                        navController.navigate(Screen.DiferenciasInventario.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Diferencias de inventario
            composable(Screen.DiferenciasInventario.route) {
                val inventarioVM: InventarioViewModel = viewModel() // o hiltViewModel()
                DiferenciasInventarioScreen(
                    inventarioViewModel = inventarioVM,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Conteo de ubicación específica
            composable(
                route = "conteo_ubicacion/{idUbicacion}",
                arguments = listOf(
                    navArgument("idUbicacion") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val idUbicacion = backStackEntry.arguments?.getInt("idUbicacion") ?: 0
                // TODO: Crear ConteoUbicacionScreen
                // Por ahora redirige atrás
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }

            // ========== GESTIÓN DE USUARIOS (ADMIN) ==========

            // Lista de usuarios
            composable(route = Screen.GestionUsuarios.route) {
                GestionUsuariosScreen(
                    usuarioViewModel = usuarioViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Crear usuario
            composable(route = Screen.CrearUsuario.route) {
                // La creación se hace desde GestionUsuariosScreen con diálogo
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }

            // ========== PERFIL Y CONFIGURACIÓN ==========

            // Perfil del usuario
            composable(route = Screen.Perfil.route) {
                PerfilScreen(
                    authViewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            // Limpiar todo el stack
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Configuración
            composable(Screen.Configuracion.route) {
                ConfiguracionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

        }

    }

}