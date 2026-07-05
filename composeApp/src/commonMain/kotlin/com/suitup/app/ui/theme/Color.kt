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
    val InkBlack = Color(0xFF0A0A0C)
    val Charcoal = Color(0xFF111216)
    val WarmBlack = Color(0xFF15171B)
    val SlateSurface = Color(0xFF1C1F24)
    val MutedGray = Color(0xFF2B2E34)
    val BoneText = Color(0xFFEDE8DE)
    val PearlText = Color(0xFFF6F5F3)
    val GoldPrimary = Color(0xFFD4AF37)
    val GoldChampagne = Color(0xFFE6C97A)
    val BronzeSubtle = Color(0xFFB78C4A)
    val Success = Color(0xFF2ECC71)
    val Warning = Color(0xFFFFC107)
    val Error = Color(0xFFE74C3C)
    val Info = Color(0xFF3DA9FC)

    // Compatibility aliases used throughout the current screens.
    val Black = InkBlack
    val SurfaceLow = WarmBlack
    val Surface = SlateSurface
    val SurfaceHigh = MutedGray
    val SurfaceHighest = Color(0xFF343840)

    val Ink = PearlText
    val Bone = InkBlack
    val Pearl = PearlText
    val SurfaceWhite = PearlText

    val Slate = BoneText
    val Smoke = Color(0xFFA6A29A)
    val Mist = BronzeSubtle.copy(alpha = 0.42f)

    val Gold = GoldPrimary
    val GoldDeep = BronzeSubtle
    val GoldInk = InkBlack

    val Navy = Color(0xFF1A2A44)
    val Olive = Color(0xFF3D4434)
    val Burgundy = Color(0xFF4A1A1A)
    val Tan = Color(0xFFB5A68D)

    val PaleGreen = Color(0xFF123321)
    val PaleGreenInk = Success
    val PaleAmber = Color(0xFF3A2E08)
    val PaleAmberInk = Warning
    val PaleRed = Color(0xFF3A1717)
    val PaleRedInk = Error
    val PaleBlue = Color(0xFF102B3D)
    val PaleBlueInk = Info

    val Overlay = Color(0xCC000000)

    // --- Phase 6C.1: Stitch-aligned semantic tokens (additive) ---
    // Sourced from Prompt 9 `suitup_implementation_blueprint.md` (literal handoff names)
    // with DESIGN.md's Material3 tonal ramp used to fill gaps (surface tiers, on-* roles).
    // Where the two Stitch sources disagreed (accent gold, error) or were silent
    // (success/warning/info, disabled/pressed states, scrim), the existing validated
    // repo value is kept rather than inventing an unconfirmed one — see
    // UI_PHASE_6C1_FOUNDATION_REPORT.md for the full discrepancy log.
    val Background = Color(0xFF131315)          // blueprint.md Background
    val SurfaceRaised = Color(0xFF252427)        // blueprint.md SurfaceRaised
    val SurfaceInteractive = Color(0xFF2A2A2C)   // DESIGN.md surface-container-high (hover)
    val SurfaceSelected = Color(0xFF353437)      // DESIGN.md surface-container-highest (Level 2 active/selected)
    val Border = Color(0xFF39393B)               // blueprint.md Border
    val BorderStrong = Color(0xFF99907C)         // DESIGN.md outline (higher-contrast border)

    val TextPrimary = PearlText                  // kept: existing warm off-white preferred over Stitch's pure #FFFFFF / #E5E1E4
    val TextSecondary = Color(0xFFA1A1A5)        // blueprint.md TextSecondary
    val TextMuted = Smoke                        // reuse existing muted tone for lowest-emphasis text

    val AccentGold = GoldPrimary                 // kept: existing #D4AF37 (also DESIGN.md's rendered primary-container value); blueprint's #F2CA50 conflicts with DESIGN.md's own HTML output
    val AccentGoldPressed = BronzeSubtle          // deeper existing tone reused for pressed state
    val AccentGoldDisabled = GoldPrimary.copy(alpha = 0.38f) // derived (no disabled value specified anywhere in handoff)

    val Scrim = Color(0x80000000)                // derived Material-default scrim (not specified in handoff docs); Overlay above remains the heavier modal backdrop already in use
}

val SuitDarkColorScheme: ColorScheme = darkColorScheme(
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

    outline = SuitColors.Border,
    outlineVariant = SuitColors.BorderStrong,

    error = SuitColors.PaleRedInk,
    onError = SuitColors.Black,
    errorContainer = SuitColors.PaleRed,
    onErrorContainer = SuitColors.PaleRedInk,
)

val SuitLightColorScheme: ColorScheme = SuitDarkColorScheme

@Immutable
data class SuitColorTokens(
    val inkBlack: Color = SuitColors.InkBlack,
    val ink: Color = SuitColors.Ink,
    val charcoal: Color = SuitColors.Charcoal,
    val warmBlack: Color = SuitColors.WarmBlack,
    val bone: Color = SuitColors.Bone,
    val pearl: Color = SuitColors.Pearl,
    val gold: Color = SuitColors.Gold,
    val goldChampagne: Color = SuitColors.GoldChampagne,
    val bronzeSubtle: Color = SuitColors.BronzeSubtle,
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

    // Phase 6C.1 additions
    val background: Color = SuitColors.Background,
    val surfaceRaised: Color = SuitColors.SurfaceRaised,
    val surfaceInteractive: Color = SuitColors.SurfaceInteractive,
    val surfaceSelected: Color = SuitColors.SurfaceSelected,
    val border: Color = SuitColors.Border,
    val borderStrong: Color = SuitColors.BorderStrong,
    val textPrimary: Color = SuitColors.TextPrimary,
    val textSecondary: Color = SuitColors.TextSecondary,
    val textMuted: Color = SuitColors.TextMuted,
    val accentGold: Color = SuitColors.AccentGold,
    val accentGoldPressed: Color = SuitColors.AccentGoldPressed,
    val accentGoldDisabled: Color = SuitColors.AccentGoldDisabled,
    val scrim: Color = SuitColors.Scrim,
)
