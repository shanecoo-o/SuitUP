package com.suitup.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * SuitUP color palette.
 *
 * Princípios aplicados das skills de design:
 * - Nunca usar #000000 puro (usamos #0D0D0D off-black)
 * - Off-white cálido em vez de #FFFFFF puro
 * - Acento dourado único (estratégia "Restrained": 1 cor de acento ≤10%)
 * - Pastéis muito desaturados para tags/estado badges
 * - Bordas hairline em vez de shadows pesadas
 */
object SuitColors {
    // Primárias do mockup
    val Ink = Color(0xFF0D0D0D)              // Off-black principal
    val Charcoal = Color(0xFF2C2C2C)         // Cinza escuro secundário
    val Bone = Color(0xFFF5F5F5)             // Off-white de fundo
    val Gold = Color(0xFFC8A96A)             // Acento dourado (alfaiataria)

    // Neutros derivados
    val Pearl = Color(0xFFFAFAFA)            // Surface mais clara que Bone
    val Mist = Color(0xFFEAEAEA)             // Bordas hairline
    val Smoke = Color(0xFFB8B8B8)            // Texto desativado / placeholder
    val Slate = Color(0xFF787774)            // Texto secundário

    // Estados (pastéis muito desaturados)
    val PaleGreen = Color(0xFFEDF3EC)
    val PaleGreenInk = Color(0xFF346538)

    val PaleAmber = Color(0xFFFBF3DB)
    val PaleAmberInk = Color(0xFF956400)

    val PaleRed = Color(0xFFFDEBEC)
    val PaleRedInk = Color(0xFF9F2F2D)

    val PaleBlue = Color(0xFFE1F3FE)
    val PaleBlueInk = Color(0xFF1F6C9F)

    // Surfaces
    val SurfaceWhite = Color(0xFFFFFFFF)
    val Overlay = Color(0x99000000)
}

/**
 * Material3 ColorScheme mapeado para Suit colors.
 */
val SuitLightColorScheme: ColorScheme = lightColorScheme(
    primary = SuitColors.Ink,
    onPrimary = SuitColors.SurfaceWhite,
    primaryContainer = SuitColors.Charcoal,
    onPrimaryContainer = SuitColors.SurfaceWhite,

    secondary = SuitColors.Gold,
    onSecondary = SuitColors.Ink,
    secondaryContainer = SuitColors.PaleAmber,
    onSecondaryContainer = SuitColors.PaleAmberInk,

    tertiary = SuitColors.Charcoal,
    onTertiary = SuitColors.SurfaceWhite,

    background = SuitColors.Bone,
    onBackground = SuitColors.Ink,

    surface = SuitColors.SurfaceWhite,
    onSurface = SuitColors.Ink,
    surfaceVariant = SuitColors.Pearl,
    onSurfaceVariant = SuitColors.Slate,

    outline = SuitColors.Mist,
    outlineVariant = SuitColors.Mist,

    error = SuitColors.PaleRedInk,
    onError = SuitColors.SurfaceWhite,
    errorContainer = SuitColors.PaleRed,
    onErrorContainer = SuitColors.PaleRedInk,
)

/**
 * Tokens semânticos extra que o Material não cobre bem.
 * Usados via LocalSuitColors.
 */
@Immutable
data class SuitColorTokens(
    val ink: Color = SuitColors.Ink,
    val charcoal: Color = SuitColors.Charcoal,
    val bone: Color = SuitColors.Bone,
    val pearl: Color = SuitColors.Pearl,
    val gold: Color = SuitColors.Gold,
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
