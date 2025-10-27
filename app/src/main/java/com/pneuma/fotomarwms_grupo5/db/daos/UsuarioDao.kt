package com.pneuma.fotomarwms_grupo5.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pneuma.fotomarwms_grupo5.db.entities.UsuarioLocal
import kotlinx.coroutines.flow.Flow 

@Dao // Indica a Room que esto es un DAO
interface UsuarioDao {

    /**
     * Guarda un usuario pendiente de creación en la BD local.
     * Devuelve el ID local autogenerado.
     */
    @Insert
    suspend fun insertarUsuarioPendiente(usuario: UsuarioLocal): Long

    /**
     * Borra un usuario pendiente de la BD local usando su ID local.
     * Se llamará cuando el backend confirme la creación (200 OK).
     */
    @Query("DELETE FROM usuarios_pendientes_creacion WHERE idLocal = :id")
    suspend fun borrarUsuarioPendientePorId(id: Long): Int // Devuelve 1 si borró

    /**
     * (Opcional) Obtiene una lista de todos los usuarios pendientes guardados.
     * Útil para verlos o implementar reintentos.
     * Flow permite observar cambios en la lista automáticamente.
     */
    @Query("SELECT * FROM usuarios_pendientes_creacion ORDER BY timestamp ASC")
    fun obtenerUsuariosPendientes(): Flow<List<UsuarioLocal>>
}