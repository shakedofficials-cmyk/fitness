package com.recompos.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val Dark = darkColorScheme(
    primary = Color(0xFF78E08F),
    secondary = Color(0xFF8AB4F8),
    tertiary = Color(0xFFFFD166),
    error = Color(0xFFFF6B6B),
    background = Color(0xFF101418),
    surface = Color(0xFF171D22),
    surfaceVariant = Color(0xFF25313A)
)

private val Light = lightColorScheme(
    primary = Color(0xFF146C43),
    secondary = Color(0xFF2457A6),
    tertiary = Color(0xFF8A6100)
)

object CoachColors {
    val completed = Color(0xFF78E08F)
    val warning = Color(0xFFFFD166)
    val danger = Color(0xFFFF6B6B)
    val deload = Color(0xFF8AB4F8)
    val pr = Color(0xFFE9A8FF)
}

@Composable
fun RecompTheme(themeMode: String = "dark", content: @Composable () -> Unit) {
    val context = LocalContext.current
    val dark = themeMode == "dark" || (themeMode == "system" && isSystemInDarkTheme())
    val scheme: ColorScheme = when {
        Build.VERSION.SDK_INT >= 31 && dark -> dynamicDarkColorScheme(context)
        Build.VERSION.SDK_INT >= 31 -> dynamicLightColorScheme(context)
        dark -> Dark
        else -> Light
    }
    MaterialTheme(colorScheme = scheme, typography = MaterialTheme.typography, content = content)
}
