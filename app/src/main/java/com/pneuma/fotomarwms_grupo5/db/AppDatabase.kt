package com.pneuma.fotomarwms_grupo5.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


//DAOS
import com.pneuma.fotomarwms_grupo5.db.daos.UsuarioDao
import com.pneuma.fotomarwms_grupo5.db.daos.SolicitudMovimientoDao
import com.pneuma.fotomarwms_grupo5.db.daos.ConteoDao
//Clases
import com.pneuma.fotomarwms_grupo5.db.entities.SolicitudMovimientoLocal
import com.pneuma.fotomarwms_grupo5.db.entities.UsuarioLocal
import com.pneuma.fotomarwms_grupo5.db.entities.ConteoLocal


@Database(
    entities = [

    SolicitudMovimientoLocal::class,
    UsuarioLocal::class,
    ConteoLocal::class

   ],

    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Room implementará esto para darnos el DAO
    abstract fun solicitudMovimientoDao(): SolicitudMovimientoDao
    abstract fun usuarioDao(): UsuarioDao

    abstract fun conteoDao(): ConteoDao

    // --- Singleton para tener una sola instancia de la BD ---
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fotomar_wms_db_simple" // Nombre archivo BD
                )
                //Para manejar el cambio de versiones BORRANDO datos viejos
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

}