package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pneuma.fotomarwms_grupo5.model.Mensaje
import com.pneuma.fotomarwms_grupo5.viewmodels.MensajesViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla de Mensajes
 *
 * Muestra:
 * - Mensajes del jefe de bodega a operadores
 * - Notificaciones del sistema
 * - Permite marcar como leído
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajesScreen(
    navController: NavController,
    viewModel: MensajesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var tabSeleccionado by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mensajes") },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Pestañas: Mensajes del Jefe / Tareas Pendientes
            TabRow(selectedTabIndex = tabSeleccionado) {
                Tab(
                    selected = tabSeleccionado == 0,
                    onClick = { tabSeleccionado = 0 },
                    text = { Text("Mensajes del Jefe") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = "Mensajes"
                        )
                    }
                )
                Tab(
                    selected = tabSeleccionado == 1,
                    onClick = { tabSeleccionado = 1 },
                    text = { Text("Tareas Pendientes") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Tareas"
                        )
                    }
                )
            }

            // Contenido según pestaña seleccionada
            when (tabSeleccionado) {
                0 -> MensajesDelJefeTab(
                    mensajes = uiState.mensajes,
                    onMensajeClick = { viewModel.marcarComoLeido(it.id) }
                )
                1 -> TareasPendientesTab(uiState.tareas)
            }
        }
    }
}

/**
 * Tab que muestra los mensajes del jefe
 */
@Composable
fun MensajesDelJefeTab(
    mensajes: List<Mensaje>,
    onMensajeClick: (Mensaje) -> Unit
) {
    if (mensajes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = "Sin mensajes",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hay mensajes",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mensajes) { mensaje ->
                MensajeCard(
                    mensaje = mensaje,
                    onClick = { onMensajeClick(mensaje) }
                )
            }
        }
    }
}

/**
 * Card que representa un mensaje del jefe
 */
@Composable
fun MensajeCard(
    mensaje: Mensaje,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (mensaje.leido)
                MaterialTheme.colorScheme.surface
            else
                Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar del remitente
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mensaje.emisor.nombre.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Header: Remitente y hora
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = mensaje.emisor.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (mensaje.importante) {
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = "Urgente",
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = timeFormat.format(mensaje.fecha),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Título del mensaje
                Text(
                    text = mensaje.titulo,
                    fontSize = 13.sp,
                    fontWeight = if (!mensaje.leido) FontWeight.SemiBold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Contenido del mensaje (preview)
                Text(
                    text = mensaje.contenido,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                // Badge si no está leído
                if (!mensaje.leido) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "Marcar como leído",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab que muestra las tareas pendientes
 */
@Composable
fun TareasPendientesTab(tareas: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tareas) { tarea ->
            TareaPendienteCard(descripcion = tarea)
        }
    }
}

/**
 * Card que representa una tarea pendiente
 */
@Composable
fun TareaPendienteCard(descripcion: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF9C4) // Amarillo claro
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = "Tarea",
                tint = Color(0xFFFFA726),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = descripcion,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "PENDIENTE",
                    fontSize = 11.sp,
                    color = Color(0xFFF57C00)
                )
            }
        }
    }
}