package com.suitup.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shared icon size categories.
 *
 * No icon-size scale is defined anywhere in the Stitch handoff (audited: DESIGN.md,
 * blueprint.md, both Prompt 7 responsive docs) — this is a gap in the design source,
 * not an omission here. Values below are derived from the current SuitIcons default
 * (22.dp, used broadly today) so adopting this scale later causes no visual jump for
 * the existing "standard" usage; small/large/feature/navigation are new categories
 * for future use.
 */
@Immutable
data class SuitIconSizes(
    val small: Dp = 16.dp,
    val standard: Dp = 22.dp,       // matches current SuitIcons default — no behavior change
    val large: Dp = 28.dp,
    val feature: Dp = 40.dp,        // hero/empty-state/feature illustrations
    val navigation: Dp = 24.dp,     // bottom nav / top bar action icons
)

val LocalSuitIconSizes = staticCompositionLocalOf { SuitIconSizes() }
