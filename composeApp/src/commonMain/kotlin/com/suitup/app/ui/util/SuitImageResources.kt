package com.suitup.app.ui.util

import org.jetbrains.compose.resources.DrawableResource
import suitup.composeapp.generated.resources.Res
import suitup.composeapp.generated.resources.suit_beige_casual
import suitup.composeapp.generated.resources.suit_black_classic
import suitup.composeapp.generated.resources.suit_brown_premium
import suitup.composeapp.generated.resources.suit_charcoal_slim
import suitup.composeapp.generated.resources.suit_navy_executive

/**
 * Stable model-to-asset mapping (Phase 9.4, Tasks 4-7).
 *
 * Keys are looked up by [SuitModel.imageKey] / [ModeloFato.urlImagemPrevia] — never by
 * array index, grid position, or recomposition order — so visual identity survives
 * Catalog -> Product Detail -> Editor navigation.
 *
 * Two key families resolve here on purpose:
 * - The **new** semantic keys (`suit_black_classic`, `suit_navy_executive`,
 *   `suit_charcoal_slim`, `suit_beige_casual`, `suit_brown_premium`) are named after the
 *   actually-observed color/style of the real production photo behind them (Phase 9.4
 *   asset audit) and are what [MockCatalogStore] now assigns going forward.
 * - The **legacy** keys (`suit_classic_black`, `suit_grey_slim`, `suit_navy_business`,
 *   `suit_casual_linen`) are kept as aliases onto the same real assets rather than
 *   removed, because they are still referenced from files outside this phase's allowed
 *   scope — the admin catalog form's [AdminCatalogOptions.imageKeys] dropdown
 *   (`ui/screens/admin/AdminScreens.kt`, off-limits this phase) and the remote mapper's
 *   default fallback (`data/mapper/RemoteMappers.kt`, off-limits this phase). Removing
 *   them outright would silently break those call sites; aliasing keeps every existing
 *   reference resolvable while the real underlying PNGs are the corrected ones (the four
 *   files previously behind these legacy keys were mis-saved Stitch/design-mockup
 *   screenshots with baked-in UI chrome, not usable product photos — see the asset audit
 *   in UI_PHASE_9_4_CUSTOMER_PRIMARY_REPORT.md).
 *
 * Fallback: an unknown/missing key never renders a broken image — it deterministically
 * resolves to [Res.drawable.suit_black_classic], the same non-random default the mapping
 * already used before this phase.
 */
fun suitImageResourceOrNull(key: String): DrawableResource? = when (key) {
    // New semantic keys (Phase 9.4).
    "suit_black_classic" -> Res.drawable.suit_black_classic
    "suit_navy_executive" -> Res.drawable.suit_navy_executive
    "suit_charcoal_slim" -> Res.drawable.suit_charcoal_slim
    "suit_beige_casual" -> Res.drawable.suit_beige_casual
    "suit_brown_premium" -> Res.drawable.suit_brown_premium
    // Legacy keys kept as aliases — still referenced by admin/remote code out of scope this phase.
    "suit_classic_black" -> Res.drawable.suit_black_classic
    "suit_navy_business" -> Res.drawable.suit_navy_executive
    "suit_grey_slim" -> Res.drawable.suit_charcoal_slim
    "suit_casual_linen" -> Res.drawable.suit_beige_casual
    else -> null
}

fun suitImageResource(key: String): DrawableResource =
    suitImageResourceOrNull(key) ?: Res.drawable.suit_black_classic
