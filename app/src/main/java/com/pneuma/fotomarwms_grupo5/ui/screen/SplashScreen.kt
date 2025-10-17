package com.pneuma.fotomarwms_grupo5.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.pneuma.fotomarwms_grupo5.R

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    // Espera 2.5 segundos antes de ir al login
    LaunchedEffect(Unit) {
        delay(2500)
        onNavigateToLogin()
    }

    // Diseño del splash
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // 👈 usa tu logo aquí
            contentDescription = "Logo Fotomar",
            modifier = Modifier.size(180.dp)
        )
    }
}
