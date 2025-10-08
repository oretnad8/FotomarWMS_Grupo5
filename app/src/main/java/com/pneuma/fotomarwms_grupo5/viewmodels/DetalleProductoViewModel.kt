package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import com.pneuma.fotomarwms_grupo5.model.Producto
import com.pneuma.fotomarwms_grupo5.model.ProductoUbicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

/**
 * Estado de la UI para el detalle del producto
 */
data class DetalleProductoUiState(
    val producto: Producto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel que gestiona el detalle de un producto específico
 *
 * Responsabilidades:
 * - Cargar información completa del producto por SKU
 * - Mostrar ubicaciones y stock por ubicación
 */
class DetalleProductoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleProductoUiState())
    val uiState: StateFlow<DetalleProductoUiState> = _uiState.asStateFlow()

    /**
     * Carga la información del producto desde el backend
     *
     * @param sku SKU del producto a cargar
     *
     * TODO: Conectar con API cuando esté disponible
     */
    fun cargarProducto(sku: String) {
        _uiState.value = DetalleProductoUiState(isLoading = true)

        // Simulación de carga desde backend
        // En producción: usar repository con llamada a API

        // Base de datos de prueba
        val productosPrueba = mapOf(
            "CA30001" to Producto(
                sku = "CA30001",
                descripcion = "Cámara Canon EOS R5",
                stock = 15,
                codigoBarrasIndividual = "8714574687520",
                lpn = "LPN-001",
                lpnDesc = "Caja 001 - Canon EOS",
                fechaVencimiento = null,
                vencimientoCercano = false,
                ubicaciones = listOf(
                    ProductoUbicacion(1, "A-12", 10),
                    ProductoUbicacion(2, "B-05", 5)
                )
            ),
            "CA30002" to Producto(
                sku = "CA30002",
                descripcion = "Cámara Sony A7 IV",
                stock = 8,
                codigoBarrasIndividual = "4548736132603",
                lpn = "LPN-002",
                vencimientoCercano = false,
                ubicaciones = listOf(
                    ProductoUbicacion(3, "A-15", 8)
                )
            ),
            "LE30001" to Producto(
                sku = "LE30001",
                descripcion = "Lente Canon RF 24-70mm f/2.8",
                stock = 5,
                codigoBarrasIndividual = "4549292187618",
                ubicaciones = listOf(
                    ProductoUbicacion(4, "C-20", 5)
                )
            ),
            "TR30001" to Producto(
                sku = "TR30001",
                descripcion = "Trípode Manfrotto 055",
                stock = 2,
                fechaVencimiento = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000), // 30 días
                vencimientoCercano = true,
                ubicaciones = listOf(
                    ProductoUbicacion(5, "B-30", 2)
                )
            )
        )

        val producto = productosPrueba[sku]

        _uiState.value = if (producto != null) {
            DetalleProductoUiState(
                producto = producto,
                isLoading = false,
                errorMessage = null
            )
        } else {
            DetalleProductoUiState(
                producto = null,
                isLoading = false,
                errorMessage = "Producto con SKU $sku no encontrado"
            )
        }
    }
}