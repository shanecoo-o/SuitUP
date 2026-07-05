package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.mock.toModeloFato
import com.suitup.app.ui.screens.product.ProductDetailScreen
import com.suitup.app.ui.screens.product.ProductDetailScreenModel

class ProductDetailVoyagerScreen(private val modeloId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ProductDetailScreenModel(modeloId) }
        val state by screenModel.state.collectAsState()

        ProductDetailScreen(
            model = state.model,
            isLoading = state.isLoading,
            cartItemCount = state.cartItemCount,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onCustomize = {
                state.model?.let { model ->
                    MockOrderStore.startDraft(model.toModeloFato())
                    navigator.push(Editor2DStageVoyagerScreen(model.id))
                }
            },
        )
    }
}
