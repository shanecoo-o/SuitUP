package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.suitup.app.data.session.AuthRuntime
import com.suitup.app.ui.components.SuitBottomNav
import com.suitup.app.ui.components.SuitNavDensity
import com.suitup.app.ui.components.SuitPrimaryDestinationScaffold
import com.suitup.app.ui.components.SuitTab
import kotlinx.coroutines.launch

/**
 * Main Shell — depois do login.
 * TabNavigator com 4 tabs (Home/Models/Orders/Profile) + bottom nav persistente.
 *
 * Cada Tab encapsula um Navigator próprio (stack independente por tab).
 * Expõe LocalSignOut via CompositionLocalProvider para que o Profile (que está
 * dentro de um Navigator interno) consiga fazer logout no Navigator outer.
 */
class MainShellScreen : Screen {

    @Composable
    override fun Content() {
        // outerNavigator = o Navigator que está acima do MainShellScreen (do App()).
        // É este que precisa receber o replaceAll(SplashVoyagerScreen()) no logout.
        val outerNavigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()

        val signOut: () -> Unit = {
            coroutineScope.launch {
                AuthRuntime.sessionManager.logout()
                outerNavigator.replaceAll(LoginVoyagerScreen())
            }
        }

        // Phase 9.4: shared holder so a tab-root screen's own
        // `rememberSuitNavDensity(...)` (Home's LazyColumn, Catalog's
        // LazyVerticalGrid) can drive this bottom nav — see LocalSuitNavDensity's
        // doc comment in NavLocals.kt for why this indirection exists.
        val navDensity = remember { mutableStateOf(SuitNavDensity.Expanded) }

        CompositionLocalProvider(LocalSignOut provides signOut, LocalSuitNavDensity provides navDensity) {
            TabNavigator(HomeTab) { tabNavigator ->
                val density by navDensity
                SuitPrimaryDestinationScaffold(
                    bottomNav = {
                        SuitBottomNav(
                            selected = currentSuitTab(tabNavigator),
                            onSelect = { tab -> tabNavigator.current = tab.toVoyagerTab() },
                            density = density,
                        )
                    },
                    content = { tabNavigator.current.Content() },
                )
            }
        }
    }
}

/** Mapping SuitTab → Voyager Tab. */
private fun SuitTab.toVoyagerTab(): Tab = when (this) {
    SuitTab.Home -> HomeTab
    SuitTab.Models -> ModelsTab
    SuitTab.Orders -> OrdersTab
    SuitTab.Profile -> ProfileTab
}

/** Mapping inverso: Voyager Tab atual → SuitTab para o BottomNav saber qual destacar. */
private fun currentSuitTab(tabNavigator: TabNavigator): SuitTab {
    return when (tabNavigator.current) {
        HomeTab -> SuitTab.Home
        ModelsTab -> SuitTab.Models
        OrdersTab -> SuitTab.Orders
        ProfileTab -> SuitTab.Profile
        else -> SuitTab.Home
    }
}
