package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.models.UiState
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.InventarioViewModel
import com.pneuma.fotomarwms_grupo5.viewmodels.UbicacionViewModel

/**
 * Pantalla de Conteo de Ubicación Específica
 * 
 * Permite:
 * - Ver información de la ubicación
 * - Ver productos que deberían estar según el sistema
 * - Registrar cantidad física encontrada de cada producto
 * - Agregar productos no esperados
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConteoUbicacionScreen(
    idUbicacion: Int,
    ubicacionViewModel: UbicacionViewModel,
    inventarioViewModel: InventarioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val ubicacionDetailState by ubicacionViewModel.ubicacionDetailState.collectAsStateWithLifecycle()
    val conteoState by inventarioViewModel.conteoState.collectAsStateWithLifecycle()
    
    // Estados de UI
    var showSuccessDialog by remember { mutableStateOf(false) }
    var conteosRealizados by remember { mutableStateOf(0) }
    
    // Cargar ubicación al iniciar
    LaunchedEffect(idUbicacion) {
        // TODO: Obtener ubicación por ID desde el backend
        // Por ahora usamos un código de ejemplo, pero debería obtenerse del backend
        // basado en idUbicacion
        ubicacionViewModel.getUbicacionDetail("A-12")
    }
    
    // Manejar éxito de conteo
    LaunchedEffect(conteoState) {
        when (val state = conteoState) {
            is UiState.Success -> {
                conteosRealizados++
                showSuccessDialog = true
                inventarioViewModel.clearConteoState()
            }
            else -> {}
        }
    }
    
    // Diálogo de éxito
    SuccessDialog(
        message = "Conteo registrado exitosamente",
        onDismiss = {
            showSuccessDialog = false
        },
        showDialog = showSuccessDialog
    )
    
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Conteo de Ubicación",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = ubicacionDetailState) {
                is UiState.Loading -> {
                    LoadingState(message = "Cargando ubicación...")
                }
                
                is UiState.Success -> {
                    val ubicacion = state.data
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // ========== INFORMACIÓN DE UBICACIÓN ==========
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = ubicacion.codigoUbicacion,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Piso ${ubicacion.piso} - Posición ${ubicacion.numero}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = "$conteosRealizados conteo(s)",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                        
                        // ========== INSTRUCCIONES ==========
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Instrucciones",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "1. Verifica físicamente cada producto\n2. Cuenta la cantidad exacta\n3. Registra la cantidad física encontrada",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                        
                        // ========== PRODUCTOS A CONTAR ==========
                        Text(
                            text = "Productos a Contar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        if (ubicacion.productos.isNullOrEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Inbox,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Sin productos registrados",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Según el sistema, esta ubicación está vacía",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            ubicacion.productos.forEach { producto ->
                                ProductoConteoCard(
                                    sku = producto.sku,
                                    descripcion = producto.descripcion,
                                    cantidadSistema = producto.cantidad,
                                    idUbicacion = ubicacion.idUbicacion,
                                    onRegistrarConteo = { sku, cantidadFisica ->
                                        inventarioViewModel.registrarConteo(
                                            sku = sku,
                                            idUbicacion = idUbicacion,
                                            cantidadFisica = cantidadFisica
                                        )
                                    },
                                    isLoading = conteoState is UiState.Loading
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // ========== BOTÓN FINALIZAR ==========
                        SecondaryButton(
                            text = "Finalizar Conteo de esta Ubicación",
                            onClick = {
                                onNavigateBack()
                            },
                            icon = Icons.Default.CheckCircle
                        )
                    }
                }
                
                is UiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = {
                            ubicacionViewModel.getUbicacionDetail("A-12")
                        }
                    )
                }
                
                is UiState.Idle -> {}
            }
        }
    }
}

/**
 * Card para contar un producto específico
 */
@Composable
private fun ProductoConteoCard(
    sku: String,
    descripcion: String,
    cantidadSistema: Int,
    idUbicacion: Int,
    onRegistrarConteo: (String, Int) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var cantidadFisica by remember { mutableStateOf("") }
    var yaContado by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // SKU y descripción
            Text(
                text = sku,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Cantidad según sistema
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cantidad Sistema: $cantidadSistema",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (!yaContado) {
                // Campo para cantidad física
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberTextField(
                        value = cantidadFisica,
                        onValueChange = { cantidadFisica = it },
                        label = "Cantidad Física",
                        placeholder = "0",
                        modifier = Modifier.weight(1f)
                    )
                    
                    Button(
                        onClick = {
                            if (cantidadFisica.isNotBlank()) {
                                onRegistrarConteo(sku, cantidadFisica.toInt())
                                yaContado = true
                            }
                        },
                        enabled = cantidadFisica.isNotBlank() && !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Registrar"
                        )
                    }
                }
            } else {
                // Mensaje de conteo registrado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✓ Conteo registrado: $cantidadFisica unidades",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}