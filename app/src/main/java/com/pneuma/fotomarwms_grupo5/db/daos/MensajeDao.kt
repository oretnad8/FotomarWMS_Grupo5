package com.pneuma.fotomarwms_grupo5.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pneuma.fotomarwms_grupo5.db.entities.MensajeLocal
import kotlinx.coroutines.flow.Flow

@Dao // Anotación que identifica esta interfaz como un DAO para Room
interface MensajeDao {

    /**
     * Guarda un mensaje pendiente de envío en la base de datos local.
     * Devuelve el ID local autogenerado.
     */
    @Insert
    suspend fun insertarMensajePendiente(mensaje: MensajeLocal): Long

    /**
     * Borra un mensaje pendiente de la BD local usando su ID.
     * Se usará cuando el backend confirme que el mensaje fue enviado.
     */
    @Query("DELETE FROM mensajes_pendientes WHERE idLocal = :id")
    suspend fun borrarMensajePendientePorId(id: Long): Int // Devuelve el número de filas borradas

    /**
     * (Opcional) Obtiene una lista de todos los mensajes pendientes de envío.
     * Útil para una futura implementación de reintentos automáticos.
     */
    @Query("SELECT * FROM mensajes_pendientes ORDER BY timestamp ASC")
    fun obtenerMensajesPendientes(): Flow<List<MensajeLocal>>
}