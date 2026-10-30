package com.smtudiary.android.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

val primary = hexToColor("#330043")
val secondary = hexToColor("#9D00DB")
val background = hexToColor("#eee4f0")

fun hexToColor(hex: String): Color {
    return Color(hex.toColorInt())
}