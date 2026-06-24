package com.suitup.app.ui.screens.orders

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockOrderStore
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
)

sealed class ListaPedidosUiEvent {
    data class PedidoClicado(val pedido: Pedido) : ListaPedidosUiEvent()
    data object CarrinhoClicado : ListaPedidosUiEvent()
}

class ListaPedidosScreenModel : ScreenModel {

    private val _state = MutableStateFlow(ListaPedidosUiState())
    val state: StateFlow<ListaPedidosUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            combine(MockOrderStore.orders, MockOrderStore.cart) { pedidos, cart ->
                pedidos to cart
            }.collect { (pedidos, cart) ->
                _state.update {
                    it.copy(
                        pedidos = pedidos,
                        contadorCarrinho = cart.sumOf { item -> item.quantidade },
                    )
                }
            }
        }
    }
}

data class AcompanharPedidoUiState(
    val pedido: Pedido? = null,
    val carregando: Boolean = false,
    val contadorCarrinho: Int = 0,
)

sealed class AcompanharPedidoUiEvent {
    data object SuporteClicado : AcompanharPedidoUiEvent()
    data object CarrinhoClicado : AcompanharPedidoUiEvent()
}

class AcompanharPedidoScreenModel(private val pedidoId: String) : ScreenModel {

    private val _state = MutableStateFlow(AcompanharPedidoUiState())
    val state: StateFlow<AcompanharPedidoUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            combine(MockOrderStore.orders, MockOrderStore.cart) { pedidos, cart ->
                pedidos to cart
            }.collect { (pedidos, cart) ->
                _state.update {
                    it.copy(
                        pedido = pedidos.firstOrNull { pedido -> pedido.id == pedidoId } ?: pedidos.firstOrNull(),
                        contadorCarrinho = cart.sumOf { item -> item.quantidade },
                    )
                }
            }
        }
    }
}
