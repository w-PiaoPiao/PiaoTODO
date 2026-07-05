package com.litetodo.app.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.stringPreferencesKey
import com.litetodo.app.util.Constants
import com.litetodo.app.util.Constants.KEY_DARK_MODE

enum class DarkModeOption { LIGHT, DARK, SYSTEM }

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1C1E),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF5F5F7),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF8E8E93),
    outline = androidx.compose.ui.graphics.Color(0xFFF0F0F0),
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    surface = DarkBackground,
    onSurface = DarkText,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
)

@Composable
fun LiteToDoTheme(
    darkMode: DarkModeOption = DarkModeOption.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (darkMode) {
        DarkModeOption.DARK -> true
        DarkModeOption.LIGHT -> false
        DarkModeOption.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
