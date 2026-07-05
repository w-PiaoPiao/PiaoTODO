package com.litetodo.app.util

import androidx.compose.ui.graphics.Color

fun parseColor(hex: String): Color {
    val colorString = hex.removePrefix("#")
    val long = colorString.toLong(16)
    return when (colorString.length) {
        6 -> Color(0xFF000000 or (long shl 8) or 0xFF)
        8 -> Color(long)
        else -> Color.Gray
    }
}
