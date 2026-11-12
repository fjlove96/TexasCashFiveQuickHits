package com.example.texascashfivequickhits.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CyberBackground = Color(0xFF0F172A)
val CyberCyan = Color(0xFF0EA5E9)
val CyberGold = Color(0xFFF59E0B)
val CyberGreen = Color(0xFFA7F3D0)
val CyberSurface = Color(0xFF1E293B)
val CyberText = Color(0xFFF8FAFC)
val CyberTextSecondary = Color(0xFF94A3B8)

private val CyberpunkDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    secondary = CyberGold,
    onSecondary = Color.Black,
    tertiary = CyberGreen,
    surface = CyberSurface,
    background = CyberBackground,
    onSurface = CyberText,
    onBackground = CyberText
)

@Composable
fun TexasCashFiveQuickHitsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberpunkDarkColorScheme,
        content = content
    )
}
