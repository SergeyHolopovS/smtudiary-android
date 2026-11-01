package com.smtudiary.android.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

val primary = hexToColor("#B486C8")
val onPrimary = hexToColor("#F0EDF2")
val secondary = hexToColor("#9A6FAE")
val onSecondary = hexToColor("#756E7D")
val tertiary = hexToColor("#ABA5AF")
val background = hexToColor("#0E0D10")
val onBackground = hexToColor("#302B35")
val surfaceContainer = hexToColor("#19171C")
val surfaceContainerHigh = hexToColor("#29212F")

fun hexToColor(hex: String): Color {
    return Color(hex.toColorInt())
}