# 📱 FotomarWMS - Guía Completa del Proyecto

## 🎯 Resumen del Proyecto

**FotomarWMS** es un sistema completo de gestión de bodega (WMS) para productos fotográficos, desarrollado con **Jetpack Compose** y arquitectura **MVVM**.

### Integrantes
- Dante Rojas
- Martin Villegas

---

## 📁 Estructura del Proyecto

```
app/src/main/java/com/pneuma/fotomarwms_grupo5/
├── MainActivity.kt              # Actividad principal con navegación y animaciones
├── model/
│   └── ... (Models.kt)          # Modelos de datos (Producto, Usuario, etc.)
├── navigation/
│   ├── Screen.kt                # Definición de rutas
│   └── NavigationEvent.kt       # Eventos de navegación
├── db/                          # NUEVO: Base de datos Room (Offline-First)
│   ├── AppDatabase.kt           # Definición de la base de datos
│   ├── daos/                    # Data Access Objects (ConteoDao, MensajeDao, etc.)
│   └── entities/                # Entidades locales (ConteoLocal, MensajeLocal, etc.)
├── viewmodels/                  # Lógica de presentación MVVM
│   ├── AuthViewModel.kt
│   ├── ProductoViewModel.kt
│   ├── UbicacionViewModel.kt
│   ├── AprobacionViewModel.kt
│   ├── MensajeViewModel.kt
│   ├── InventarioViewModel.kt
│   └── UsuarioViewModel.kt
├── ui/
│   ├── screen/                  # Pantallas composables
│   │   ├── LoginScreen.kt
│   │   ├── DashboardAdminScreen.kt
│   │   ├── DashboardJefeScreen.kt
│   │   ├── DashboardOperadorScreen.kt
│   │   ├── BusquedaScreen.kt
│   │   ├── DetalleProductoScreen.kt
│   │   ├── GestionUbicacionesScreen.kt
│   │   ├── DetalleUbicacionScreen.kt
│   │   ├── AprobacionesScreen.kt
│   │   ├── DetalleAprobacionScreen.kt
│   │   ├── SolicitudMovimientoScreen.kt
│   │   ├── RegistroDirectoScreen.kt
│   │   ├── InventarioScreen.kt
│   │   ├── DiferenciasInventarioScreen.kt
│   │   ├── ConteoUbicacionScreen.kt
│   │   ├── MensajesScreen.kt
│   │   ├── EnviarMensajeScreen.kt
│   │   ├── GestionUsuariosScreen.kt
│   │   ├── PerfilScreen.kt
│   │   ├── ConfiguracionScreen.kt
│   │   └── SplashScreen.kt
│   ├── componentes/             # Componentes reutilizables
│   │   ├── BarcodeScanner.kt    # NUEVO: Componente de escáner
│   │   ├── Buttons.kt, Cards.kt, etc.
│   └── theme/                   # Temas y estilos
│       ├── Color.kt, Theme.kt, Type.kt
```

---

## 🎨 Pantallas Implementadas (13 pantallas)

### 1. **LoginScreen** 🔐
- Autenticación de usuarios
- Diferenciación de roles (ADMIN, JEFE, SUPERVISOR, OPERADOR)
- Redireccionamiento automático según rol

### 2. **DashboardOperadorScreen** 👷
- Mensajes del jefe de bodega
- Tareas pendientes
- Acciones rápidas: Buscar producto, Solicitar movimiento

### 3. **DashboardJefeScreen** 👔
- Alertas del sistema (stock bajo, vencimientos, solicitudes)
- Estadísticas (pendientes, aprobados hoy)
- Acciones rápidas: Ver aprobaciones, Enviar mensaje

### 4. **DashboardAdminScreen** 🔧
- Estadísticas generales (usuarios activos, productos totales)
- Gestión de usuarios
- Descarga de reportes

### 5. **BusquedaScreen** 🔍
- Botón prominente para escanear código de barras con cámara
- Búsqueda manual por SKU, descripción o ubicación
- Resultados en cards con botón "Ver Detalles"

### 6. **DetalleProductoScreen** 📦
- Información completa del producto (SKU, descripción, stock)
- Códigos de barras (individual y LPN)
- Ubicaciones con cantidades
- Alerta de vencimiento cercano

### 7. **AprobacionesScreen** ✅
- Tabs: Pendiente, Aprobado, Rechazado
- Cards de solicitudes con información detallada
- Botones de aprobar/rechazar para solicitudes pendientes

### 8. **MensajesScreen** 💬
- Tabs: Mensajes del Jefe, Tareas Pendientes
- Badges de "RECIENTE" para mensajes importantes
- Estado de lectura

### 9. **SolicitudMovimientoScreen** 📝
- Tipos de movimiento: Ingreso, Egreso, Reubicación
- Formulario completo con SKU, cantidad, ubicaciones, motivo
- Requiere aprobación del jefe

### 10. **InventarioScreen** 📊
- Progreso general y por piso (A, B, C)
- Diferencias encontradas (sistema vs físico)
- Botón para escanear o ingresar manualmente
- Finalizar inventario

### 11. **GestionUbicacionesScreen** 📍
- Filtros por piso (A, B, C)
- 180 ubicaciones totales (60 por piso)
- Ver productos en cada ubicación
- Estado: Disponible u Ocupada

### 12. **PerfilScreen** 👤
- Información del usuario (nombre, email, rol)
- Cambiar contraseña
- Cerrar sesión

