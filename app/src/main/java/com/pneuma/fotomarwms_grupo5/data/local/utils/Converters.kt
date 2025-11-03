package com.pneuma.fotomarwms_grupo5.data.local.utils

import androidx.room.TypeConverter
import com.pneuma.fotomarwms_grupo5.models.EstadoAprobacion
import com.pneuma.fotomarwms_grupo5.models.Rol
import com.pneuma.fotomarwms_grupo5.models.TipoMensaje
import com.pneuma.fotomarwms_grupo5.models.TipoMovimiento

class Converters {
    // Convertidores para Enums (Ejemplo con Rol)
    @TypeConverter
    fun fromRol(value: Rol?): String? {
        return value?.name // Guarda el nombre del enum como String
    }

    @TypeConverter
    fun toRol(value: String?): Rol? {
        return value?.let { Rol.valueOf(it) } // Convierte el String de vuelta a Enum
    }

    @TypeConverter
    fun fromEstadoAprobacion(value: EstadoAprobacion?): String? {
        return value?.name
    }

    @TypeConverter
    fun toEstadoAprobacion(value: String?): EstadoAprobacion? {
        return value?.let { EstadoAprobacion.valueOf(it) }
    }

    @TypeConverter
    fun fromTipoMovimiento(value: TipoMovimiento?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTipoMovimiento(value: String?): TipoMovimiento? {
        return value?.let { TipoMovimiento.valueOf(it) }
    }

    @TypeConverter
    fun fromTipoMensaje(value: TipoMensaje?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTipoMensaje(value: String?): TipoMensaje? {
        return value?.let { TipoMensaje.valueOf(it) }
    }

    // Puedes añadir más convertidores aquí para Dates (a Long), List<String> (a JSON String), etc.
    /* Ejemplo para Date:
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    */
}