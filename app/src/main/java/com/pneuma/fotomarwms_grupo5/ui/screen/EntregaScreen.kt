package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.BackTopBar
import com.pneuma.fotomarwms_grupo5.viewmodels.PedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntregaScreen(
    pedidoId: Int,
    viewModel: PedidosViewModel,
    onNavigateBack: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Iniciar Ruta, 2: Finalizar
    var transportistaNombre by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Entrega Pedido #${pedidoId}",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (step == 1) {
                Text(
                    text = "¿Iniciar ruta de entrega?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { step = 2 },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Iniciar Ruta")
                }
            } else {
                Text(
                    text = "Finalizar Entrega",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = transportistaNombre,
                    onValueChange = { transportistaNombre = it },
                    label = { Text("Nombre del Transportista") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { /* Abrir Cámara */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Capturar Foto Evidencia")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        viewModel.finalizarEntrega(pedidoId, transportistaNombre, "base64_simulado")
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = transportistaNombre.isNotBlank()
                ) {
                    Text("Finalizar y Subir")
                }
            }
        }
    }
}
