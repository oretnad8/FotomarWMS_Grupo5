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
import com.pneuma.fotomarwms_grupo5.models.TipoMovimiento
import com.pneuma.fotomarwms_grupo5.models.UiState
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.*
import com.pneuma.fotomarwms_grupo5.viewmodels.AprobacionViewModel

/**
 * Pantalla de Solicitud de Movimiento
 * Solo para OPERADORES
 *
 * Tipos de movimiento:
 * - INGRESO: Nueva entrada de productos
 * - EGRESO: Salida de productos
 * - REUBICACION: Cambio de ubicación
 *
 * Todas las solicitudes requieren aprobación del Jefe/Supervisor
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudMovimientoScreen(
    aprobacionViewModel: AprobacionViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados
    val createState by aprobacionViewModel.createSolicitudState.collectAsStateWithLifecycle()

    // Estados del formulario
    var tipoMovimiento by remember { mutableStateOf<TipoMovimiento?>(null) }
    var sku by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var idUbicacionOrigen by remember { mutableStateOf("") }
    var idUbicacionDestino by remember { mutableStateOf("") }

    // Estados de validación
    var skuError by remember { mutableStateOf<String?>(null) }
    var cantidadError by remember { mutableStateOf<String?>(null) }
    var motivoError by remember { mutableStateOf<String?>(null) }
    var ubicacionError by remember { mutableStateOf<String?>(null) }

    // Estados de UI
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Manejar estados de creación
    LaunchedEffect(createState) {
        when (val state = createState) {
            is UiState.Success -> {
                showSuccessDialog = true
                // Limpiar formulario
                tipoMovimiento = null
                sku = ""
                cantidad = ""
                motivo = ""
                idUbicacionOrigen = ""
                idUbicacionDestino = ""
            }
            is UiState.Error -> {
                errorMessage = state.message
                showErrorDialog = true
            }
            else -> {}
        }
    }

    // Diálogos
    SuccessDialog(
        title = "¡Solicitud Enviada!",
        message = "Tu solicitud ha sido enviada al Jefe de Bodega para su aprobación.",
        onDismiss = {
            showSuccessDialog = false
            aprobacionViewModel.clearCreateState()
            onNavigateBack()
        },
        showDialog = showSuccessDialog
    )

    ErrorDialog(
        message = errorMessage,
        onDismiss = {
            showErrorDialog = false
            aprobacionViewModel.clearCreateState()
        },
        showDialog = showErrorDialog
    )

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Solicitar Movimiento",
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
                // Ingreso
                FilterChip(
                    text = "Ingreso",
                    selected = tipoMovimiento == TipoMovimiento.INGRESO,
                    onClick = { tipoMovimiento = TipoMovimiento.INGRESO },
                    modifier = Modifier.weight(1f)
                )

                // Egreso
                FilterChip(
                    text = "Egreso",
                    selected = tipoMovimiento == TipoMovimiento.EGRESO,
                    onClick = { tipoMovimiento = TipoMovimiento.EGRESO },
                    modifier = Modifier.weight(1f)
                )

                // Reubicación
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

                        // Campo SKU
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

                        // Campo Cantidad
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

                        // Campos específicos para REUBICACION
                        if (tipoMovimiento == TipoMovimiento.REUBICACION) {
                            NumberTextField(
                                value = idUbicacionOrigen,
                                onValueChange = {
                                    idUbicacionOrigen = it
                                    ubicacionError = null
                                },
                                label = "ID Ubicación Origen",
                                placeholder = "Ej: 12",
                                isError = ubicacionError != null,
                                errorMessage = ubicacionError,
                                leadingIcon = Icons.Default.LocationOn
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            NumberTextField(
                                value = idUbicacionDestino,
                                onValueChange = {
                                    idUbicacionDestino = it
                                    ubicacionError = null
                                },
                                label = "ID Ubicación Destino",
                                placeholder = "Ej: 25",
                                isError = ubicacionError != null,
                                leadingIcon = Icons.Default.LocationOn
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Campo Motivo
                        MultilineTextField(
                            value = motivo,
                            onValueChange = {
                                motivo = it
                                motivoError = null
                            },
                            label = "Motivo de la Solicitud",
                            placeholder = "Describe el motivo del movimiento...",
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

                // ========== INFORMACIÓN ==========
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            text = "Tu solicitud será enviada al Jefe de Bodega para su revisión y aprobación.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ========== BOTÓN ENVIAR ==========
                PrimaryButton(
                    text = "Enviar Solicitud",
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

                        if (tipoMovimiento == TipoMovimiento.REUBICACION) {
                            if (idUbicacionOrigen.isBlank() || idUbicacionDestino.isBlank()) {
                                ubicacionError = "Las ubicaciones son obligatorias"
                                isValid = false
                            }
                        }

                        // Si es válido, enviar solicitud
                        if (isValid) {
                            when (tipoMovimiento) {
                                TipoMovimiento.INGRESO -> {
                                    aprobacionViewModel.createSolicitudIngreso(
                                        sku = sku,
                                        cantidad = cantidad.toInt(),
                                        motivo = motivo
                                    )
                                }
                                TipoMovimiento.EGRESO -> {
                                    aprobacionViewModel.createSolicitudEgreso(
                                        sku = sku,
                                        cantidad = cantidad.toInt(),
                                        motivo = motivo
                                    )
                                }
                                TipoMovimiento.REUBICACION -> {
                                    aprobacionViewModel.createSolicitudReubicacion(
                                        sku = sku,
                                        cantidad = cantidad.toInt(),
                                        motivo = motivo,
                                        idUbicacionOrigen = idUbicacionOrigen.toInt(),
                                        idUbicacionDestino = idUbicacionDestino.toInt()
                                    )
                                }
                                else -> {}
                            }
                        }
                    },
                    enabled = createState !is UiState.Loading,
                    icon = Icons.Default.Send
                )

                // Indicador de carga
                if (createState is UiState.Loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}