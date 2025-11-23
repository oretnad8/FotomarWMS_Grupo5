package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pneuma.fotomarwms_grupo5.models.TipoMovimiento
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*

/**
 * Pantalla de Registro Directo de Movimientos
 * Solo para JEFE y SUPERVISOR
 *
 * A diferencia de las solicitudes, estos movimientos NO requieren aprobación
 * y se ejecutan inmediatamente en el sistema
 *
 * Tipos de movimiento:
 * - INGRESO: Nueva entrada de productos
 * - EGRESO: Salida de productos
 * - REUBICACION: Cambio de ubicación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroDirectoScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados del formulario
    var tipoMovimiento by rememberSaveable { mutableStateOf<TipoMovimiento?>(null) }
    var sku by rememberSaveable { mutableStateOf("") }
    var cantidad by rememberSaveable { mutableStateOf("") }
    var motivo by rememberSaveable { mutableStateOf("") }
    var idUbicacionOrigen by rememberSaveable { mutableStateOf("") }
    var idUbicacionDestino by rememberSaveable { mutableStateOf("") }
    var ubicacionIngreso by rememberSaveable { mutableStateOf("") }
    var ubicacionEgreso by rememberSaveable { mutableStateOf("") }

    // Estados de validación
    var skuError by remember { mutableStateOf<String?>(null) }
    var cantidadError by remember { mutableStateOf<String?>(null) }
    var motivoError by remember { mutableStateOf<String?>(null) }
    var ubicacionError by remember { mutableStateOf<String?>(null) }

    // Estados de UI
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmación
    ConfirmDialog(
        title = "Confirmar Registro",
        message = "¿Estás seguro de registrar este movimiento? Esta acción se ejecutará inmediatamente sin necesidad de aprobación.",
        onConfirm = {
            showConfirmDialog = false
            // TODO: Ejecutar registro directo
            showSuccessDialog = true
        },
        onDismiss = { showConfirmDialog = false },
        showDialog = showConfirmDialog,
        confirmText = "Registrar",
        dismissText = "Cancelar"
    )

    // Diálogo de éxito
    SuccessDialog(
        title = "¡Movimiento Registrado!",
        message = "El movimiento ha sido registrado exitosamente en el sistema.",
        onDismiss = {
            showSuccessDialog = false
            // Limpiar formulario
            tipoMovimiento = null
            sku = ""
            cantidad = ""
            motivo = ""
            idUbicacionOrigen = ""
            idUbicacionDestino = ""
            ubicacionIngreso = ""
            ubicacionEgreso = ""
        },
        showDialog = showSuccessDialog
    )

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Registro Directo",
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
            // ========== ALERTA DE PRIVILEGIO ==========
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Registro sin Aprobación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2)
                        )
                        Text(
                            text = "Como Jefe de Bodega, puedes registrar movimientos que se ejecutarán inmediatamente sin necesidad de aprobación.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // ========== SELECCIÓN DE TIPO DE MOVIMIENTO ==========
            Text(
                text = "Tipo de Movimiento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    text = "Ingreso",
                    selected = tipoMovimiento == TipoMovimiento.INGRESO,
                    onClick = { tipoMovimiento = TipoMovimiento.INGRESO },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    text = "Egreso",
                    selected = tipoMovimiento == TipoMovimiento.EGRESO,
                    onClick = { tipoMovimiento = TipoMovimiento.EGRESO },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    text = "Reubicación",
                    selected = tipoMovimiento == TipoMovimiento.REUBICACION,
                    onClick = { tipoMovimiento = TipoMovimiento.REUBICACION },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========== FORMULARIO ==========
            if (tipoMovimiento != null) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Datos del Movimiento",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        AppTextField(
                            value = sku,
                            onValueChange = {
                                sku = it.uppercase()
                                skuError = null
                            },
                            label = "SKU del Producto",
                            placeholder = "Ej: AP30001",
                            leadingIcon = Icons.Default.Inventory,
                            isError = skuError != null,
                            errorMessage = skuError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        NumberTextField(
                            value = cantidad,
                            onValueChange = {
                                cantidad = it
                                cantidadError = null
                            },
                            label = "Cantidad",
                            placeholder = "0",
                            isError = cantidadError != null,
                            errorMessage = cantidadError,
                            leadingIcon = Icons.Default.Numbers
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== REUBICACIÓN ==========
                        if (tipoMovimiento == TipoMovimiento.REUBICACION) {
                            Text(
                                text = "Ubicaciones",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Ubicación Origen
                            OutlinedTextField(
                                value = idUbicacionOrigen,
                                onValueChange = {
                                    idUbicacionOrigen = it
                                    ubicacionError = null
                                },
                                label = { Text("Ubicación Origen") },
                                placeholder = { Text("Ej: P1-A-01") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = ubicacionError != null
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ScanUbicacionButton(
                                onUbicacionScanned = { ubicacion ->
                                    idUbicacionOrigen = ubicacion
                                    ubicacionError = null
                                },
                                label = "Escanear Ubicación Origen",
                                isFullWidth = true
                            )

                            if (ubicacionError != null) {
                                Text(
                                    text = ubicacionError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                                )
                            }

                            // Ubicación Destino
                            OutlinedTextField(
                                value = idUbicacionDestino,
                                onValueChange = {
                                    idUbicacionDestino = it
                                    ubicacionError = null
                                },
                                label = { Text("Ubicación Destino") },
                                placeholder = { Text("Ej: P2-B-05") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = ubicacionError != null
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ScanUbicacionButton(
                                onUbicacionScanned = { ubicacion ->
                                    idUbicacionDestino = ubicacion
                                    ubicacionError = null
                                },
                                label = "Escanear Ubicación Destino",
                                isFullWidth = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // ========== INGRESO ==========
                        if (tipoMovimiento == TipoMovimiento.INGRESO) {
                            Text(
                                text = "Ubicación de Ingreso",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = ubicacionIngreso,
                                onValueChange = {
                                    ubicacionIngreso = it
                                    ubicacionError = null
                                },
                                label = { Text("Ubicación") },
                                placeholder = { Text("Ej: P1-A-01") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = ubicacionError != null
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ScanUbicacionButton(
                                onUbicacionScanned = { ubicacion ->
                                    ubicacionIngreso = ubicacion
                                    ubicacionError = null
                                },
                                label = "Escanear Ubicación de Ingreso",
                                isFullWidth = true
                            )

                            if (ubicacionError != null) {
                                Text(
                                    text = ubicacionError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // ========== EGRESO ==========
                        if (tipoMovimiento == TipoMovimiento.EGRESO) {
                            Text(
                                text = "Ubicación de Egreso",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = ubicacionEgreso,
                                onValueChange = {
                                    ubicacionEgreso = it
                                    ubicacionError = null
                                },
                                label = { Text("Ubicación") },
                                placeholder = { Text("Ej: P1-A-01") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = ubicacionError != null
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ScanUbicacionButton(
                                onUbicacionScanned = { ubicacion ->
                                    ubicacionEgreso = ubicacion
                                    ubicacionError = null
                                },
                                label = "Escanear Ubicación de Egreso",
                                isFullWidth = true
                            )

                            if (ubicacionError != null) {
                                Text(
                                    text = ubicacionError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        MultilineTextField(
                            value = motivo,
                            onValueChange = {
                                motivo = it
                                motivoError = null
                            },
                            label = "Motivo del Movimiento",
                            placeholder = "Describe el motivo...",
                            minLines = 3,
                            maxLines = 5
                        )

                        if (motivoError != null) {
                            Text(
                                text = motivoError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ========== BOTÓN REGISTRAR ==========
                PrimaryButton(
                    text = "Registrar Movimiento",
                    onClick = {
                        // Validar formulario
                        var isValid = true

                        if (sku.isBlank()) {
                            skuError = "El SKU es obligatorio"
                            isValid = false
                        }

                        if (cantidad.isBlank() || cantidad.toIntOrNull() == null || cantidad.toInt() <= 0) {
                            cantidadError = "Ingresa una cantidad válida"
                            isValid = false
                        }

                        if (motivo.isBlank()) {
                            motivoError = "El motivo es obligatorio"
                            isValid = false
                        }

                        when (tipoMovimiento) {
                            TipoMovimiento.REUBICACION -> {
                                if (idUbicacionOrigen.isBlank() || idUbicacionDestino.isBlank()) {
                                    ubicacionError = "Las ubicaciones son obligatorias"
                                    isValid = false
                                }
                            }
                            TipoMovimiento.INGRESO -> {
                                if (ubicacionIngreso.isBlank()) {
                                    ubicacionError = "La ubicación de ingreso es obligatoria"
                                    isValid = false
                                }
                            }
                            TipoMovimiento.EGRESO -> {
                                if (ubicacionEgreso.isBlank()) {
                                    ubicacionError = "La ubicación de egreso es obligatoria"
                                    isValid = false
                                }
                            }
                            else -> {}
                        }

                        if (isValid) {
                            showConfirmDialog = true
                        }
                    },
                    icon = Icons.Default.SaveAlt
                )
            }
        }
    }
}
