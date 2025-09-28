import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pneuma.fotomarwms_grupo5.navigation.Screen
import com.pneuma.fotomarwms_grupo5.viewmodels.MainViewModel
//import com.example.ejemplo2.navigation.Screen
//import com.example.ejemplo2.viewmodels.MainViewModel
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    navController: NavController,     // Controlador de navegación para moverse entre pantallas
    viewModel: MainViewModel          // ViewModel que centraliza la navegación (eventos)
) {
    // Estructura visual centralizada
    Column(
        modifier = Modifier
            .fillMaxSize()             // Ocupar todo el alto disponible
            .padding(all = 16.dp),     // Margen interno general
        verticalArrangement = Arrangement.Center,  // Centrar elementos verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centrar elementos horizontalmente
    ) {
        // Título o texto principal
        Text(text = "Pantalla de Configuración (Settings)")

        Spacer(modifier = Modifier.height(24.dp)) // Espacio vertical

        // Botón para volver al Home
        Button(
            onClick = {
                viewModel.navigateTo(Screen.Home) // Emitir evento de navegación al ViewModel
            }
        ) {
            Text(text = "Volver al Inicio")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Más espacio

        // Botón para ir al Perfil
        Button(
            onClick = {
                viewModel.navigateTo(Screen.Profile) // Emitir evento para ir a perfil
            }
        ) {
            Text(text = "Ir a Perfil")
        }
    }
}