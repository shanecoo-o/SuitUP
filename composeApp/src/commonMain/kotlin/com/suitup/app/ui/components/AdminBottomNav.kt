package com.suitup.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.suitup.app.ui.icons.BagIcon
import com.suitup.app.ui.icons.BellIcon
import com.suitup.app.ui.icons.HomeIcon
import com.suitup.app.ui.icons.ShirtIcon

/**
 * Admin bottom navigation (Phase 9.3, Task 15).
 *
 * Distinct destinations/labels from the customer [SuitTab] set — Visão Geral
 * / Gestão / Operações / Actividade — sharing [SuitNavBar] as the rendering
 * engine so the two navigations read as one visual system without
 * duplicating layout code. Zero new icons: reuses the existing
 * [com.suitup.app.ui.icons] set (Home/Shirt/Bag/Bell), matching each
 * destination's closest existing semantic.
 *
 * This is standalone, ready-to-integrate infrastructure. It is deliberately
 * NOT wired into a real Admin `TabNavigator`/shell this phase: "Actividade"
 * has no backing screen yet, and grouping the existing Catalog/Orders/
 * Payments screens into "Gestão"/"Operações" would be a feature-screen
 * restructure — both out of scope per this phase's mandate (see
 * `UI_PHASE_9_3_NAVIGATION_REPORT.md`, Remaining Risks).
 */
enum class AdminTab(val label: String) {
    Overview("Visão Geral"),
    Management("Gestão"),
    Operations("Operações"),
    Activity("Actividade"),
}

@Composable
fun AdminBottomNav(
    selected: AdminTab,
    onSelect: (AdminTab) -> Unit,
    modifier: Modifier = Modifier,
    density: SuitNavDensity = SuitNavDensity.Expanded,
    translucent: Boolean = false,
) {
    val items = AdminTab.entries.map { tab ->
        val isSelected = selected == tab
        SuitNavItem(
            label = tab.label,
            selected = isSelected,
            onClick = { onSelect(tab) },
            icon = { tint ->
                when (tab) {
                    AdminTab.Overview -> HomeIcon(tint = tint, filled = isSelected)
                    AdminTab.Management -> ShirtIcon(tint = tint, filled = isSelected)
                    AdminTab.Operations -> BagIcon(tint = tint, filled = isSelected)
                    AdminTab.Activity -> BellIcon(tint = tint)
                }
            },
        )
    }
    SuitNavBar(items = items, modifier = modifier, density = density, translucent = translucent)
}
