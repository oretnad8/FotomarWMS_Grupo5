package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pneuma.fotomarwms_grupo5.repository.ProductoRepository
import com.pneuma.fotomarwms_grupo5.repository.UbicacionRepository

/**
 * Factory para crear ViewModels con inyección de dependencias
 * Permite pasar repositorios a los ViewModels
 */
class ViewModelFactory(
    private val application: Application,
    private val productoRepository: ProductoRepository? = null,
    private val ubicacionRepository: UbicacionRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductoViewModel::class.java) -> {
                require(productoRepository != null) { "ProductoRepository requerido para ProductoViewModel" }
                ProductoViewModel(productoRepository) as T
            }
            modelClass.isAssignableFrom(UbicacionViewModel::class.java) -> {
                require(ubicacionRepository != null) { "UbicacionRepository requerido para UbicacionViewModel" }
                UbicacionViewModel(application, ubicacionRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
