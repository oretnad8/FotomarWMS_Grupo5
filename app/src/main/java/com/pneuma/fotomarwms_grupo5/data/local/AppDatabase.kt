package com.pneuma.fotomarwms_grupo5.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pneuma.fotomarwms_grupo5.data.local.dao.*
import com.pneuma.fotomarwms_grupo5.data.local.entities.*
import com.pneuma.fotomarwms_grupo5.data.local.utils.Converters // Necesitarás crear TypeConverters

@Database(
    entities = [
        UsuarioEntity::class,
        ProductoEntity::class,
        UbicacionEntity::class,
        AprobacionEntity::class,
        MensajeEntity::class,
        ConteoEntity::class
        // Añade aquí futuras entidades
    ],
    version = 1, // Incrementa si cambias el esquema
    exportSchema = false // Puedes ponerlo a true si quieres exportar el esquema
)
@TypeConverters(Converters::class) // Para tipos complejos como Enum o Date
abstract class AppDatabase : RoomDatabase() {

    // DAOs abstractos
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun ubicacionDao(): UbicacionDao
    abstract fun aprobacionDao(): AprobacionDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun conteoDao(): ConteoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fotomarwms" // Nombre del archivo de la base de datos
                )
                    // .fallbackToDestructiveMigration() // Opcional: Borra y recrea si la versión cambia (¡CUIDADO!)
                    // .addMigrations(MIGRATION_1_2) // Opcional: Para migraciones más controladas
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}