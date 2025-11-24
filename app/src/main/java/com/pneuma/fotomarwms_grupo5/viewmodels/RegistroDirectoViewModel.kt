package com.pneuma.fotomarwms_grupo5.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pneuma.fotomarwms_grupo5.models.*
import com.pneuma.fotomarwms_grupo5.network.RetrofitClient
import com.pneuma.fotomarwms_grupo5.network.AsignarUbicacionRequest
import com.pneuma.fotomarwms_grupo5.services.UbicacionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para Registro Directo de Movimientos
 * Maneja operaciones que se ejecutan inmediatamente sin necesidad de aprobación
 * Solo para JEFE y SUPERVISOR
 */
class RegistroDirectoViewModel(application: Application) : AndroidViewModel(application) {

    private val ubicacionesService = RetrofitClient.ubicacionesService
    private val ubicacionService = UbicacionService(application)

    // ========== ESTADOS ==========

    private val _registroState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val registroState: StateFlow<UiState<Boolean>> = _registroState.asStateFlow()

    // ========== OPERACIONES DIRECTAS ==========

    /**
     * Registra un INGRESO directo
     * Asigna el producto a la ubicación destino inmediatamente
     */
    fun registrarIngreso(sku: String, cantidad: Int, ubicacionDestino: String, motivo: String) {
        viewModelScope.launch {
            try {
                _registroState.value = UiState.Loading

                // Convertir código de ubicación a ID
                val idDestino = ubicacionService.getIdUbicacionByCodigo(ubicacionDestino)

                if (idDestino == null) {
                    _registroState.value = UiState.Error(
                        message = "No se pudo obtener el ID de la ubicacion. Verifica que el codigo sea valido."
                    )
                    return@launch
                }

                // Asignar producto a ubicación
                val asignarRequest = AsignarUbicacionRequest(
                    sku = sku,
                    codigoUbicacion = ubicacionDestino,
                    cantidad = cantidad
                )
                val asignarResponse = ubicacionesService.asignarProducto(asignarRequest)

                if (asignarResponse.isSuccessful) {
                    _registroState.value = UiState.Success(true)
                } else {
                    _registroState.value = UiState.Error(
                        message = "Error al registrar ingreso: ${asignarResponse.code()}"
                    )
                }

            } catch (e: Exception) {
                _registroState.value = UiState.Error(
                    message = "Error al registrar ingreso: ${e.message}"
                )
            }
        }
    }

    /**
     * Registra un EGRESO directo
     * Reduce el stock del producto en la ubicación origen
     * TODO: Implementar en el backend
     */
    fun registrarEgreso(sku: String, cantidad: Int, ubicacionOrigen: String, motivo: String) {
        viewModelScope.launch {
            try {
                _registroState.value = UiState.Loading

                // Convertir código de ubicación a ID
                val idOrigen = ubicacionService.getIdUbicacionByCodigo(ubicacionOrigen)

                if (idOrigen == null) {
                    _registroState.value = UiState.Error(
                        message = "No se pudo obtener el ID de la ubicacion. Verifica que el codigo sea valido."
                    )
                    return@launch
                }

                // TODO: Llamar al endpoint de egreso del backend
                // Por ahora, simulamos éxito
                _registroState.value = UiState.Success(true)

            } catch (e: Exception) {
                _registroState.value = UiState.Error(
                    message = "Error al registrar egreso: ${e.message}"
                )
            }
        }
    }

    /**
     * Registra una REUBICACION directa
     * Mueve el producto de una ubicación a otra
     * TODO: Implementar en el backend
     */
    fun registrarReubicacion(sku: String, cantidad: Int, ubicacionOrigen: String, ubicacionDestino: String, motivo: String) {
        viewModelScope.launch {
            try {
                _registroState.value = UiState.Loading

                // Convertir códigos de ubicación a IDs
                val idOrigen = ubicacionService.getIdUbicacionByCodigo(ubicacionOrigen)
                val idDestino = ubicacionService.getIdUbicacionByCodigo(ubicacionDestino)

                if (idOrigen == null || idDestino == null) {
                    _registroState.value = UiState.Error(
                        message = "No se pudo obtener los IDs de las ubicaciones. Verifica que los codigos sean validos."
                    )
                    return@launch
                }

                // Asignar producto a ubicación destino
                val asignarRequest = AsignarUbicacionRequest(
                    sku = sku,
                    codigoUbicacion = ubicacionDestino,
                    cantidad = cantidad
                )
                val asignarResponse = ubicacionesService.asignarProducto(asignarRequest)

                if (asignarResponse.isSuccessful) {
                    _registroState.value = UiState.Success(true)
                } else {
                    _registroState.value = UiState.Error(
                        message = "Error al registrar reubicacion: ${asignarResponse.code()}"
                    )
                }

            } catch (e: Exception) {
                _registroState.value = UiState.Error(
                    message = "Error al registrar reubicacion: ${e.message}"
                )
            }
        }
    }

    // ========== UTILIDADES ==========

    fun clearState() {
        _registroState.value = UiState.Idle
    }
}
