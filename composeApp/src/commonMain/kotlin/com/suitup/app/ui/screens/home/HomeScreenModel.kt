package com.suitup.app.ui.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val pedidosRecentes: List<Pedido> = emptyList(),
    val contadorCarrinho: Int = 0,
    val carregando: Boolean = false,
)

sealed class HomeUiEvent {
    data object CriarNovoFatoClicado : HomeUiEvent()
    data class PedidoClicado(val pedido: Pedido) : HomeUiEvent()
    data object VerTodosPedidosClicado : HomeUiEvent()
    data object CarrinhoClicado : HomeUiEvent()
}

class HomeScreenModel : ScreenModel {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            MockOrderStore.orders.collect { pedidos ->
                _state.update {
                    it.copy(
                        pedidosRecentes = pedidos.take(3),
                        contadorCarrinho = MockOrderStore.cartItemCount,
                    )
                }
            }
        }
    }
}
