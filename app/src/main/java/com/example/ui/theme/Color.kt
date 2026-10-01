package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val IndigoPrimary = Color(0xFF4F46E5)
val VioletAccent = Color(0xFF7C3AED)
val IndigoSecondary = Color(0xFF6366F1)
val EmeraldSuccess = Color(0xFF10B981)
val AmberWarning = Color(0xFFF59E0B)
val RoseError = Color(0xFFF43F5E)

// Dark Theme Colors
val DarkBackground = Color(0xFF0B1020)
val DarkSurface = Color(0xFF131B2E)
val DarkSurfaceVariant = Color(0xFF1E293B)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkBorder = Color(0xFF334155)

// Light Theme Colors
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)
val LightBorder = Color(0xFFE2E8F0)

val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(IndigoPrimary, VioletAccent)
)

val PrimaryGradientVertical = Brush.verticalGradient(
    colors = listOf(IndigoPrimary, VioletAccent)
)

val SuccessGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF059669), Color(0xFF10B981))
)
