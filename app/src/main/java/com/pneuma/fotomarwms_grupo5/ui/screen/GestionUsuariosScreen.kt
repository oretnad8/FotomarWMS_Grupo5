package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pneuma.fotomarwms_grupo5.model.UserRole
import com.pneuma.fotomarwms_grupo5.model.Usuario
import com.pneuma.fotomarwms_grupo5.viewmodels.GestionUsuariosViewModel

/**
 * Pantalla de gestión de usuarios (solo ADMIN)
 *
 * Permite:
 * - Ver lista de todos los usuarios
 * - Crear nuevos usuarios
 * - Editar información de usuarios
 * - Activar/desactivar usuarios
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosScreen(
    navController: NavController,
    viewModel: GestionUsuariosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarDialogoNuevoUsuario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoNuevoUsuario = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar usuario")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EstadisticaUsuarioCard(
                    titulo = "Total",
                    valor = uiState.usuarios.size.toString(),
                    color = Color(0xFF42A5F5),
                    modifier = Modifier.weight(1f)
                )
                EstadisticaUsuarioCard(
                    titulo = "Activos",
                    valor = uiState.usuarios.count { it.activo }.toString(),
                    color = Color(0xFF66BB6A),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Usuarios Registrados",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de usuarios
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.usuarios) { usuario ->
                        UsuarioCard(
                            usuario = usuario,
                            onToggleActivo = { viewModel.toggleUsuarioActivo(it) }
                        )
                    }
                }
            }
        }
    }

    // Diálogo para crear nuevo usuario
    if (mostrarDialogoNuevoUsuario) {
        NuevoUsuarioDialog(
            onDismiss = { mostrarDialogoNuevoUsuario = false },
            onConfirm = { nombre, email, rol ->
                viewModel.crearUsuario(nombre, email, rol)
                mostrarDialogoNuevoUsuario = false
            }
        )
    }
}

/**
 * Card de estadística de usuarios
 */
@Composable
fun EstadisticaUsuarioCard(
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                fontSize = 32.sp,
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
 * Card de usuario individual
 */
@Composable
fun UsuarioCard(
    usuario: Usuario,
    onToggleActivo: (Usuario) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (usuario.rol) {
                            UserRole.ADMIN -> Color(0xFFEF5350)
                            UserRole.JEFE -> Color(0xFF42A5F5)
                            UserRole.SUPERVISOR -> Color(0xFFFFA726)
                            UserRole.OPERADOR -> Color(0xFF66BB6A)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuario.nombre,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = usuario.email,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Badge(
                    containerColor = when (usuario.rol) {
                        UserRole.ADMIN -> Color(0xFFEF5350)
                        UserRole.JEFE -> Color(0xFF42A5F5)
                        UserRole.SUPERVISOR -> Color(0xFFFFA726)
                        UserRole.OPERADOR -> Color(0xFF66BB6A)
                    }
                ) {
                    Text(
                        text = usuario.rol.name,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Switch de activo/inactivo
            Switch(
                checked = usuario.activo,
                onCheckedChange = { onToggleActivo(usuario) }
            )
        }
    }
}

/**
 * Diálogo para crear nuevo usuario
 */
@Composable
fun NuevoUsuarioDialog(
    onDismiss: () -> Unit,
    onConfirm: (nombre: String, email: String, rol: UserRole) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(UserRole.OPERADOR) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Rol:", fontSize = 14.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = rol == UserRole.OPERADOR,
                        onClick = { rol = UserRole.OPERADOR },
                        label = { Text("Operador", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = rol == UserRole.JEFE,
                        onClick = { rol = UserRole.JEFE },
                        label = { Text("Jefe", fontSize = 12.sp) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nombre, email, rol) },
                enabled = nombre.isNotBlank() && email.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}