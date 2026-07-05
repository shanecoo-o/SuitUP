package com.suitup.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.suitup.app.ui.components.ErrorStateCard
import com.suitup.app.ui.components.SuitContentLoading
import com.suitup.app.ui.components.SuitDetailScaffold
import com.suitup.app.ui.components.SuitDetailTopBar
import com.suitup.app.ui.screens.orders.AcompanharPedidoScreenModel
import com.suitup.app.ui.screens.orders.ListaPedidosScreenModel
import com.suitup.app.ui.screens.orders.ListaPedidosUiEvent
import com.suitup.app.ui.screens.orders.OrdersListScreen
import com.suitup.app.ui.screens.orders.TrackOrderScreen

class OrdersListVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val signOut = LocalSignOut.current
        val screenModel = rememberScreenModel { ListaPedidosScreenModel() }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.sessaoExpirada) {
            if (state.sessaoExpirada) {
                screenModel.sessaoExpiradaConsumida()
                signOut()
            }
        }

        OrdersListScreen(
            orders = state.pedidos,
            cartItemCount = state.contadorCarrinho,
            isLoading = state.carregando,
            errorMessage = state.erro,
            isUsingMockFallback = state.usandoFallbackMock,
            onOrderClick = { pedido -> navigator.push(TrackOrderVoyagerScreen(pedido.id)) },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onExploreCatalog = { tabNavigator.current = ModelsTab },
            onRetry = { screenModel.onEvent(ListaPedidosUiEvent.TentarNovamente) },
        )
    }
}

class TrackOrderVoyagerScreen(private val pedidoId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val signOut = LocalSignOut.current
        val screenModel = rememberScreenModel { AcompanharPedidoScreenModel(pedidoId) }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.sessaoExpirada) {
            if (state.sessaoExpirada) {
                screenModel.sessaoExpiradaConsumida()
                signOut()
            }
        }

        val pedido = state.pedido
        if (pedido == null) {
            SuitDetailScaffold(
                topBar = { SuitDetailTopBar(title = "Detalhes do pedido", onBack = { navigator.pop() }) },
            ) {
                if (state.carregando) {
                    SuitContentLoading(
                        modifier = Modifier.fillMaxSize(),
                        message = "A carregar pedido...",
                    )
                } else {
                    ErrorStateCard(
                        title = state.erro ?: "Pedido não encontrado.",
                        description = "Verifique a ligação e tente novamente.",
                        retryLabel = "Tentar novamente",
                        onRetry = screenModel::refresh,
                        modifier = Modifier.padding(24.dp),
                    )
                }
            }
        } else {
            TrackOrderScreen(
                order = pedido,
                cartItemCount = state.contadorCarrinho,
                backendTimeline = state.timeline,
                isTimelineLoading = state.carregandoTimeline,
                timelineError = state.erroTimeline,
                noticeMessage = if (state.usandoFallbackMock) {
                    "A mostrar dados locais em modo demo."
                } else null,
                onBack = { navigator.pop() },
                onCartClick = { navigator.push(CartVoyagerScreen()) },
                onRetryTimeline = screenModel::refresh,
            )
        }
    }
}
