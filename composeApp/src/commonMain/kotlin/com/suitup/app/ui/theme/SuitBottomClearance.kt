package com.suitup.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Bottom-clearance policy (Phase 9.3, Task 19).
 *
 * Phase 9.1 introduced a single `bottomScrollClearance = 128dp` value. That
 * was never meant as universal padding for every screen — this object splits
 * it into the explicit situations a screen can actually be in, so nothing
 * defaults to 128dp just because that was the only number available.
 *
 * Ownership: a clearance value is how much space SCREEN CONTENT reserves at
 * its own bottom edge so it isn't covered by chrome that a SCAFFOLD (not the
 * content) is responsible for drawing — see the ownership model documented in
 * `SuitScaffolds.kt`. Content asks for the policy matching whichever scaffold
 * it is hosted in; it never invents its own bottom padding.
 */
object SuitBottomClearance {
    /** No bottom chrome — content may use the full available height (e.g. Immersive stage with no CTA). */
    val none: Dp = 0.dp

    /** Reserved for [com.suitup.app.ui.components.SuitBottomNav] / `AdminBottomNav` at [com.suitup.app.ui.components.SuitNavDensity.Expanded]. */
    val bottomNavExpanded: Dp = 64.dp

    /** Reserved for [com.suitup.app.ui.components.SuitBottomNav] / `AdminBottomNav` at [com.suitup.app.ui.components.SuitNavDensity.Compact]. */
    val bottomNavCompact: Dp = 52.dp

    /** Reserved for a fixed CTA region with no bottom navigation present (Detail / Form Flow screens). */
    val fixedCta: Dp = 84.dp

    /** Reserved for a fixed CTA on an Immersive stage screen — tighter than the form-flow CTA. */
    val immersiveCta: Dp = 72.dp

    /** Reserved when a bottom nav AND a floating CTA occupy the same screen simultaneously. */
    val bottomNavPlusFloatingCta: Dp = 140.dp
}
