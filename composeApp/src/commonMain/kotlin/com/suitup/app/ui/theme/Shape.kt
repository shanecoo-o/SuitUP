package com.suitup.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * Shapes do SuitUP.
 *
 * Princípio: bordas crisp (4-12px max). Pills (rounded-full) só para tags pequenas.
 * Sem bordas redondas em containers grandes.
 */
@Immutable
data class SuitShapes(
    val none: RoundedCornerShape = RoundedCornerShape(0.dp),
    val xs: RoundedCornerShape = RoundedCornerShape(4.dp),
    val sm: RoundedCornerShape = RoundedCornerShape(6.dp),
    val md: RoundedCornerShape = RoundedCornerShape(8.dp),
    val lg: RoundedCornerShape = RoundedCornerShape(12.dp),
    val xl: RoundedCornerShape = RoundedCornerShape(16.dp),
    // Phase 6C.1: Stitch DESIGN.md's "xl" radius tier (1.5rem/24dp), used for e.g.
    // Primary Cards per the handoff. Added as a new tier rather than resizing the
    // existing `card` token in place, to avoid a global visual change ahead of the
    // screen-migration phase; adopt per-component in later phases as needed.
    val xxl: RoundedCornerShape = RoundedCornerShape(24.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(999.dp),

    // Semânticos
    val button: RoundedCornerShape = RoundedCornerShape(8.dp),
    val card: RoundedCornerShape = RoundedCornerShape(10.dp),
    val input: RoundedCornerShape = RoundedCornerShape(8.dp),
    val sheet: RoundedCornerShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
    val tag: RoundedCornerShape = RoundedCornerShape(999.dp),
    val avatar: RoundedCornerShape = RoundedCornerShape(999.dp),
)

val LocalSuitShapes = staticCompositionLocalOf { SuitShapes() }

val SuitMaterialShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp),
)
