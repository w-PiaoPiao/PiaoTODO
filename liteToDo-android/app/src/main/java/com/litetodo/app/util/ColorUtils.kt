package com.litetodo.app.util

import androidx.compose.ui.graphics.Color

fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: IllegalArgumentException) {
        Color.Gray
    }
}
