package com.suitup.app.ui.screens.cart

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.domain.model.ItemCarrinho
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CarrinhoUiState(
    val itens: List<ItemCarrinho> = emptyList(),
    val taxaEntrega: Int = 0,
    val carregando: Boolean = false,
) {
    val subtotal: Int get() = itens.sumOf { it.precoUnitarioMt * it.quantidade }
    val total: Int get() = subtotal + taxaEntrega
    val contadorCarrinho: Int get() = itens.sumOf { it.quantidade }
    val carrinhoVazio: Boolean get() = itens.isEmpty()
}

sealed class CarrinhoUiEvent {
    data class QuantidadeAlterada(val itemId: String, val quantidade: Int) : CarrinhoUiEvent()
    data class RemoverItem(val itemId: String) : CarrinhoUiEvent()
    data object FinalizarPedidoClicado : CarrinhoUiEvent()
}

class CarrinhoScreenModel : ScreenModel {

    private val _state = MutableStateFlow(CarrinhoUiState())
    val state: StateFlow<CarrinhoUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            MockOrderStore.cart.collect { items ->
                _state.update {
                    it.copy(
                        itens = items,
                        taxaEntrega = MockData.taxaEntregaMt,
                    )
                }
            }
        }
    }

    fun onEvent(event: CarrinhoUiEvent) {
        when (event) {
            is CarrinhoUiEvent.QuantidadeAlterada -> {
                MockOrderStore.updateQuantity(event.itemId, event.quantidade)
                refresh()
            }
            is CarrinhoUiEvent.RemoverItem -> {
                MockOrderStore.removeItem(event.itemId)
                refresh()
            }
            is CarrinhoUiEvent.FinalizarPedidoClicado -> refresh()
        }
    }

    private fun refresh() {
        _state.update {
            it.copy(
                itens = MockOrderStore.cartItems,
                taxaEntrega = MockData.taxaEntregaMt,
            )
        }
    }
}
