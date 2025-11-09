package com.pneuma.fotomarwms_grupo5.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// DAOS
import com.pneuma.fotomarwms_grupo5.db.daos.*

// ENTITIES
import com.pneuma.fotomarwms_grupo5.db.entities.*

@Database(
    entities = [
        ProductoLocal::class,
        UbicacionLocal::class,
        SolicitudMovimientoLocal::class,
        AprobacionLocal::class,
        UsuarioLocal::class,
        ConteoLocal::class,
        MensajeLocal::class,
        AsignacionUbicacionLocal::class
    ],
    version = 6, // Incrementado por los cambios
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun productoDao(): ProductoDao
    abstract fun ubicacionDao(): UbicacionDao
    abstract fun solicitudMovimientoDao(): SolicitudMovimientoDao
    abstract fun aprobacionDao(): AprobacionDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun conteoDao(): ConteoDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun asignacionUbicacionDao(): AsignacionUbicacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fotomar_wms_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
