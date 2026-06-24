package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.suitup.app.ui.screens.orders.AcompanharPedidoScreenModel
import com.suitup.app.ui.screens.orders.ListaPedidosScreenModel
import com.suitup.app.ui.screens.orders.OrdersListScreen
import com.suitup.app.ui.screens.orders.TrackOrderScreen

class OrdersListVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ListaPedidosScreenModel() }
        val state by screenModel.state.collectAsState()

        OrdersListScreen(
            orders = state.pedidos,
            cartItemCount = state.contadorCarrinho,
            onOrderClick = { pedido -> navigator.push(TrackOrderVoyagerScreen(pedido.id)) },
            onCartClick = { navigator.push(CartVoyagerScreen()) }
        )
    }
}

class TrackOrderVoyagerScreen(private val pedidoId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AcompanharPedidoScreenModel(pedidoId) }
        val state by screenModel.state.collectAsState()

        val pedido = state.pedido ?: return

        TrackOrderScreen(
            order = pedido,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
        )
    }
}
