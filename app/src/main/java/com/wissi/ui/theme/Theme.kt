package com.wissi.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE6D5B8), // color del boton
    secondary = PurpleGrey40,
    tertiary = Pink40,

    background = Color.White,   // Fondo general
    surface = Color.White,      // Fondo de tarjetas, etc

    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,

    onBackground = Color.Black, // Texto sobre fondo
    onSurface = Color.Black     // Texto sobre surface
)

@Composable
fun Wissi2Theme(
    darkTheme: Boolean = false, // 👈 FORZAMOS modo claro
    dynamicColor: Boolean = false, // 👈 desactivamos colores dinámicos
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // 👈 usamos SIEMPRE el claro

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}