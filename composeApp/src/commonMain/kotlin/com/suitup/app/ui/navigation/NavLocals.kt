package com.suitup.app.ui.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.suitup.app.ui.components.SuitNavDensity

/**
 * Callback de logout global, fornecida pelo Navigator outer (o que envolve o MainShellScreen).
 *
 * O ProfileVoyagerScreen consome isto para fazer logout que destrói o MainShell
 * e volta ao AuthFlow. Sem este local, o ProfileVoyagerScreen está dentro de um
 * Navigator interno (da ProfileTab) que não tem acesso direto ao stack outer.
 *
 * Default: no-op. MainShellScreen sobrescreve com o callback real.
 */
val LocalSignOut = compositionLocalOf<() -> Unit> { { } }

/**
 * Shared compact-on-scroll density holder (Phase 9.4's first real
 * [com.suitup.app.ui.components.rememberSuitNavDensity] integration).
 *
 * The bottom nav lives in [MainShellScreen], one level *above* each tab's own
 * inner `Navigator`/content (Home's `LazyColumn`, Catalog's `LazyVerticalGrid`)
 * — there is no direct parameter path from that inner scrollable up to
 * `SuitBottomNav(density = ...)`. This local closes that gap: a tab-root
 * screen calls `rememberSuitNavDensity(its own scroll state)` and pushes the
 * result in via `LaunchedEffect`, and [MainShellScreen] reads the same
 * [MutableState] to drive the real bottom nav. `staticCompositionLocalOf` is
 * safe here because the provided [MutableState] instance itself never changes
 * across recompositions in [MainShellScreen] — only its `.value` does, which
 * Compose already tracks through normal state-read invalidation.
 */
val LocalSuitNavDensity = staticCompositionLocalOf<MutableState<SuitNavDensity>> {
    mutableStateOf(SuitNavDensity.Expanded)
}
