package com.suitup.app.ui.screens.orders

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─── Lista de Pedidos ─────────────────────────────────────────────────────────

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
            _state.update {
                it.copy(
                    pedidos = listOf(MockData.novoPedido) + MockData.pedidosRecentes,
                    contadorCarrinho = MockData.itensCarrinho.sumOf { item -> item.quantidade },
                )
            }
        }
    }
}

// ─── Acompanhar Pedido ────────────────────────────────────────────────────────

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
            val todosPedidos = listOf(MockData.novoPedido) + MockData.pedidosRecentes
            val pedido = todosPedidos.firstOrNull { it.id == pedidoId } ?: MockData.novoPedido
            _state.update {
                it.copy(
                    pedido = pedido,
                    contadorCarrinho = MockData.itensCarrinho.sumOf { item -> item.quantidade },
                )
            }
        }
    }
}
