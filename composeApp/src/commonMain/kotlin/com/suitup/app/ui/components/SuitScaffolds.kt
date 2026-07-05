package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTheme

/**
 * Shared scaffold archetypes (Phase 9.3, Tasks 2-6, 11).
 *
 * ## Inset / safe-area ownership model (Tasks 2, 3)
 *
 * SuitUP is confirmed **non-edge-to-edge** on Android (no `enableEdgeToEdge()`
 * / `WindowCompat.setDecorFitsSystemWindows(false)` in `MainActivity.kt`, and
 * `themes.xml` does not override `decorFitsSystemWindows`). That means the OS
 * already reserves system-bar space by insetting the whole activity window
 * before Compose ever measures — status bar and navigation bar space is
 * real, but it is consumed *automatically*, outside Compose's layout tree.
 *
 * Given that baseline, these scaffolds deliberately do **not** apply
 * `WindowInsets.systemBars` / `.navigationBars` padding. Doing so on top of
 * an already-inset window would double-pad (system reserves the space, then
 * Compose reserves it *again* inside the already-adjusted content area) —
 * this is the exact stacking bug this phase's mandate warns against. If a
 * future phase switches the app to edge-to-edge, that is the point at which
 * real `WindowInsets` padding should be introduced here, in one place, so
 * every scaffold picks it up at once instead of every screen inventing its
 * own fix.
 *
 * The one exception is [Modifier.imePadding] in [SuitFormFlowScaffold]: it is
 * a portable, purely additive inset (keyboard height only, never system-bar
 * height) that does not stack with anything the OS is already doing, so it
 * is safe to apply directly.
 *
 * ## Ownership split: scaffold vs. content
 *
 * A scaffold owns and draws any chrome docked to an edge of the screen (top
 * bar, bottom nav, fixed CTA bar). Screen content owns only the middle
 * region and, when it scrolls, reserves clearance at its own trailing edge
 * so the last item isn't covered by that chrome — using the matching
 * [SuitBottomClearance] constant for whichever chrome the hosting scaffold
 * draws. Content never draws its own bottom bar and a scaffold never adds
 * scroll-clearance padding on content's behalf; each side owns exactly one
 * half of the problem.
 *
 * ## Navigation visibility (Task 16)
 *
 * Bottom-nav visibility is structural, not an ad hoc per-screen flag:
 * [SuitPrimaryDestinationScaffold] is the only archetype with a `bottomNav`
 * slot, so only screens that are top-level tab destinations can show one.
 * Every other archetype has no such slot at all — a Detail/Immersive/
 * FormFlow/List screen cannot accidentally grow a bottom nav because the
 * scaffold gives it nowhere to put one.
 */

/**
 * Archetype A — primary tab destination (Home / Catalog / Orders / Profile).
 * The only archetype with a `bottomNav` slot (Task 16). `topBar` is optional
 * since some primary destinations build their own in-content header.
 */
@Composable
fun SuitPrimaryDestinationScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    bottomNav: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().background(SuitColors.Bone)) {
        topBar?.invoke()
        Box(modifier = Modifier.weight(1f)) { content() }
        bottomNav()
    }
}

/**
 * Archetype B — pushed detail screen (Product Detail, Order Detail/Tracking).
 * No bottom nav slot; an optional fixed CTA bar sits at the bottom instead.
 * Content reserves [SuitBottomClearance.fixedCta] when [fixedCta] is present.
 */
@Composable
fun SuitDetailScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    fixedCta: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().background(SuitColors.Bone)) {
        topBar?.invoke()
        Box(modifier = Modifier.weight(1f)) { content() }
        fixedCta?.invoke()
    }
}

/**
 * Archetype C — immersive stage (Editor 2D, Preview 3D). Content fills the
 * entire available area (e.g. a [SuitImageContainer] stage); `topBar` and
 * `fixedCta`, when present, float on top of it rather than displacing it —
 * an immersive stage is meant to read as full-bleed, not letterboxed by
 * chrome the way Detail/FormFlow screens are.
 */
@Composable
fun SuitImmersiveScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    fixedCta: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(SuitColors.WarmBlack)) {
        content()
        Column(modifier = Modifier.fillMaxSize()) {
            topBar?.invoke()
            Spacer(modifier = Modifier.weight(1f))
            fixedCta?.invoke()
        }
    }
}

/**
 * Archetype D — multi-step form flow (Checkout steps, Address, Payment).
 * Applies [Modifier.imePadding] so the fixed CTA / last field never ends up
 * hidden behind the on-screen keyboard — see the ownership-model note above
 * on why this is the one real inset exception.
 */
@Composable
fun SuitFormFlowScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    fixedCta: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SuitColors.Bone)
            .imePadding(),
    ) {
        topBar?.invoke()
        Box(modifier = Modifier.weight(1f)) { content() }
        fixedCta?.invoke()
    }
}

/**
 * Archetype E — standalone list/browse screen pushed outside a tab's own
 * bottom nav context (e.g. a list reached via push rather than as tab root).
 * No bottom nav, no fixed CTA slot — a list screen's action is picking an
 * item, not confirming a persistent action.
 */
@Composable
fun SuitListScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().background(SuitColors.Bone)) {
        topBar?.invoke()
        Box(modifier = Modifier.weight(1f)) { content() }
    }
}

/**
 * Fixed CTA container (Task 11) — generic docked bottom bar a Detail/
 * Immersive/FormFlow scaffold hosts in its `fixedCta` slot. Just background +
 * responsive padding; the caller supplies the actual [SuitButton]/[SuitDualBottomBar]-style
 * content, so this stays a container, not a second button component.
 */
@Composable
fun SuitFixedCtaBar(
    modifier: Modifier = Modifier,
    background: Color = SuitColors.Bone,
    content: @Composable () -> Unit,
) {
    val responsive = SuitTheme.responsive
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = responsive.horizontalContentPadding, vertical = 12.dp),
    ) {
        content()
    }
}
