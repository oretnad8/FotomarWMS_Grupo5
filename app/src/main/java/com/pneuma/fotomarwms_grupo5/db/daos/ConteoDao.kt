package com.pneuma.fotomarwms_grupo5.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pneuma.fotomarwms_grupo5.db.entities.ConteoLocal
import kotlinx.coroutines.flow.Flow

@Dao // Indica a Room que esta interfaz es un DAO
interface ConteoDao {

    /**
     * Guarda un conteo pendiente en la base de datos local.
     * Devuelve el ID local autogenerado para el registro guardado.
     */
    @Insert
    suspend fun insertarConteoPendiente(conteo: ConteoLocal): Long

    /**
     * Borra un conteo pendiente de la BD local usando su ID.
     * Se llamará a esta función cuando el backend confirme que recibió el conteo.
     */
    @Query("DELETE FROM conteos_pendientes WHERE idLocal = :id")
    suspend fun borrarConteoPendientePorId(id: Long): Int // Devuelve el número de filas borradas (debería ser 1)

    /**
     * (Opcional) Obtiene una lista de todos los conteos pendientes guardados.
     * Esto es útil para implementar una lógica de reintentos más adelante.
     */
    @Query("SELECT * FROM conteos_pendientes ORDER BY timestamp ASC")
    fun obtenerConteosPendientes(): Flow<List<ConteoLocal>>
}