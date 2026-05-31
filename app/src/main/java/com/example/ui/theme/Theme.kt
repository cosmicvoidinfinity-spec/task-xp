package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TechOrange,
    onPrimary = PureWhite,
    secondary = AmberOrange,
    onSecondary = SolidBlack,
    tertiary = PureWhite,
    background = SlateBlack,
    surface = MatteBlack,
    onBackground = PureWhite,
    onSurface = PureWhite,
    surfaceVariant = BorderGray,
    onSurfaceVariant = MutedTextDark
)

private val LightColorScheme = lightColorScheme(
    primary = TechOrange,
    onPrimary = PureWhite,
    secondary = AmberOrange,
    onSecondary = SolidBlack,
    tertiary = SolidBlack,
    background = LightBackground,
    surface = LightSurface,
    onBackground = SolidBlack,
    onSurface = SolidBlack,
    surfaceVariant = LightBorder,
    onSurfaceVariant = MutedTextDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
