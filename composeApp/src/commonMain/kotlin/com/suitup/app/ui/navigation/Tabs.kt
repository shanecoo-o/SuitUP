package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition

/**
 * As 4 Tabs do bottom nav. Cada uma é um `Tab` Voyager que contém um `Navigator`
 * interno — isto dá stack independente por tab (mudar de tab e voltar mantém
 * o ecrã onde estavas).
 *
 * As `TabOptions` (título/icon) não são usadas — o SuitBottomNav tem look próprio.
 */

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 0u, title = "Início")

    @Composable
    override fun Content() {
        Navigator(HomeVoyagerScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

object ModelsTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 1u, title = "Modelos")

    @Composable
    override fun Content() {
        Navigator(SelectModelVoyagerScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

object OrdersTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 2u, title = "Pedidos")

    @Composable
    override fun Content() {
        Navigator(OrdersListVoyagerScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 3u, title = "Perfil")

    @Composable
    override fun Content() {
        Navigator(ProfileVoyagerScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
