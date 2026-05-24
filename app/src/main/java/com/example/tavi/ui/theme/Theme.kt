package com.example.tavi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TaviColorScheme = darkColorScheme(
    primary = TaviAccent,
    onPrimary = SpaceBlack,
    background = SpaceBlack,
    surface = SpaceNavy,
    onBackground = Color.White,
    onSurface = Color.White,
    secondary = BreathBlue,
    error = RiskRed
)

@Composable
fun TaviTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TaviColorScheme,
        typography = TaviTypography,
        content = content
    )
}
