package com.suitup.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * SuitUP dark-theme surface hierarchy.
 *
 * The Stitch DESIGN.md source defines elevation as tonal layering (numbered
 * "Level 0/1/2"), not drop shadows — restrained dark-theme depth comes from
 * surface-color contrast and a subtle border on the topmost level, never
 * large shadows. This maps those levels onto the existing none/low/medium/high
 * naming requested for the foundation.
 */
enum class SuitElevationLevel { NONE, LOW, MEDIUM, HIGH }

@Immutable
data class SuitElevationTokens(
    val none: Color = SuitColors.Background,          // Level 0 — base app background
    val low: Color = SuitColors.Surface,               // Level 1 — standard cards/items
    val medium: Color = SuitColors.SurfaceRaised,       // Level 1 (alt) — elevated components/menus
    val high: Color = SuitColors.SurfaceSelected,       // Level 2 — active/hover/selected
    val highBorder: Color = SuitColors.BorderStrong,    // Level 2's accompanying 1px border
)

val LocalSuitElevation = staticCompositionLocalOf { SuitElevationTokens() }

fun SuitElevationTokens.surfaceFor(level: SuitElevationLevel): Color = when (level) {
    SuitElevationLevel.NONE -> none
    SuitElevationLevel.LOW -> low
    SuitElevationLevel.MEDIUM -> medium
    SuitElevationLevel.HIGH -> high
}
