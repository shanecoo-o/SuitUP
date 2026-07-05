package com.suitup.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Top bar variants (Phase 9.3, Task 9).
 *
 * All four are thin semantic wrappers around the single existing
 * [SuitTopBar] engine (unmodified) — they fix the `dark` preset and the
 * parameter set each screen archetype actually needs, so screens ask for the
 * bar matching their scaffold instead of re-deriving `dark`/background
 * choices ad hoc. This mirrors the wrapper pattern the codebase already uses
 * for [PremiumTopBar] (a Charcoal/dark preset over [SuitTopBar]).
 *
 * Purely additive: existing direct `SuitTopBar(...)` call sites across
 * feature screens are untouched and remain valid — normalization happens by
 * making the semantic entry points *available*, not by forcing a migration
 * of screens this phase.
 */

/** Top-level destination bar (Home/Catalog/Orders/Profile tabs) — transparent over the screen's own Bone background, usually no back chevron. */
@Composable
fun SuitPrimaryTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onCart: (() -> Unit)? = null,
    cartBadgeCount: Int = 0,
    trailing: (@Composable () -> Unit)? = null,
    centerContent: (@Composable () -> Unit)? = null,
) {
    SuitTopBar(
        modifier = modifier,
        title = title,
        onBack = null,
        onCart = onCart,
        cartBadgeCount = cartBadgeCount,
        dark = false,
        trailing = trailing,
        centerContent = centerContent,
    )
}

/** Pushed detail/form-flow screen bar — always has a back target. Light by default; pass `dark = true` for the Charcoal detail style (e.g. order tracking). */
@Composable
fun SuitDetailTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    onCart: (() -> Unit)? = null,
    cartBadgeCount: Int = 0,
    dark: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    SuitTopBar(
        modifier = modifier,
        title = title,
        onBack = onBack,
        onCart = onCart,
        cartBadgeCount = cartBadgeCount,
        dark = dark,
        trailing = trailing,
    )
}

/** Immersive stage bar (Editor 2D/3D preview) — dark by default to read over a WarmBlack/EditorStage canvas; `trailing` carries stage controls (rotate/zoom/etc). */
@Composable
fun SuitImmersiveTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    centerContent: (@Composable () -> Unit)? = null,
) {
    SuitTopBar(
        modifier = modifier,
        title = title,
        onBack = onBack,
        onCart = null,
        dark = true,
        trailing = trailing,
        centerContent = centerContent,
    )
}

/** Admin surface bar — Charcoal/dark to match the admin dashboard's enterprise styling; no cart slot (admin has no shopping cart). */
@Composable
fun SuitAdminTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    SuitTopBar(
        modifier = modifier,
        title = title,
        onBack = onBack,
        onCart = null,
        dark = true,
        trailing = trailing,
    )
}
