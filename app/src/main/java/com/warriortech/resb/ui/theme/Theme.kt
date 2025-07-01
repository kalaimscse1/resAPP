package com.warriortech.resb.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = BluePrimaryLight,
    onPrimaryContainer = TextPrimary,
    secondary = ReddishAccent,
    onSecondary = TextOnPrimary,
    secondaryContainer = ReddishAccentLight,
    onSecondaryContainer = TextPrimary,
    tertiary = Success,
    onTertiary = TextOnPrimary,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    outlineVariant = Color(0xFFEEEEEE),
    error = Error,
    onError = TextOnPrimary,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C)
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = BluePrimaryDark,
    onPrimaryContainer = TextOnDark,
    secondary = ReddishAccentLight,
    onSecondary = Color.Black,
    secondaryContainer = ReddishAccentDark,
    onSecondaryContainer = TextOnDark,
    tertiary = Success,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextOnDark,
    surface = SurfaceDark,
    onSurface = TextOnDark,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = DividerColorDark,
    outlineVariant = Color(0xFF404040),
    error = Color(0xFFFF6B6B),
    onError = Color.Black,
    errorContainer = Color(0xFF4A0E0E),
    onErrorContainer = Color(0xFFFFB4B4)
)

@Composable
fun ResbTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }
//    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}