### 13. **GestionUsuariosScreen** 👥 (Solo ADMIN)
- Lista de usuarios con estados
- Crear nuevos usuarios
- Activar/desactivar usuarios
- Asignar roles

---

## 🔑 Roles y Permisos

### ADMIN (Administrador)
- ✅ Crear usuarios
- ✅ Gestionar configuración del sistema
- ✅ Descargar reportes
- ❌ No gestiona operaciones de bodega

### JEFE DE BODEGA
- ✅ Registro directo de ingresos/egresos (sin aprobación)
- ✅ Aprobación de solicitudes sin restricciones
- ✅ Gestión de ubicaciones
- ✅ Envío de mensajes a operadores

### SUPERVISOR
- ✅ Aprueba solicitudes (pero notifica al jefe)
- ✅ Similar a Jefe pero con notificación adicional

### OPERADOR
- ✅ Solo solicitudes (requieren aprobación)
- ✅ Búsqueda de productos
- ✅ Inventario
- ✅ Puede cambiar productos de ubicación con justificación
- ❌ No puede aprobar

---

## 💾 Modelos de Datos Principales

### Usuario
```kotlin
data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: UserRole,
    val activo: Boolean = true
)
```

### Producto
```kotlin
data class Producto(
    val sku: String,                    // Formato: AB12345
    val descripcion: String,
    val stock: Int,
    val codigoBarrasIndividual: String?,
    val lpn: String?,                   // Código de caja
    val fechaVencimiento: Date?,
    val vencimientoCercano: Boolean,
    val ubicaciones: List<ProductoUbicacion>
)
```

### Aprobación
```kotlin
data class Aprobacion(
    val id: Int,
    val tipoMovimiento: TipoMovimiento,
    val producto: Producto,
    val cantidad: Int,
    val motivo: String,
    val solicitante: Usuario,
    val estado: EstadoAprobacion,
    val aprobador: Usuario?,
    val observaciones: String?
)
```

---

## 🚀 Cómo Probar la App

### Usuarios de Prueba

| Email | Password | Rol |
|-------|----------|-----|
| admin@fotomar.cl | cualquiera | ADMIN |
| jefe@fotomar.cl | cualquiera | JEFE |
| supervisor@fotomar.cl | cualquiera | SUPERVISOR |
| operador@fotomar.cl | cualquiera | OPERADOR |

### Flujo de Prueba Sugerido

1. **Login como Operador** → Ver dashboard con mensajes y tareas
2. **Buscar Producto** → Probar búsqueda manual (ej: "Canon")
3. **Ver Detalle** → Click en cualquier resultado
4. **Solicitar Movimiento** → Crear solicitud de ingreso
5. **Cerrar Sesión** → Volver al login
6. **Login como Jefe** → Ver alertas y pendientes
7. **Ir a Aprobaciones** → Ver solicitud del operador
8. **Aprobar/Rechazar** → Procesar solicitud
9. **Inventario** → Ver progreso por piso
10. **Login como Admin** → Gestionar usuarios

---

## 🔧 Pendiente de Implementación (Backend)

### Cuando conectes con tu backend Spring Boot:

1. **Crear Repositories**:
```kotlin
// Ejemplo: ProductoRepository.kt
class ProductoRepository {
    suspend fun buscarProductos(query: String): List<Producto> {
        // Llamada a API REST
        return api.get("/productos/search?q=$query")
    }
}
```

2. **Actualizar ViewModels**:
- Reemplazar datos de prueba con llamadas a repository
- Usar coroutines para operaciones asíncronas
- Manejar errores de red

3. **Agregar Retrofit/Ktor**:
```kotlin
// build.gradle.kts
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
```

4. **Implementar Autenticación Real**:
- JWT tokens
- Persistencia de sesión
- Refresh tokens

5. **Escaneo de Códigos de Barras**:
```kotlin
// Agregar en build.gradle.kts
implementation("com.google.mlkit:barcode-scanning:17.2.0")
```

---

## 📝 TODOs Principales

### Funcionalidades Críticas
- [ ] Integración con backend Spring Boot
- [ ] Autenticación con JWT
- [ ] Escaneo de códigos de barras con cámara
- [ ] Sistema de notificaciones push
- [ ] Descarga de reportes en PDF/Excel
- [ ] Configuración de alertas personalizadas

### Mejoras UX/UI
- [ ] Animaciones de transición entre pantallas
- [ ] Pull-to-refresh en listas
- [ ] Skeleton screens durante carga
- [ ] Dark mode completo
- [ ] Soporte para tablet

### Técnicas
- [ ] Tests unitarios de ViewModels
- [ ] Tests de UI con Compose
- [ ] Manejo de errores robusto
- [ ] Logs y analytics
- [ ] Caché offline con Room

---

## 📞 Contacto y Soporte

**Equipo de Desarrollo:**
- Dante Rojas
- Martin Villegas

**Asignatura:** Desarrollo Aplicaciones Móviles  
**Versión:** 1.0  
**Fecha:** Septiembre 2025

---

## 🏆 Características Destacadas

✅ **Arquitectura MVVM** completa y bien estructurada  
✅ **13 pantallas funcionales** con navegación fluida  
✅ **4 roles diferenciados** con permisos específicos  
✅ **Sistema de aprobaciones** completo  
✅ **Inventario con cuadre** de diferencias  
✅ **180 ubicaciones** de bodega gestionables  
✅ **Diseño Material Design 3** moderno  
✅ **Preparado para integración** con backend

---

**¡Éxito con tu proyecto FotomarWMS! 🚀📦**
