package com.suitup.app.ui.screens.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.data.mock.MockOrderStore
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
)

sealed class PerfilUiEvent {
    data object CarrinhoClicado : PerfilUiEvent()
    data object SairClicado : PerfilUiEvent()
}

class PerfilScreenModel(
    private val sessionManager: AuthSessionManager = AuthRuntime.sessionManager,
) : ScreenModel {

    private val _state = MutableStateFlow(PerfilUiState())
    val state: StateFlow<PerfilUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            combine(
                MockOrderStore.cart,
                MockOrderStore.orders,
                sessionManager.state,
            ) { cart, orders, session -> Triple(cart, orders, session) }
                .collect { (cart, orders, session) ->
                _state.update {
                    it.copy(
                        utilizador = (session as? AuthSessionState.Authenticated)
                            ?.account
                            ?.profile
                            ?: MockData.utilizadorActual,
                        contadorCarrinho = cart.sumOf { item -> item.quantidade },
                        contadorPedidos = orders.size,
                    )
                }
            }
        }
    }
}
