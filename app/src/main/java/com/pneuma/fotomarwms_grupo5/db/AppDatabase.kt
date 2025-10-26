package com.pneuma.fotomarwms_grupo5.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pneuma.fotomarwms_grupo5.db.daos.SolicitudMovimientoDao
import com.pneuma.fotomarwms_grupo5.db.entities.SolicitudMovimientoLocal


@Database(entities = [SolicitudMovimientoLocal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Room implementará esto para darnos el DAO
    abstract fun solicitudMovimientoDao(): SolicitudMovimientoDao

    // --- Singleton para tener una sola instancia de la BD ---
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fotomar_wms_db_simple" // Nombre archivo BD
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}