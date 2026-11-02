package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TechnicianDarkColorScheme = darkColorScheme(
    primary = TechCyanPrimary,
    onPrimary = TechDarkBg,
    primaryContainer = TechSurfaceVariant,
    onPrimaryContainer = TechCyanPrimary,
    secondary = TechBlueSecondary,
    onSecondary = TechDarkBg,
    secondaryContainer = TechSurfaceLight,
    onSecondaryContainer = TechTextPrimary,
    tertiary = TechAmber,
    onTertiary = TechDarkBg,
    background = TechDarkBg,
    onBackground = TechTextPrimary,
    surface = TechSurface,
    onSurface = TechTextPrimary,
    surfaceVariant = TechSurfaceVariant,
    onSurfaceVariant = TechTextSecondary,
    outline = TechBorder,
    outlineVariant = TechBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Technician theme is always immersive dark
    dynamicColor: Boolean = false, // Keep our high-tech branding colors intact
    content: @Composable () -> Unit
) {
    val colorScheme = TechnicianDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = TechDarkBg.toArgb()
            window.navigationBarColor = TechDarkBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
