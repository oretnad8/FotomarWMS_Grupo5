package com.pneuma.fotomarwms_grupo5.data.local.dao

import androidx.room.*
import com.pneuma.fotomarwms_grupo5.data.local.entities.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Upsert
    suspend fun upsertProductos(productos: List<ProductoEntity>)

    @Query("SELECT * FROM productos ORDER BY sku ASC")
    fun getAllProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE sku = :sku LIMIT 1")
    suspend fun getProductoBySku(sku: String): ProductoEntity?

    // Búsqueda simple (mejora según necesites)
    @Query("SELECT * FROM productos WHERE sku LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%'")
    fun searchProductos(query: String): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE needsSync = 1")
    suspend fun getProductosPendientes(): List<ProductoEntity>

    @Query("UPDATE productos SET needsSync = 0 WHERE sku IN (:skus)")
    suspend fun marcarProductosComoSincronizados(skus: List<String>)

    @Query("DELETE FROM productos")
    suspend fun deleteAll()
}