package com.suitup.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * SuitUP dark sartorial palette.
 *
 * The Figma reference is dark-first: charcoal app background, layered dark
 * surfaces, bone text, beige-gray muted text, and restrained gold accents.
 * Several existing screens use SuitColors.Ink for text, so Ink now represents
 * the primary readable ink-on-dark color. Use the explicit Surface* tokens for
 * containers and Charcoal/Black for dark fills.
 */
object SuitColors {
    val Black = Color(0xFF0E0E0E)
    val Charcoal = Color(0xFF131313)
    val SurfaceLow = Color(0xFF1C1B1B)
    val Surface = Color(0xFF201F1F)
    val SurfaceHigh = Color(0xFF2A2A2A)
    val SurfaceHighest = Color(0xFF353534)

    val Ink = Color(0xFFE5E2E1)
    val Bone = Color(0xFF131313)
    val Pearl = Color(0xFFE5E2E1)
    val SurfaceWhite = Color(0xFFE5E2E1)

    val Slate = Color(0xFFD0C5AF)
    val Smoke = Color(0xFF99907C)
    val Mist = Color(0xFF4D4635)

    val Gold = Color(0xFFF2CA50)
    val GoldDeep = Color(0xFFD4AF37)
    val GoldInk = Color(0xFF3C2F00)

    val Navy = Color(0xFF1A2A44)
    val Olive = Color(0xFF3D4434)
    val Burgundy = Color(0xFF4A1A1A)
    val Tan = Color(0xFFB5A68D)

    val PaleGreen = Color(0xFF17351D)
    val PaleGreenInk = Color(0xFFB8E3BC)
    val PaleAmber = Color(0xFF3E310A)
    val PaleAmberInk = Color(0xFFFFDD78)
    val PaleRed = Color(0xFF3B1618)
    val PaleRedInk = Color(0xFFFFB7B8)
    val PaleBlue = Color(0xFF102B3D)
    val PaleBlueInk = Color(0xFFA9D8F8)

    val Overlay = Color(0xCC000000)
}

val SuitLightColorScheme: ColorScheme = darkColorScheme(
    primary = SuitColors.Gold,
    onPrimary = SuitColors.GoldInk,
    primaryContainer = SuitColors.GoldDeep,
    onPrimaryContainer = SuitColors.GoldInk,

    secondary = SuitColors.Slate,
    onSecondary = SuitColors.Black,
    secondaryContainer = SuitColors.SurfaceHigh,
    onSecondaryContainer = SuitColors.Slate,

    tertiary = SuitColors.Tan,
    onTertiary = SuitColors.Black,

    background = SuitColors.Bone,
    onBackground = SuitColors.Ink,

    surface = SuitColors.Surface,
    onSurface = SuitColors.Ink,
    surfaceVariant = SuitColors.SurfaceHigh,
    onSurfaceVariant = SuitColors.Slate,

    outline = SuitColors.Mist,
    outlineVariant = SuitColors.Mist,

    error = SuitColors.PaleRedInk,
    onError = SuitColors.Black,
    errorContainer = SuitColors.PaleRed,
    onErrorContainer = SuitColors.PaleRedInk,
)

@Immutable
data class SuitColorTokens(
    val ink: Color = SuitColors.Ink,
    val charcoal: Color = SuitColors.Charcoal,
    val bone: Color = SuitColors.Bone,
    val pearl: Color = SuitColors.Pearl,
    val gold: Color = SuitColors.Gold,
    val goldDeep: Color = SuitColors.GoldDeep,
    val goldInk: Color = SuitColors.GoldInk,
    val surfaceLow: Color = SuitColors.SurfaceLow,
    val surface: Color = SuitColors.Surface,
    val surfaceHigh: Color = SuitColors.SurfaceHigh,
    val mist: Color = SuitColors.Mist,
    val slate: Color = SuitColors.Slate,
    val smoke: Color = SuitColors.Smoke,
    val statusSuccess: Color = SuitColors.PaleGreenInk,
    val statusSuccessBg: Color = SuitColors.PaleGreen,
    val statusPending: Color = SuitColors.PaleAmberInk,
    val statusPendingBg: Color = SuitColors.PaleAmber,
    val statusError: Color = SuitColors.PaleRedInk,
    val statusErrorBg: Color = SuitColors.PaleRed,
    val statusInfo: Color = SuitColors.PaleBlueInk,
    val statusInfoBg: Color = SuitColors.PaleBlue,
    val overlay: Color = SuitColors.Overlay,
)
