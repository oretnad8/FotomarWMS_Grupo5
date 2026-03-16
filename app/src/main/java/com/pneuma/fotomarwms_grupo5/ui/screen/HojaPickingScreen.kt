package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.network.DetallePickingCheck
import com.pneuma.fotomarwms_grupo5.network.EstadoPedido
import com.pneuma.fotomarwms_grupo5.network.ItemPicking
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.BackTopBar
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.BarcodeScanner
import com.pneuma.fotomarwms_grupo5.viewmodels.PedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HojaPickingScreen(
    pedidoId: Int,
    viewModel: PedidosViewModel,
    onNavigateBack: () -> Unit
) {
    val pickingData by viewModel.hojaPicking.collectAsStateWithLifecycle()
    val currentPedido by viewModel.currentPedido.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var selectedItem by remember { mutableStateOf<ItemPicking?>(null) }
    val pickingResults = remember { mutableStateMapOf<String, Int>() }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(pedidoId) {
        viewModel.loadHojaPicking(pedidoId)
        viewModel.loadPedido(pedidoId)
    }

    // Auto-calculate progress
    val totalItems = pickingData?.items?.sumOf { it.displayCantidad } ?: 0
    val totalPicked = pickingResults.values.sum()
    val progress = if (totalItems > 0) totalPicked.toFloat() / totalItems.toFloat() else 0f
    val isComplete = progress >= 1f && totalItems > 0

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Pedido #${pedidoId}",
                onBackClick = onNavigateBack
            )
        },
        bottomBar = {
            if (currentPedido?.estado == EstadoPedido.EN_PICKING && pickingData != null) {
                PickingProgressFooter(
                    progress = progress,
                    isComplete = isComplete,
                    totalItems = totalItems,
                    totalPicked = totalPicked,
                    onFinishClick = {
                        val checks = pickingData!!.items?.map { item ->
                            val sku = item.sku ?: ""
                            val qty = pickingResults[sku] ?: 0
                            DetallePickingCheck(sku, qty)
                        } ?: emptyList()
                        viewModel.confirmarPicking(pedidoId, checks)
                        onNavigateBack()
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (currentPedido == null && pickingData == null) {
                Text("Cargando información...", modifier = Modifier.align(Alignment.Center))
            } else {
                val pedido = currentPedido
                val estado = pedido?.estado ?: EstadoPedido.PENDIENTE // Default to PENDIENTE if null

                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Info
                    PickingHeader(
                        cliente = pedido?.displayCliente ?: pickingData?.displayCliente ?: "Cliente Desconocido",
                        fecha = pedido?.fecha ?: "Fecha desconocida",
                        estado = estado
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    if (estado == EstadoPedido.PENDIENTE) {
                        // State: PENDIENTE -> Show "Tomar Pedido"
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Button(
                                onClick = { viewModel.aceptarPedido(pedidoId) },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("TOMAR PEDIDO", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    } else {
                        // State: EN_PICKING (or others) -> Show List
                        val items = pickingData?.items ?: emptyList()
                        if (items.isEmpty()) {
                            Text("No hay items para pickear", modifier = Modifier.padding(16.dp))
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(items) { item ->
                                    val pickedQty = pickingResults[item.sku] ?: 0
                                    
                                    PickingItemCard(
                                        item = item,
                                        pickedQuantity = pickedQty,
                                        onClick = { 
                                            selectedItem = item
                                            showBottomSheet = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BottomSheet for Picking
            if (showBottomSheet && selectedItem != null) {
                ModalBottomSheet(
                    onDismissRequest = { 
                        showBottomSheet = false
                        selectedItem = null
                    },
                    sheetState = rememberModalBottomSheetState()
                ) {
                    PickingBottomSheetContent(
                        item = selectedItem!!,
                        currentPicked = pickingResults[selectedItem!!.sku] ?: 0,
                        onConfirm = { location, qty ->
                            selectedItem!!.sku?.let { sku -> pickingResults[sku] = qty }
                            showBottomSheet = false
                            selectedItem = null
                        },
                        onCancel = {
                            showBottomSheet = false
                            selectedItem = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PickingHeader(cliente: String, fecha: String, estado: EstadoPedido) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Cliente", style = MaterialTheme.typography.labelMedium)
            Text(text = cliente, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = "Fecha", style = MaterialTheme.typography.labelMedium)
                    Text(text = fecha, style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Estado", style = MaterialTheme.typography.labelMedium)
                    Text(text = estado.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun PickingProgressFooter(
    progress: Float,
    isComplete: Boolean,
    totalItems: Int,
    totalPicked: Int,
    onFinishClick: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Progreso del Picking", style = MaterialTheme.typography.labelMedium)
                Text(text = "$totalPicked / $totalItems unidades", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onFinishClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = isComplete,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isComplete) "TERMINAR PICKING" else "FALTA COMPLETAR PICKING")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingItemCard(
    item: ItemPicking, 
    pickedQuantity: Int = 0,
    onClick: () -> Unit
) {
    val isFullyPicked = pickedQuantity >= item.displayCantidad && item.displayCantidad > 0
    val cardColor = if (isFullyPicked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.displayDescripcion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (pickedQuantity > 0) {
                    Badge(
                        containerColor = if (isFullyPicked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            text = "$pickedQuantity / ${item.displayCantidad}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Text(
                            text = "0 / ${item.displayCantidad}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "SKU: ${item.sku ?: "N/A"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Place, 
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.displayUbicacion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PickingBottomSheetContent(
    item: ItemPicking,
    currentPicked: Int,
    onConfirm: (String, Int) -> Unit,
    onCancel: () -> Unit
) {
    var quantity by remember { mutableStateOf(if (currentPicked > 0) currentPicked.toString() else "") }
    var location by remember { mutableStateOf("") }
    var showScanner by remember { mutableStateOf(false) }

    // Función para transformar "Pasillo 1/Rack A1" -> "P1-A-01"
    fun transformLocation(input: String): String {
        val regex = Regex("""(?i)Pasillo\s+(\d+)\s*/\s*Rack\s+([A-Z])(\d+)""")
        val match = regex.find(input) ?: return input
        
        val pasillo = match.groupValues[1]
        val letter = match.groupValues[2].uppercase()
        val number = match.groupValues[3].padStart(2, '0')
        
        return "P$pasillo-$letter-$number"
    }

    if (showScanner) {
        Dialog(
            onDismissRequest = { showScanner = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                BarcodeScanner(
                    onBarcodeScanned = { scanned ->
                        location = transformLocation(scanned)
                        showScanner = false
                    },
                    onClose = { showScanner = false }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding() // Handle gesture nav bar
    ) {
        Text(
            text = "Pickear Producto",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = item.displayDescripcion,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "SKU: ${item.sku}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = location,
            onValueChange = { location = transformLocation(it) },
            label = { Text("Escanear Ubicación") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ej: P1-A-01") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { showScanner = true }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Escanear")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = quantity,
            onValueChange = { 
                if (it.all { char -> char.isDigit() }) {
                    quantity = it 
                }
            },
            label = { Text("Cantidad a Pickear (Solicitado: ${item.displayCantidad})") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val q = quantity.toIntOrNull() ?: 0
                    onConfirm(location, q)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Confirmar")
            }
        }
    }
}
