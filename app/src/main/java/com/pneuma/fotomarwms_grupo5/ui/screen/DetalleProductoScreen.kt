package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pneuma.fotomarwms_grupo5.model.Producto
import com.pneuma.fotomarwms_grupo5.model.ProductoUbicacion
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla de Detalle de un Producto específico
 *
 * Muestra:
 * - Información completa del producto
 * - Stock y ubicaciones
 * - Códigos de barras y LPN
 * - Fecha de vencimiento si aplica
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    sku: String
) {
    // TODO: Obtener producto desde ViewModel basado en SKU
    // Por ahora usamos datos de prueba
    val producto = remember {
        Producto(
            sku = sku,
            descripcion = "Canon EOS R5 - Cámara Mirrorless Full Frame 45MP",
            stock = 15,
            codigoBarrasIndividual = "1234567890123",
            lpn = "LPN-CAM-001",
            lpnDesc = "Caja de cámaras Canon",
            ubicaciones = listOf(
                ProductoUbicacion(1, "A13", 10),
                ProductoUbicacion(2, "B05", 5)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Producto") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SKU y Stock
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = producto.sku,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = "Stock",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stock: ${producto.stock} unidades",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Descripción
            InfoCard(
                titulo = "Descripción",
                icono = Icons.Default.Description
            ) {
                Text(text = producto.descripcion, fontSize = 14.sp)
            }

            // Códigos
            InfoCard(
                titulo = "Códigos",
                icono = Icons.Default.QrCode
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (producto.codigoBarrasIndividual != null) {
                        InfoRow(
                            label = "Código de Barras:",
                            value = producto.codigoBarrasIndividual
                        )
                    }
                    if (producto.lpn != null) {
                        InfoRow(label = "LPN:", value = producto.lpn)
                    }
                    if (producto.lpnDesc != null) {
                        InfoRow(label = "Descripción LPN:", value = producto.lpnDesc)
                    }
                }
            }

            // Ubicaciones
            InfoCard(
                titulo = "Ubicaciones",
                icono = Icons.Default.LocationOn
            ) {
                if (producto.ubicaciones.isEmpty()) {
                    Text(
                        text = "Sin ubicaciones asignadas",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        producto.ubicaciones.forEach { ubicacion ->
                            UbicacionItem(ubicacion)
                        }
                    }
                }
            }

            // Vencimiento si aplica
            if (producto.fechaVencimiento != null) {
                InfoCard(
                    titulo = "Fecha de Vencimiento",
                    icono = Icons.Default.CalendarToday
                ) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    Text(
                        text = dateFormat.format(producto.fechaVencimiento),
                        fontSize = 14.sp
                    )
                    if (producto.vencimientoCercano) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFFF9C4),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alerta",
                                tint = Color(0xFFF57C00)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Vencimiento próximo (menos de 2 meses)",
                                fontSize = 13.sp,
                                color = Color(0xFFF57C00)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card de información reutilizable
 */
@Composable
fun InfoCard(
    titulo: String,
    icono: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * Fila de información label-value
 */
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Item de ubicación con cantidad
 */
@Composable
fun UbicacionItem(ubicacion: ProductoUbicacion) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Ubicación",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = ubicacion.codigoUbicacion,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "${ubicacion.cantidadEnUbicacion} uds",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}