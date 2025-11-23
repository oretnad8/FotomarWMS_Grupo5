package com.pneuma.fotomarwms_grupo5.ui.screen.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pneuma.fotomarwms_grupo5.models.UbicacionFormatter

/**
 * Botón para escanear código de ubicación
 * Reutilizable en múltiples screens
 *
 * @param onUbicacionScanned Callback cuando se detecta una ubicación válida
 * @param label Etiqueta del botón
 * @param modifier Modificador de composable
 */
@Composable
fun ScanUbicacionButton(
    onUbicacionScanned: (String) -> Unit,
    label: String = "Escanear Ubicación",
    modifier: Modifier = Modifier
) {
    var showBarcodeScanner by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    OutlinedButton(
        onClick = { showBarcodeScanner = true },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }

    // Mostrar error si es necesario
    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Escáner de código de barras
    if (showBarcodeScanner) {
        BarcodeScanner(
            onBarcodeScanned = { scannedCode ->
                // Parsear el código escaneado (formato P1/A1) a formato estándar (P1-A-01)
                val parsedCode = UbicacionFormatter.parseScannedCode(scannedCode)

                if (parsedCode != null) {
                    onUbicacionScanned(parsedCode)
                    showBarcodeScanner = false
                    showError = false
                } else {
                    showError = true
                    errorMessage = "Código de ubicación inválido. Formato esperado: P1/A1"
                    showBarcodeScanner = false
                }
            },
            onClose = {
                showBarcodeScanner = false
            }
        )
    }
}
