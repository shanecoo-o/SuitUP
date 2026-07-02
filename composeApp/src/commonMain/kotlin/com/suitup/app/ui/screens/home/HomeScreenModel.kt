package com.suitup.app.ui.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.catalog.CatalogRuntime
import com.suitup.app.data.catalog.CustomerCatalogRepository
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.order.CustomerOrderRepository
import com.suitup.app.data.order.OrderRuntime
import com.suitup.app.data.mock.toModeloFato
import com.suitup.app.domain.model.ModeloFato
import com.suitup.app.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val pedidosRecentes: List<Pedido> = emptyList(),
    val modelosDestaque: List<ModeloFato> = emptyList(),
    val contadorCarrinho: Int = 0,
    val carregando: Boolean = false,
    val sessaoExpirada: Boolean = false,
)

sealed class HomeUiEvent {
    data object CriarNovoFatoClicado : HomeUiEvent()
    data class PedidoClicado(val pedido: Pedido) : HomeUiEvent()
    data object VerTodosPedidosClicado : HomeUiEvent()
    data object CarrinhoClicado : HomeUiEvent()
}

class HomeScreenModel(
    private val catalogRepository: CustomerCatalogRepository = CatalogRuntime.repository,
    private val orderRepository: CustomerOrderRepository = OrderRuntime.repository,
) : ScreenModel {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            combine(
                orderRepository.state,
                MockOrderStore.cart,
                catalogRepository.state,
            ) { ordersState, cart, catalog ->
                Triple(ordersState.orders, cart, catalog)
            }.collect { (pedidos, cart, catalog) ->
                _state.update {
                    it.copy(
                        pedidosRecentes = pedidos.take(3),
                        modelosDestaque = catalog.models
                            .take(4)
                            .map { model -> model.toModeloFato() },
                        contadorCarrinho = cart.sumOf { item -> item.quantidade },
                        carregando = catalog.isLoading,
                        sessaoExpirada = orderRepository.state.value.sessionExpired,
                    )
                }
            }
        }
        screenModelScope.launch { catalogRepository.refresh() }
        screenModelScope.launch { orderRepository.refreshOrders() }
    }

    fun sessaoExpiradaConsumida() {
        orderRepository.consumeSessionExpired()
    }
}
