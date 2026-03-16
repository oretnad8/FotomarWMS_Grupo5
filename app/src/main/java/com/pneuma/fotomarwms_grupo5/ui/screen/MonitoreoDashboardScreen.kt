package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pneuma.fotomarwms_grupo5.ui.screen.componentes.BackTopBar
import com.pneuma.fotomarwms_grupo5.viewmodels.PedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoreoDashboardScreen(
    viewModel: PedidosViewModel,
    onNavigateBack: () -> Unit
) {
    val monitoreoData by viewModel.monitoreo.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMonitoreo()
    }

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Monitoreo de Notas de Venta",
                onBackClick = onNavigateBack
            )

        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (monitoreoData == null) {
                Text("Cargando estadísticas...", modifier = Modifier.align(Alignment.Center))
            } else {
                val stats = monitoreoData!!.totales.toList()
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(stats) { (estado, count) ->
                        StatCard(estado = estado, count = count)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(estado: String, count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = estado.replace("_", " "),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
