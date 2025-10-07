package com.pneuma.fotomarwms_grupo5.viewmodels

import androidx.lifecycle.ViewModel
import com.pneuma.fotomarwms_grupo5.model.Producto
import com.pneuma.fotomarwms_grupo5.model.ProductoUbicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

/**
 * Estado de la UI para la pantalla de Búsqueda
 */
data class BusquedaUiState(
    val resultados: List<Producto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel que gestiona la lógica de búsqueda de productos
 *
 * Responsabilidades:
 * - Buscar productos por SKU, descripción o ubicación
 * - Procesar códigos escaneados
 * - Filtrar resultados
 */
class BusquedaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BusquedaUiState())
    val uiState: StateFlow<BusquedaUiState> = _uiState.asStateFlow()

    // Base de datos simulada de productos
    private val productosPrueba = listOf(
        Producto(
            sku = "AP30001",
            descripcion = "Canon EOS R5 - Cámara Mirrorless Full Frame",
            stock = 15,
            codigoBarrasIndividual = "1234567890123",
            lpn = "LPN-CAM-001",
            ubicaciones = listOf(
                ProductoUbicacion(1, "A13", 10),
                ProductoUbicacion(2, "B05", 5)
            )
        ),
        Producto(
            sku = "AP30002",
            descripcion = "Sony A7 IV - Cámara Profesional",
            stock = 8,
            codigoBarrasIndividual = "1234567890124",
            ubicaciones = listOf(
                ProductoUbicacion(3, "A15", 8)
            )
        ),
        Producto(
            sku = "LN20001",
            descripcion = "Lente Sony FE 24-70mm f/2.8 GM II",
            stock = 5,
            codigoBarrasIndividual = "2345678901234",
            lpn = "LPN-LENS-001",
            ubicaciones = listOf(
                ProductoUbicacion(4, "B25", 5)
            )
        ),
        Producto(
            sku = "TR10001",
            descripcion = "Trípode Manfrotto Befree Advanced",
            stock = 3,
            codigoBarrasIndividual = "3456789012345",
            ubicaciones = listOf(
                ProductoUbicacion(5, "C10", 3)
            ),
            vencimientoCercano = false
        ),
        Producto(
            sku = "FL15001",
            descripcion = "Flash Godox V1 para Canon",
            stock = 0,
            codigoBarrasIndividual = "4567890123456",
            ubicaciones = emptyList()
        ),
        Producto(
            sku = "BA05001",
            descripcion = "Batería Canon LP-E6NH",
            stock = 2,
            fechaVencimiento = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000), // 30 días
            vencimientoCercano = true,
            ubicaciones = listOf(
                ProductoUbicacion(6, "A02", 2)
            )
        )
    )

    /**
     * Busca productos por texto (SKU, descripción o ubicación)
     *
     * @param query Texto de búsqueda
     */
    fun buscarProductos(query: String) {
        if (query.length < 2) {
            _uiState.value = BusquedaUiState(resultados = emptyList())
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        // TODO: Llamar al endpoint de búsqueda

        // Simulación de búsqueda local
        val resultados = productosPrueba.filter { producto ->
            producto.sku.contains(query, ignoreCase = true) ||
                    producto.descripcion.contains(query, ignoreCase = true) ||
                    producto.ubicaciones.any { it.codigoUbicacion.contains(query, ignoreCase = true) }
        }

        _uiState.value = BusquedaUiState(
            resultados = resultados,
            isLoading = false
        )
    }

    /**
     * Busca un producto por su código de barras escaneado
     *
     * @param codigo Código de barras escaneado
     */
    fun buscarPorCodigo(codigo: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        // TODO: Llamar al endpoint de búsqueda por código

        // Simulación de búsqueda por código
        val resultado = productosPrueba.find { producto ->
            producto.codigoBarrasIndividual == codigo ||
                    producto.lpn == codigo ||
                    producto.sku == codigo
        }

        _uiState.value = if (resultado != null) {
            BusquedaUiState(
                resultados = listOf(resultado),
                isLoading = false
            )
        } else {
            BusquedaUiState(
                resultados = emptyList(),
                isLoading = false,
                errorMessage = "No se encontró el producto con código: $codigo"
            )
        }
    }

    /**
     * Limpia los resultados de búsqueda
     */
    fun limpiarResultados() {
        _uiState.value = BusquedaUiState()
    }
}