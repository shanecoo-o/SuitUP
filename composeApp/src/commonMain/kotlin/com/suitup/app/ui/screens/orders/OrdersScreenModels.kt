package com.suitup.app.ui.screens.orders

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.order.CustomerOrderRepository
import com.suitup.app.data.order.OrderRuntime
import com.suitup.app.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListaPedidosUiState(
    val pedidos: List<Pedido> = emptyList(),
    val carregando: Boolean = false,
    val contadorCarrinho: Int = 0,
    val erro: String? = null,
    val usandoFallbackMock: Boolean = false,
    val sessaoExpirada: Boolean = false,
)

sealed class ListaPedidosUiEvent {
    data class PedidoClicado(val pedido: Pedido) : ListaPedidosUiEvent()
    data object CarrinhoClicado : ListaPedidosUiEvent()
    data object TentarNovamente : ListaPedidosUiEvent()
}

class ListaPedidosScreenModel(
    private val repository: CustomerOrderRepository = OrderRuntime.repository,
) : ScreenModel {

    private val _state = MutableStateFlow(ListaPedidosUiState())
    val state: StateFlow<ListaPedidosUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            combine(repository.state, MockOrderStore.cart) { ordersState, cart ->
                ordersState to cart
            }.collect { (ordersState, cart) ->
                _state.update {
                    it.copy(
                        pedidos = ordersState.orders,
                        carregando = ordersState.isLoading,
                        contadorCarrinho = cart.sumOf { item -> item.quantidade },
                        erro = ordersState.errorMessage,
                        usandoFallbackMock = ordersState.isUsingMockFallback,
                        sessaoExpirada = ordersState.sessionExpired,
                    )
                }
            }
        }
        refresh()
    }

    fun onEvent(event: ListaPedidosUiEvent) {
        if (event is ListaPedidosUiEvent.TentarNovamente) refresh()
    }

    fun refresh() {
        screenModelScope.launch { repository.refreshOrders() }
    }

    fun sessaoExpiradaConsumida() {
        repository.consumeSessionExpired()
    }
}

data class AcompanharPedidoUiState(
    val pedido: Pedido? = null,
    val carregando: Boolean = false,
    val contadorCarrinho: Int = 0,
    val erro: String? = null,
    val usandoFallbackMock: Boolean = false,
    val sessaoExpirada: Boolean = false,
)

sealed class AcompanharPedidoUiEvent {
    data object SuporteClicado : AcompanharPedidoUiEvent()
    data object CarrinhoClicado : AcompanharPedidoUiEvent()
}

class AcompanharPedidoScreenModel(
    private val pedidoId: String,
    private val repository: CustomerOrderRepository = OrderRuntime.repository,
) : ScreenModel {

    private val _state = MutableStateFlow(AcompanharPedidoUiState())
    val state: StateFlow<AcompanharPedidoUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            MockOrderStore.cart.collect { cart ->
                _state.update { it.copy(contadorCarrinho = cart.sumOf { item -> item.quantidade }) }
            }
        }
        refresh()
    }

    fun refresh() {
        screenModelScope.launch {
            _state.update { it.copy(carregando = true, erro = null) }
            val result = repository.getOrder(pedidoId)
            _state.update {
                it.copy(
                    pedido = result.order,
                    carregando = false,
                    erro = result.errorMessage,
                    usandoFallbackMock = result.isUsingMockFallback,
                    sessaoExpirada = result.sessionExpired,
                )
            }
        }
    }

    fun sessaoExpiradaConsumida() {
        _state.update { it.copy(sessaoExpirada = false) }
    }
}
