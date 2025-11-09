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
import com.pneuma.fotomarwms_grupo5.viewmodels.ProductoViewModel

/**
 * Pantalla de Detalle de Producto con funcionalidad de edición
 * 
 * Incluye:
 * - Botones para escanear código de barras y LPN
 * - Edición manual de campos
 * - Asignación a ubicaciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreenNew(
    sku: String,
    productoViewModel: ProductoViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToUbicacion: (String) -> Unit,
    onNavigateToAsignarUbicacion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val productoDetailState by productoViewModel.productoDetailState.collectAsStateWithLifecycle()
    val selectedProducto by productoViewModel.selectedProducto.collectAsStateWithLifecycle()

    // Estados de edición
    var isEditMode by remember { mutableStateOf(false) }
    var showBarcodeScannerForIndividual by remember { mutableStateOf(false) }
    var showBarcodeScannerForLPN by remember { mutableStateOf(false) }
    
    // Campos editables
    var editCodigoBarras by remember { mutableStateOf("") }
    var editLpn by remember { mutableStateOf("") }
    var editLpnDesc by remember { mutableStateOf("") }
    var editDescripcion by remember { mutableStateOf("") }
    var editStock by remember { mutableStateOf("") }

    // Cargar detalle del producto al iniciar
    LaunchedEffect(sku) {
        productoViewModel.getProductoDetail(sku)
    }

    // Actualizar campos cuando se carga el producto
    LaunchedEffect(selectedProducto) {
        selectedProducto?.let { producto ->
            editCodigoBarras = producto.codigoBarrasIndividual ?: ""
            editLpn = producto.lpn ?: ""
            editLpnDesc = producto.lpnDesc ?: ""
            editDescripcion = producto.descripcion
            editStock = producto.stock.toString()
        }
    }

    // Escáner de código de barras individual
    if (showBarcodeScannerForIndividual) {
        BarcodeScanner(
            onBarcodeScanned = { codigo ->
                editCodigoBarras = codigo
                showBarcodeScannerForIndividual = false
            },
            onClose = { showBarcodeScannerForIndividual = false }
        )
        return
    }

    // Escáner de LPN
    if (showBarcodeScannerForLPN) {
        BarcodeScanner(
            onBarcodeScanned = { codigo ->
                editLpn = codigo
                showBarcodeScannerForLPN = false
            },
            onClose = { showBarcodeScannerForLPN = false }
        )
        return
    }

    Scaffold(
        topBar = {
            BackTopBar(
                title = if (isEditMode) "Editar Producto" else "Detalle del Producto",
                onBackClick = {
                    if (isEditMode) {
                        isEditMode = false
                    } else {
                        onNavigateBack()
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = productoDetailState) {
                is UiState.Loading -> {
                    LoadingState(message = "Cargando producto...")
                }

                is UiState.Success -> {
                    val producto = state.data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // ========== INFORMACIÓN PRINCIPAL ==========
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // SKU
                                Text(
                                    text = producto.sku,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Descripción (editable)
                                if (isEditMode) {
                                    OutlinedTextField(
                                        value = editDescripcion,
                                        onValueChange = { editDescripcion = it },
                                        label = { Text("Descripción") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = producto.descripcion,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Stock (editable)
                                if (isEditMode) {
                                    OutlinedTextField(
                                        value = editStock,
                                        onValueChange = { editStock = it },
                                        label = { Text("Stock") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    StockBadge(stock = producto.stock)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== CÓDIGOS CON ESCÁNER ==========
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Códigos",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Código de barras individual con botón de escáner
                                Text(
                                    text = "Código Individual",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isEditMode) {
                                        OutlinedTextField(
                                            value = editCodigoBarras,
                                            onValueChange = { editCodigoBarras = it },
                                            placeholder = { Text("Escanear o ingresar") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { showBarcodeScannerForIndividual = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CameraAlt,
                                                contentDescription = "Escanear código",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = producto.codigoBarrasIndividual ?: "No disponible",
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // LPN con botón de escáner
                                Text(
                                    text = "LPN (Código de Caja)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isEditMode) {
                                        OutlinedTextField(
                                            value = editLpn,
                                            onValueChange = { editLpn = it },
                                            placeholder = { Text("Escanear o ingresar") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { showBarcodeScannerForLPN = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CameraAlt,
                                                contentDescription = "Escanear LPN",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = producto.lpn ?: "No disponible",
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                // Descripción LPN (editable)
                                if (isEditMode) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedTextField(
                                        value = editLpnDesc,
                                        onValueChange = { editLpnDesc = it },
                                        label = { Text("Descripción LPN") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else if (producto.lpnDesc != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Descripción: ${producto.lpnDesc}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== FECHA DE VENCIMIENTO ==========
                        if (producto.fechaVencimiento != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (producto.vencimientoCercano)
                                        MaterialTheme.colorScheme.errorContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (producto.vencimientoCercano)
                                            Icons.Default.Warning
                                        else
                                            Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = if (producto.vencimientoCercano)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = if (producto.vencimientoCercano)
                                                "⚠️ Vencimiento Cercano"
                                            else
                                                "Fecha de Vencimiento",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (producto.vencimientoCercano)
                                                MaterialTheme.colorScheme.error
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = producto.fechaVencimiento,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // ========== UBICACIONES ==========
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Ubicaciones",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Botón para asignar nueva ubicación
                                    if (!isEditMode) {
                                        FilledTonalButton(
                                            onClick = { onNavigateToAsignarUbicacion(sku) },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Asignar")
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (producto.ubicaciones.isNullOrEmpty()) {
                                    Text(
                                        text = "Sin ubicación asignada",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    producto.ubicaciones.forEach { ubicacion ->
                                        Card(
                                            onClick = {
                                                onNavigateToUbicacion(ubicacion.codigoUbicacion)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = ubicacion.codigoUbicacion,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        text = "Cantidad: ${ubicacion.cantidadEnUbicacion}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        modifier = Modifier.padding(top = 4.dp)
                                                    )
                                                }

                                                Icon(
                                                    imageVector = Icons.Default.ChevronRight,
                                                    contentDescription = "Ver ubicación"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== BOTONES DE ACCIÓN ==========
                        if (isEditMode) {
                            // Botones de guardar/cancelar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        isEditMode = false
                                        // Restaurar valores originales
                                        selectedProducto?.let { producto ->
                                            editCodigoBarras = producto.codigoBarrasIndividual ?: ""
                                            editLpn = producto.lpn ?: ""
                                            editLpnDesc = producto.lpnDesc ?: ""
                                            editDescripcion = producto.descripcion
                                            editStock = producto.stock.toString()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancelar")
                                }

                                Button(
                                    onClick = {
                                        // TODO: Guardar cambios
                                        // productoViewModel.updateProducto(...)
                                        isEditMode = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Guardar")
                                }
                            }
                        } else {
                            // Botón de editar
                            Button(
                                onClick = { isEditMode = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Editar Producto")
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = {
                            productoViewModel.getProductoDetail(sku)
                        }
                    )
                }

                is UiState.Idle -> {
                    // No mostrar nada en estado Idle
                }
            }
        }
    }
}
