package com.pneuma.fotomarwms_grupo5.ui.screen.componentes

import androidx.compose.runtime.*
import com.pneuma.fotomarwms_grupo5.models.Usuario
import androidx.compose.material3.*


// Función reutilizable para contar usuarios activos
fun contarUsuariosActivos(lista: List<Usuario>?): Int {
    return lista?.count { it.activo } ?: 0
}