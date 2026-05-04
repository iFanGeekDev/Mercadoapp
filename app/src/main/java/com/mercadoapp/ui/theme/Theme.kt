package com.mercadoapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    // Brand
    primary              = Brand500,
    onPrimary            = Color.White,
    primaryContainer     = Dark600,
    onPrimaryContainer   = Brand300,

    // Accent / Secondary
    secondary            = Accent500,
    onSecondary          = Dark900,
    secondaryContainer   = Dark500,
    onSecondaryContainer = Accent400,

    // Tertiary
    tertiary             = Brand300,
    onTertiary           = Dark800,

    // Background / Surface
    background           = Dark900,
    onBackground         = TextPrimary,
    surface              = Dark800,
    onSurface            = TextPrimary,
    surfaceVariant       = Dark700,
    onSurfaceVariant     = TextSecondary,

    // Outline
    outline              = Dark400,
    outlineVariant       = Dark500,

    // Error
    error                = ErrorRed,
    onError              = Color.White,
    errorContainer       = Color(0xFF3D1A21),
    onErrorContainer     = Color(0xFFFFB3BC),

    // Inverse
    inverseSurface       = Light100,
    inverseOnSurface     = Dark900,
    inversePrimary       = Brand600,

    // Scrim
    scrim                = Color(0x99000000)
)

private val LightColorScheme = lightColorScheme(
    primary              = Brand600,
    onPrimary            = Color.White,
    primaryContainer     = Brand300,
    onPrimaryContainer   = Brand700,

    secondary            = Accent600,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFB2F5EA),
    onSecondaryContainer = Color(0xFF003D30),

    background           = Light100,
    onBackground         = Color(0xFF1A1A2E),
    surface              = Color.White,
    onSurface            = Color(0xFF1A1A2E),
    surfaceVariant       = Light200,
    onSurfaceVariant     = Color(0xFF5A5A7A),

    error                = ErrorRed,
    onError              = Color.White
)

@Composable
fun MercadoTheme(
    darkTheme: Boolean = true, // Dark by default — premium feel
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
