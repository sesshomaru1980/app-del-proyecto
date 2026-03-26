package com.example.neobarber.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = NeoBlue,
    secondary = NeoDarkBlue,
    background = NeoWhite,
    surface = NeoWhite,
    onPrimary = NeoWhite,
    onSecondary = NeoWhite,
    onBackground = NeoDark,
    onSurface = NeoDark
)

private val DarkColors = darkColorScheme(
    primary = NeoBlue,
    secondary = NeoDarkBlue,
    background = NeoDark,
    surface = NeoDark,
    onPrimary = NeoWhite,
    onSecondary = NeoWhite,
    onBackground = NeoWhite,
    onSurface = NeoWhite
)

@Composable
fun NeoBarberTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}