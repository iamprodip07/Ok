package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FreshGreenPrimaryDark,
    onPrimary = FreshGreenOnPrimaryDark,
    primaryContainer = FreshGreenContainerDark,
    onPrimaryContainer = FreshGreenOnContainerDark,
    secondary = CitrusOrangeDark,
    onSecondary = FreshGreenOnPrimaryDark,
    secondaryContainer = CitrusOrangeContainerDark,
    onSecondaryContainer = FreshOnSurfaceDark,
    tertiary = GoldenAmberDark,
    onTertiary = FreshGreenOnPrimaryDark,
    tertiaryContainer = GoldenAmberContainerDark,
    onTertiaryContainer = FreshOnSurfaceDark,
    background = FreshBackgroundDark,
    onBackground = FreshOnSurfaceDark,
    surface = FreshSurfaceDark,
    onSurface = FreshOnSurfaceDark,
    surfaceVariant = FreshSurfaceVariantDark,
    onSurfaceVariant = FreshOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = FreshGreenPrimary,
    onPrimary = FreshGreenOnPrimary,
    primaryContainer = FreshGreenContainer,
    onPrimaryContainer = FreshGreenOnContainer,
    secondary = CitrusOrange,
    onSecondary = FreshGreenOnPrimary,
    secondaryContainer = CitrusOrangeContainer,
    onSecondaryContainer = FreshOnSurfaceLight,
    tertiary = GoldenAmber,
    onTertiary = FreshGreenOnPrimary,
    tertiaryContainer = GoldenAmberContainer,
    onTertiaryContainer = FreshOnSurfaceLight,
    background = FreshBackgroundLight,
    onBackground = FreshOnSurfaceLight,
    surface = FreshSurfaceLight,
    onSurface = FreshOnSurfaceLight,
    surfaceVariant = FreshSurfaceVariantLight,
    onSurfaceVariant = FreshOnSurfaceVariantLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted brand theme for consistent vibrant look
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
