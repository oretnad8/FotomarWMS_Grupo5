package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.pneuma.fotomarwms_grupo5.model.UserRole
import com.pneuma.fotomarwms_grupo5.model.Usuario
import com.pneuma.fotomarwms_grupo5.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la UI para la pantalla de Login
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val usuarioActual: Usuario? = null
)

/**
 * ViewModel que maneja la lógica de autenticación
 *
 * Funciones principales:
 * - Validar credenciales del usuario
 * - Determinar rol y redirigir al dashboard correspondiente
 * - Manejar estados de carga y errores
 */
class LoginViewModel : ViewModel() {

    // Estado privado mutable
    private val _uiState = MutableStateFlow(LoginUiState())

    // Estado público inmutable para la UI
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Función de autenticación
     *
     * TODO: Conectar con API de autenticación cuando el backend esté listo
     * Por ahora usa datos de prueba
     *
     * @param email Email o usuario ingresado
     * @param password Contraseña ingresada
     * @param navController Controlador de navegación para redirigir
     */
    fun login(email: String, password: String, navController: NavController) {
        // Iniciar estado de carga
        _uiState.value = LoginUiState(isLoading = true)

        // Simulación de llamada a API (reemplazar con servicio real)
        // En producción: usar repository pattern y coroutines

        // Usuarios de prueba para desarrollo
        val usuariosPrueba = listOf(
            Usuario(1, "Admin Principal", "admin@fotomar.cl", UserRole.ADMIN),
            Usuario(2, "Jefe de Bodega", "jefe@fotomar.cl", UserRole.JEFE),
            Usuario(3, "Supervisor", "supervisor@fotomar.cl", UserRole.SUPERVISOR),
            Usuario(4, "Operador", "operador@fotomar.cl", UserRole.OPERADOR)
        )

        // Buscar usuario por email
        val usuario = usuariosPrueba.find { it.email == email }

        // Validar credenciales (en desarrollo, cualquier password funciona)
        if (usuario != null && password.isNotEmpty()) {
            // Login exitoso
            _uiState.value = LoginUiState(
                isLoading = false,
                usuarioActual = usuario
            )

            // Redirigir según el rol del usuario
            navegarSegunRol(usuario.rol, navController)

        } else {
            // Login fallido
            _uiState.value = LoginUiState(
                isLoading = false,
                errorMessage = "Credenciales inválidas. Intenta nuevamente."
            )
        }
    }

    /**
     * Redirige al dashboard correspondiente según el rol del usuario
     *
     * @param rol Rol del usuario autenticado
     * @param navController Controlador de navegación
     */
    private fun navegarSegunRol(rol: UserRole, navController: NavController) {
        val destino = when (rol) {
            UserRole.ADMIN -> Screen.DashboardAdmin
            UserRole.JEFE, UserRole.SUPERVISOR -> Screen.DashboardJefe
            UserRole.OPERADOR -> Screen.DashboardOperador
        }

        // Navegar y limpiar el stack (no permitir volver atrás al login)
        navController.navigate(destino.route) {
            popUpTo(Screen.Login.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}