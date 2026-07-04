package com.suitup.app.ui.screens.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.order.CustomerOrderRepository
import com.suitup.app.data.order.OrderRuntime
import com.suitup.app.data.session.AuthRuntime
import com.suitup.app.data.session.AuthSessionManager
import com.suitup.app.domain.model.AuthSessionState
import com.suitup.app.domain.model.Utilizador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val utilizador: Utilizador = MockData.utilizadorActual,
    val contadorCarrinho: Int = 0,
    val contadorPedidos: Int = 0,
    val sessaoExpirada: Boolean = false,
)

sealed class PerfilUiEvent {
    data object CarrinhoClicado : PerfilUiEvent()
    data object SairClicado : PerfilUiEvent()
}

class PerfilScreenModel(
    private val sessionManager: AuthSessionManager = AuthRuntime.sessionManager,
    private val orderRepository: CustomerOrderRepository = OrderRuntime.repository,
) : ScreenModel {

    private val _state = MutableStateFlow(PerfilUiState())
    val state: StateFlow<PerfilUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            combine(
                MockOrderStore.cart,
                orderRepository.state,
                sessionManager.state,
            ) { cart, ordersState, session -> Triple(cart, ordersState.orders, session) }
                .collect { (cart, orders, session) ->
                _state.update {
                    it.copy(
                        utilizador = (session as? AuthSessionState.Authenticated)
                            ?.account
                            ?.profile
                            ?: MockData.utilizadorActual,
                        contadorCarrinho = cart.sumOf { item -> item.quantidade },
                        contadorPedidos = orders.size,
                        sessaoExpirada = orderRepository.state.value.sessionExpired,
                    )
                }
            }
        }
        screenModelScope.launch { orderRepository.refreshOrders() }
    }

    fun sessaoExpiradaConsumida() {
        orderRepository.consumeSessionExpired()
    }
}
