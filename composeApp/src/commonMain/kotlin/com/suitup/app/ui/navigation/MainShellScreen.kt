package com.suitup.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.suitup.app.data.session.AuthRuntime
import com.suitup.app.ui.components.SuitBottomNav
import com.suitup.app.ui.components.SuitTab
import com.suitup.app.ui.theme.SuitColors
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

        CompositionLocalProvider(LocalSignOut provides signOut) {
            TabNavigator(HomeTab) { tabNavigator ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SuitColors.Bone)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        tabNavigator.current.Content()
                    }

                    SuitBottomNav(
                        selected = currentSuitTab(tabNavigator),
                        onSelect = { tab -> tabNavigator.current = tab.toVoyagerTab() }
                    )
                }
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
