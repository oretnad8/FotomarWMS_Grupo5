package com.pneuma.fotomarwms_grupo5.viewmodels
import com.pneuma.fotomarwms_grupo5.models.Producto
import com.pneuma.fotomarwms_grupo5.models.UiState
import com.pneuma.fotomarwms_grupo5.repository.ProductoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<ProductoRepository>()
    private lateinit var viewModel: ProductoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = ProductoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchProductos encuentra productos y actualiza el estado`() = runTest {
        // 1. GIVEN: Lista falsa con la estructura CORRECTA de tu Producto.kt
        val listaFalsa = listOf(
            Producto(
                sku = "CAM-01",
                descripcion = "Cámara Canon",
                stock = 5,
                codigoBarrasIndividual = "123456",
                lpn = null,
                lpnDesc = null,
                fechaVencimiento = null
            ),
            Producto(
                sku = "LEN-02",
                descripcion = "Lente Sony",
                stock = 3,
                codigoBarrasIndividual = "987654",
                lpn = null,
                lpnDesc = null,
                fechaVencimiento = null
            )
        )

        // Mock del repositorio
        coEvery { repository.searchProductos("Cámara") } returns Result.success(listaFalsa)

        // 2. WHEN: Buscamos
        viewModel.searchProductos("Cámara")
        advanceUntilIdle()

        // 3. THEN: Verificaciones
        val estado = viewModel.searchState.value

        assertTrue(estado is UiState.Success)
        assertEquals(2, (estado as UiState.Success).data.size)
        assertEquals("Cámara Canon", estado.data[0].descripcion)
    }
}

