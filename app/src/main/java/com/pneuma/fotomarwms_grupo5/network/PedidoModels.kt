package com.pneuma.fotomarwms_grupo5.network

import com.google.gson.annotations.SerializedName

/**
 * Estados posibles de un pedido
 */
enum class EstadoPedido {
    @SerializedName("PENDIENTE") PENDIENTE,
    @SerializedName("ACEPTADO") ACEPTADO,
    @SerializedName("EN_PICKING") EN_PICKING,
    @SerializedName("PICKING_COMPLETADO") PICKING_COMPLETADO,
    @SerializedName("VALIDADO") VALIDADO,
    @SerializedName("EN_TRANSPORTE") EN_TRANSPORTE,
    @SerializedName("ENTREGADO") ENTREGADO,
    @SerializedName("CANCELADO") CANCELADO
}

data class Pedido(
    val id: Int,
    @SerializedName("cliente") val clienteAlias: String? = null,
    @SerializedName("clienteNombre") val clienteNombre: String? = null,
    @SerializedName("nombreCliente") val nombreCliente: String? = null,
    @SerializedName("customerName") val customerName: String? = null,
    @SerializedName("nombre_cliente") val nombre_cliente: String? = null,
    @SerializedName("cliente_nombre") val cliente_nombre: String? = null,

    @SerializedName("fecha") val fechaAlias: String? = null,
    @SerializedName("fechaPedido") val fechaPedido: String? = null,
    @SerializedName("fechaCreacion") val fechaCreacion: String? = null, // Verified from backend
    @SerializedName("orderDate") val orderDate: String? = null,
    @SerializedName("order_date") val order_date: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("fecha_pedido") val fecha_pedido: String? = null,
    @SerializedName("date") val date: String? = null,

    val estado: EstadoPedido? = null,
    val operadorAsignadoId: Int? = null,
    val transportistaNombre: String? = null,
    val fotoEvidencia: String? = null,
    @SerializedName("detalles", alternate = ["items", "productos", "detalle_pedido"])
    val detalles: List<DetallePedido>? = null
) {
    val displayCliente: String
        get() = clienteNombre ?: clienteAlias ?: nombreCliente ?: customerName ?: nombre_cliente ?: cliente_nombre ?: "Cliente desconocido"

    // Computed property to get the valid date
    val fecha: String?
        get() = fechaCreacion ?: fechaPedido ?: orderDate ?: order_date ?: createdAt ?: fecha_pedido ?: fechaAlias ?: date
}


data class DetallePedido(
    val id: Int,
    val sku: String?,
    val descripcion: String?,
    val cantidadSolicitada: Int?,
    val cantidadPickeada: Int?,
    val ubicacionSugerida: String? = null
)

data class HojaPickingResponse(
    val pedidoId: Int,
    @SerializedName("clienteNombre") val clienteNombre: String? = null,
    @SerializedName("cliente") val cliente: String? = null,
    @SerializedName("nombreCliente") val nombreCliente: String? = null,
    @SerializedName("customerName") val customerName: String? = null,
    @SerializedName("nombre_cliente") val nombre_cliente: String? = null,
    @SerializedName("cliente_nombre") val cliente_nombre: String? = null,
    @SerializedName("clienteAlias") val clienteAlias: String? = null,
    
    @SerializedName("detalles", alternate = ["items", "productos", "detalle_pedido"])
    val items: List<ItemPicking>?
) {
    val displayCliente: String
        get() = clienteNombre ?: cliente ?: nombreCliente ?: customerName ?: nombre_cliente ?: cliente_nombre ?: clienteAlias ?: "Cliente desconocido"
}

data class ItemPicking(
    val sku: String?,
    @SerializedName("codigoBarras") val codigoBarras: String? = null,
    @SerializedName("barcode") val barcode: String? = null,
    @SerializedName("codigo_barras") val codigo_barras: String? = null,
    
    // Nombres / Descripción
    @SerializedName("descripcion") val descripcionAlias: String? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("nombreProducto") val nombreProducto: String? = null,
    @SerializedName("producto_nombre") val producto_nombre: String? = null,
    @SerializedName("descripcion_producto") val descripcion_producto: String? = null,

    // Cantidad
    @SerializedName("cantidad") val cantidadAlias: Int? = null,
    @SerializedName("cantidadSolicitada") val cantidadSolicitada: Int? = null,
    @SerializedName("cant") val cant: Int? = null,
    @SerializedName("cantidad_solicitada") val cantidad_solicitada: Int? = null,
    @SerializedName("cantidad_picking") val cantidad_picking: Int? = null,
    @SerializedName("qty") val qty: Int? = null,

    val pasillo: Int? = null,
    val estante: String? = null,

    // Ubicacion
    @SerializedName("codigoUbicacion") val codigoUbicacion: String? = null,
    @SerializedName("ubicacion") val ubicacion: String? = null,
    @SerializedName("posicion") val posicion: String? = null,
    @SerializedName("ubicacion_codigo") val ubicacion_codigo: String? = null,
    @SerializedName("posicion_bodega") val posicion_bodega: String? = null,
    
    // Nested backend structure for Picking
    val ubicacionesRecomendadas: List<UbicacionRecomendada>? = null,
    
    val producto: ItemProductoNested? = null
) {
    val displayDescripcion: String
        get() {
            // Priority 1: Direct description
            val directo = descripcionAlias ?: nombreProducto ?: nombre ?: producto_nombre ?: descripcion_producto
            if (!directo.isNullOrBlank()) return directo
            
            // Priority 2: Nested in Producto
            val anidado = producto?.descripcion ?: producto?.nombre
            if (!anidado.isNullOrBlank()) return anidado
            
            // Priority 3: Deeply nested in UbicacionesRecomendadas -> Productos
            val profundo = ubicacionesRecomendadas?.firstOrNull()?.productos?.firstOrNull()?.descripcion
            if (!profundo.isNullOrBlank()) return profundo
            
            return "Sin descripción (SKU: ${sku ?: "N/A"})"
        }

    val displayCantidad: Int
        get() = cantidadAlias ?: cantidadSolicitada ?: cant ?: cantidad_solicitada ?: cantidad_picking ?: qty ?: 0

    val displayUbicacion: String
        get() = ubicacionesRecomendadas?.firstOrNull()?.codigoUbicacion ?: "Sin ubicación asignada"
}

data class UbicacionRecomendada(
    val idUbicacion: Int?,
    val codigoUbicacion: String?,
    val pasillo: Int?,
    val piso: String?,
    val numero: Int?,
    val totalProductos: Int?,
    val cantidadTotal: Int?,
    val productos: List<ProductoEnUbicacion>?
)



data class ProductoResumen(
    val sku: String?,
    val descripcion: String?,
    val nombre: String?
)

data class ItemProductoNested(
    val sku: String?,
    val descripcion: String?,
    val nombre: String?
)


data class ConfirmarPickingRequest(
    val operadorId: Int,
    val detalles: List<DetallePickingCheck>
)

data class DetallePickingCheck(
    val sku: String,
    val cantidadPickeada: Int
)

data class FinalizarEntregaRequest(
    val transportistaNombre: String,
    val fotoEvidenciaBase64: String
)

data class MonitoreoResponse(
    val totales: Map<String, Int>
)
