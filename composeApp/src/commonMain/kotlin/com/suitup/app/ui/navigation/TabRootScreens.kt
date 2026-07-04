package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.suitup.app.data.mock.MockData
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.ui.screens.cart.CartScreen
import com.suitup.app.ui.screens.cart.CarrinhoScreenModel
import com.suitup.app.ui.screens.cart.CarrinhoUiEvent
import com.suitup.app.ui.screens.catalog.SelectModelScreen
import com.suitup.app.ui.screens.catalog.SelecionarModeloScreenModel
import com.suitup.app.ui.screens.catalog.SelecionarModeloUiEvent
import com.suitup.app.ui.screens.home.HomeScreen
import com.suitup.app.ui.screens.home.HomeScreenModel
import com.suitup.app.ui.screens.profile.ProfileScreen
import com.suitup.app.ui.screens.profile.PerfilScreenModel

class HomeVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val signOut = LocalSignOut.current
        val screenModel = rememberScreenModel { HomeScreenModel() }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.sessaoExpirada) {
            if (state.sessaoExpirada) {
                screenModel.sessaoExpiradaConsumida()
                signOut()
            }
        }

        HomeScreen(
            pedidosRecentes = state.pedidosRecentes,
            cartItemCount = state.contadorCarrinho,
            featuredModels = state.modelosDestaque,
            userName = MockData.utilizadorActual.nome,
            onCreateNewSuit = { tabNavigator.current = ModelsTab },
            onFeaturedModelClick = { model ->
                MockOrderStore.startDraft(model)
                navigator.push(Editor2DPartsVoyagerScreen(model.id))
            },
            onOrderClick = { pedido -> navigator.push(TrackOrderVoyagerScreen(pedido.id)) },
            onSeeAllOrders = { tabNavigator.current = OrdersTab },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onProfileClick = { tabNavigator.current = ProfileTab },
        )
    }
}

class CartVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val screenModel = rememberScreenModel { CarrinhoScreenModel() }
        val state by screenModel.state.collectAsState()

        CartScreen(
            items = state.itens,
            taxaEntregaMt = state.taxaEntrega,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = {},
            onItemRemove = { item -> screenModel.onEvent(CarrinhoUiEvent.RemoverItem(item.id)) },
            onQuantityChange = { item, qty ->
                screenModel.onEvent(CarrinhoUiEvent.QuantidadeAlterada(item.id, qty))
            },
            onItemEdit = { navigator.pop() },
            onCheckout = {
                screenModel.onEvent(CarrinhoUiEvent.FinalizarPedidoClicado)
                MockOrderStore.beginCheckout()
                navigator.push(CheckoutVoyagerScreen())
            },
            onContinueShopping = {
                navigator.popUntilRoot()
                tabNavigator.current = ModelsTab
            }
        )
    }
}

class SelectModelVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SelecionarModeloScreenModel() }
        val state by screenModel.state.collectAsState()

        SelectModelScreen(
            models = state.modelos,
            selectedCategory = state.categoriaSeleccionada,
            cartItemCount = state.contadorCarrinho,
            isLoading = state.carregando,
            errorMessage = state.erroCatalogo,
            isUsingMockFallback = state.usandoFallbackMock,
            onCategorySelect = { screenModel.onEvent(SelecionarModeloUiEvent.CategoriaSeleccionada(it)) },
            onModelClick = { modelo ->
                screenModel.onEvent(SelecionarModeloUiEvent.ModeloClicado(modelo))
                navigator.push(Editor2DPartsVoyagerScreen(modelo.id))
            },
            onRetry = { screenModel.onEvent(SelecionarModeloUiEvent.TentarNovamente) },
            onCartClick = { navigator.push(CartVoyagerScreen()) }
        )
    }
}

class ProfileVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val signOut = LocalSignOut.current
        val screenModel = rememberScreenModel { PerfilScreenModel() }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.sessaoExpirada) {
            if (state.sessaoExpirada) {
                screenModel.sessaoExpiradaConsumida()
                signOut()
            }
        }

        ProfileScreen(
            user = state.utilizador,
            cartItemCount = state.contadorCarrinho,
            orderCount = state.contadorPedidos,
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onOrders = { tabNavigator.current = OrdersTab },
            onSignOut = signOut
        )
    }
}
