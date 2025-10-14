package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.models.UiState
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.MensajeViewModel
import com.pneuma.fotomarwms_grupo5.viewmodels.UsuarioViewModel


/**
 * Pantalla para Enviar Mensaje
 * Solo para JEFE y SUPERVISOR
 * 
 * Permite:
 * - Enviar mensaje a un operador específico
 * - Enviar mensaje broadcast (a todos)
 * - Marcar mensaje como importante
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnviarMensajeScreen(
    mensajeViewModel: MensajeViewModel,
    usuarioViewModel: UsuarioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val enviarState by mensajeViewModel.enviarMensajeState.collectAsStateWithLifecycle()
    val usuariosState by usuarioViewModel.usuariosState.collectAsStateWithLifecycle()
    
    // Estados del formulario
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var importante by remember { mutableStateOf(false) }
    var destinatarioSeleccionado by remember { mutableStateOf<Int?>(null) }
    var esBroadcast by remember { mutableStateOf(false) }
    
    // Estados de validación
    var tituloError by remember { mutableStateOf<String?>(null) }
    var contenidoError by remember { mutableStateOf<String?>(null) }
    
    // Estados de UI
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Cargar usuarios operadores al iniciar
    LaunchedEffect(Unit) {
        usuarioViewModel.getAllUsuarios()
    }
    
    // Manejar éxito de envío
    LaunchedEffect(enviarState) {
        when (val state = enviarState) {
            is UiState.Success -> {
                showSuccessDialog = true
                // Limpiar formulario
                titulo = ""
                contenido = ""
                importante = false
                destinatarioSeleccionado = null
                esBroadcast = false
                mensajeViewModel.clearEnviarState()
            }
            else -> {}
        }
    }
    
    // Diálogo de éxito
    SuccessDialog(
        title = "¡Mensaje Enviado!",
        message = "El mensaje ha sido enviado exitosamente.",
        onDismiss = {
            showSuccessDialog = false
            onNavigateBack()
        },
        showDialog = showSuccessDialog
    )
    
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Enviar Mensaje",
                onBackClick = onNavigateBack
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
            // ========== TIPO DE MENSAJE ==========
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Destinatario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            text = "Individual",
                            selected = !esBroadcast,
                            onClick = { esBroadcast = false },
                            modifier = Modifier.weight(1f)
                        )
                        
                        FilterChip(
                            text = "Todos (Broadcast)",
                            selected = esBroadcast,
                            onClick = {
                                esBroadcast = true
                                destinatarioSeleccionado = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // ========== SELECCIÓN DE DESTINATARIO INDIVIDUAL ==========
            if (!esBroadcast) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Selecciona un Operador",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        when (val state = usuariosState) {
                            is UiState.Success -> {
                                val operadores = state.data.filter { 
                                    it.rol.name == "OPERADOR" && it.activo 
                                }
                                
                                if (operadores.isEmpty()) {
                                    Text(
                                        text = "No hay operadores disponibles",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    operadores.forEach { usuario ->
                                        FilterChip(
                                            text = usuario.nombre,
                                            selected = destinatarioSeleccionado == usuario.id,
                                            onClick = { destinatarioSeleccionado = usuario.id },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            is UiState.Loading -> {
                                CircularProgressIndicator()
                            }
                            else -> {}
                        }
                    }
                }
            }
            
            // ========== FORMULARIO DEL MENSAJE ==========
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Contenido del Mensaje",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Título
                    AppTextField(
                        value = titulo,
                        onValueChange = { 
                            titulo = it
                            tituloError = null
                        },
                        label = "Título",
                        placeholder = "Asunto del mensaje",
                        leadingIcon = Icons.Default.Title,
                        isError = tituloError != null,
                        errorMessage = tituloError
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Contenido
                    MultilineTextField(
                        value = contenido,
                        onValueChange = { 
                            contenido = it
                            contenidoError = null
                        },
                        label = "Mensaje",
                        placeholder = "Escribe tu mensaje aquí...",
                        minLines = 5,
                        maxLines = 10
                    )
                    
                    if (contenidoError != null) {
                        Text(
                            text = contenidoError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Checkbox importante
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = importante,
                            onCheckedChange = { importante = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Marcar como importante",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // ========== BOTÓN ENVIAR ==========
            PrimaryButton(
                text = "Enviar Mensaje",
                onClick = {
                    // Validar formulario
                    var isValid = true
                    
                    if (titulo.isBlank()) {
                        tituloError = "El título es obligatorio"
                        isValid = false
                    }
                    
                    if (contenido.isBlank()) {
                        contenidoError = "El mensaje no puede estar vacío"
                        isValid = false
                    }
                    
                    if (!esBroadcast && destinatarioSeleccionado == null) {
                        isValid = false
                        // Mostrar mensaje de error
                    }
                    
                    // Si es válido, enviar
                    if (isValid) {
                        if (esBroadcast) {
                            mensajeViewModel.enviarMensajeBroadcast(
                                titulo = titulo,
                                contenido = contenido,
                                importante = importante
                            )
                        } else {
                            mensajeViewModel.enviarMensaje(
                                idDestinatario = destinatarioSeleccionado!!,
                                titulo = titulo,
                                contenido = contenido,
                                importante = importante
                            )
                        }
                    }
                },
                enabled = enviarState !is UiState.Loading,
                icon = Icons.Default.Send
            )
            
            if (enviarState is UiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}