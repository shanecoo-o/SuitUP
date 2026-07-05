package com.suitup.app.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow

/**
 * Compact-on-scroll controller (Phase 9.3, Task 6).
 *
 * Purely optional per-screen behavior: a screen that wants its bottom nav to
 * collapse while scrolling calls one of these two overloads (matching
 * whichever scroll container it already owns — `LazyColumn`/`LazyRow` state
 * or a plain `Modifier.verticalScroll` state) and feeds the returned
 * [SuitNavDensity] into `SuitBottomNav(density = ...)` / `AdminBottomNav(density = ...)`.
 * Screens that never call this keep the default [SuitNavDensity.Expanded],
 * non-reactive bar — nothing opts in automatically.
 *
 * Direction, not absolute position, drives the state: scrolling further down
 * (content moving up) collapses to [SuitNavDensity.Compact]; scrolling back
 * up restores [SuitNavDensity.Expanded]. This matches the common "collapse
 * while reading, reappear on the way back" pattern rather than a one-shot
 * threshold.
 */
@Composable
fun rememberSuitNavDensity(listState: LazyListState): SuitNavDensity {
    var density by remember { mutableStateOf(SuitNavDensity.Expanded) }
    LaunchedEffect(listState) {
        var lastIndex = listState.firstVisibleItemIndex
        var lastOffset = listState.firstVisibleItemScrollOffset
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val scrollingDown = index > lastIndex || (index == lastIndex && offset > lastOffset)
                val scrollingUp = index < lastIndex || (index == lastIndex && offset < lastOffset)
                if (scrollingDown) density = SuitNavDensity.Compact
                else if (scrollingUp) density = SuitNavDensity.Expanded
                lastIndex = index
                lastOffset = offset
            }
    }
    return density
}

/**
 * Grid overload (Phase 9.4) — Catalog's `LazyVerticalGrid` owns a [LazyGridState],
 * not a [LazyListState]; [LazyGridState] exposes the same
 * `firstVisibleItemIndex`/`firstVisibleItemScrollOffset` shape, so the direction
 * logic mirrors the [LazyListState] overload exactly.
 */
@Composable
fun rememberSuitNavDensity(gridState: LazyGridState): SuitNavDensity {
    var density by remember { mutableStateOf(SuitNavDensity.Expanded) }
    LaunchedEffect(gridState) {
        var lastIndex = gridState.firstVisibleItemIndex
        var lastOffset = gridState.firstVisibleItemScrollOffset
        snapshotFlow { gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val scrollingDown = index > lastIndex || (index == lastIndex && offset > lastOffset)
                val scrollingUp = index < lastIndex || (index == lastIndex && offset < lastOffset)
                if (scrollingDown) density = SuitNavDensity.Compact
                else if (scrollingUp) density = SuitNavDensity.Expanded
                lastIndex = index
                lastOffset = offset
            }
    }
    return density
}

@Composable
fun rememberSuitNavDensity(scrollState: ScrollState): SuitNavDensity {
    var density by remember { mutableStateOf(SuitNavDensity.Expanded) }
    LaunchedEffect(scrollState) {
        var lastValue = scrollState.value
        snapshotFlow { scrollState.value }
            .collect { value ->
                if (value > lastValue) density = SuitNavDensity.Compact
                else if (value < lastValue) density = SuitNavDensity.Expanded
                lastValue = value
            }
    }
    return density
}
