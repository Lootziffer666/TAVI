package com.example.tavi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LilitaOne = FontFamily.Default
val Barlow = FontFamily.Default
val JetBrainsMono = FontFamily.Monospace

val TaviTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = LilitaOne,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        color = TaviAccent
    ),
    headlineMedium = TextStyle(
        fontFamily = LilitaOne,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Barlow,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Barlow,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )
)
