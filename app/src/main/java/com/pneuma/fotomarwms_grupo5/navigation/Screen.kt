package com.pneuma.fotomarwms_grupo5.navigation

/**
 * Sistema de navegación centralizado para FotomarWMS
 * Define todas las pantallas disponibles en la aplicación
 */
sealed class Screen(val route: String) {

    // ============ AUTENTICACIÓN ============
    /** Pantalla de inicio de sesión */
    data object Login : Screen(route = "login_screen")

    // ============ DASHBOARDS POR ROL ============
    /** Dashboard para usuarios con rol OPERADOR */
    data object DashboardOperador : Screen(route = "dashboard_operador")

    /** Dashboard para usuarios con rol JEFE DE BODEGA */
    data object DashboardJefe : Screen(route = "dashboard_jefe")

    /** Dashboard para usuarios con rol ADMINISTRADOR */
    data object DashboardAdmin : Screen(route = "dashboard_admin")

    // ============ BÚSQUEDA Y CONSULTAS ============
    /** Pantalla de búsqueda de productos con escaneo o manual */
    data object Busqueda : Screen(route = "busqueda_screen")

    /** Detalle de un producto específico con parámetro SKU */
    data class DetalleProducto(val sku: String) : Screen(route = "detalle_producto/{sku}") {
        fun buildRoute(): String = route.replace("{sku}", sku)
        companion object {
            const val ROUTE_PATTERN = "detalle_producto/{sku}"
        }
    }

    // ============ GESTIÓN DE UBICACIONES ============
    /** Pantalla para gestionar ubicaciones de bodega */
    data object GestionUbicaciones : Screen(route = "gestion_ubicaciones")

    // ============ MOVIMIENTOS ============
    /** Solicitud de ingreso/egreso (para OPERADOR) */
    data object SolicitudMovimiento : Screen(route = "solicitud_movimiento")

    /** Registro directo de movimientos (para JEFE) */
    data object RegistroDirecto : Screen(route = "registro_directo")

    // ============ APROBACIONES ============
    /** Pantalla de aprobaciones pendientes (solo JEFE) */
    data object Aprobaciones : Screen(route = "aprobaciones_screen")

    // ============ INVENTARIO ============
    /** Pantalla de inventario con cuadre de diferencias */
    data object Inventario : Screen(route = "inventario_screen")

    // ============ MENSAJES ============
    /** Mensajes del jefe y notificaciones del sistema */
    data object Mensajes : Screen(route = "mensajes_screen")

    // ============ PERFIL Y CONFIGURACIÓN ============
    /** Perfil del usuario actual */
    data object Perfil : Screen(route = "perfil_screen")

    /** Configuración de la aplicación (solo ADMIN) */
    data object Configuracion : Screen(route = "configuracion_screen")

    /** Gestión de usuarios (solo ADMIN) */
    data object GestionUsuarios : Screen(route = "gestion_usuarios")
}