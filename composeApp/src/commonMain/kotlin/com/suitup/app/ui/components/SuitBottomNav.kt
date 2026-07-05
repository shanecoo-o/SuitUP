package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.BagIcon
import com.suitup.app.ui.icons.HomeIcon
import com.suitup.app.ui.icons.PersonIcon
import com.suitup.app.ui.icons.ShirtIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Shared bottom-navigation rendering engine (Phase 9.3, Tasks 6/15).
 *
 * [SuitBottomNav] (customer) and `AdminBottomNav` both build a list of
 * [SuitNavItem] from their own tab enum and delegate rendering to
 * [SuitNavBar]. This keeps the two navigations visually related (same bar,
 * same item layout, same selected/unselected tint rule) while letting them
 * carry entirely distinct destinations/labels — Task 15 requires distinct
 * labels, not a distinct visual language.
 *
 * Density and translucency here are the customer-nav "expanded / compact /
 * translucent" states (Task 6): [SuitNavDensity.Expanded] shows icon+label at
 * the full-height bar; [SuitNavDensity.Compact] (driven by
 * [rememberSuitNavDensity] while a screen scrolls) drops the label and uses a
 * shorter bar so more content is visible while still keeping the nav
 * reachable. `translucent` is a tonal-alpha background variant with no
 * platform-specific blur.
 */
enum class SuitNavDensity { Expanded, Compact }

/** One renderable nav destination, built by a caller-owned tab enum. */
data class SuitNavItem(
    val label: String,
    val selected: Boolean,
    val onClick: () -> Unit,
    val icon: @Composable (tint: Color) -> Unit,
)

/**
 * Shared bar renderer. Height follows [SuitBottomClearance] policy
 * ([SuitBottomClearance.bottomNavExpanded] / `bottomNavCompact`) and shrinks
 * further on [com.suitup.app.ui.theme.SuitResponsiveInfo.isShortHeight] devices
 * so short phones don't lose a disproportionate share of vertical space to
 * the nav bar.
 */
@Composable
fun SuitNavBar(
    items: List<SuitNavItem>,
    modifier: Modifier = Modifier,
    density: SuitNavDensity = SuitNavDensity.Expanded,
    translucent: Boolean = false,
) {
    val responsive = SuitTheme.responsive
    val barHeight = when (density) {
        SuitNavDensity.Expanded -> if (responsive.isShortHeight) 56.dp else 64.dp
        SuitNavDensity.Compact -> 52.dp
    }
    val background = if (translucent) SuitColors.Charcoal.copy(alpha = 0.92f) else SuitColors.Charcoal
    Column(modifier = modifier.fillMaxWidth().background(background)) {
        HorizontalDivider(thickness = 1.dp, color = SuitColors.Mist)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            items.forEach { item ->
                SuitNavBarItem(item = item, density = density, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SuitNavBarItem(item: SuitNavItem, density: SuitNavDensity, modifier: Modifier = Modifier) {
    val tint = if (item.selected) SuitColors.Gold else SuitColors.Smoke
    Column(
        modifier = modifier
            .clickable(onClick = item.onClick)
            .padding(vertical = if (density == SuitNavDensity.Compact) 4.dp else 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(modifier = Modifier.padding(horizontal = 4.dp)) { item.icon(tint) }
        if (density == SuitNavDensity.Expanded) {
            Text(
                text = item.label,
                style = SuitTextStyles.labelSmall,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

enum class SuitTab(val label: String) {
    Home("Início"),
    Models("Modelos"),
    Orders("Pedidos"),
    Profile("Perfil")
}

/**
 * Customer bottom navigation. `density`/`translucent` default to the plain
 * expanded/opaque bar so the existing call site in `MainShellScreen.kt`
 * compiles unchanged; a screen opts into compact-on-scroll by passing
 * `density = rememberSuitNavDensity(...)`.
 */
@Composable
fun SuitBottomNav(
    selected: SuitTab,
    onSelect: (SuitTab) -> Unit,
    modifier: Modifier = Modifier,
    density: SuitNavDensity = SuitNavDensity.Expanded,
    translucent: Boolean = false,
) {
    val items = SuitTab.entries.map { tab ->
        val isSelected = selected == tab
        SuitNavItem(
            label = tab.label,
            selected = isSelected,
            onClick = { onSelect(tab) },
            icon = { tint ->
                when (tab) {
                    SuitTab.Home -> HomeIcon(tint = tint, filled = isSelected)
                    SuitTab.Models -> ShirtIcon(tint = tint, filled = isSelected)
                    SuitTab.Orders -> BagIcon(tint = tint, filled = isSelected)
                    SuitTab.Profile -> PersonIcon(tint = tint, filled = isSelected)
                }
            },
        )
    }
    SuitNavBar(items = items, modifier = modifier, density = density, translucent = translucent)
}
