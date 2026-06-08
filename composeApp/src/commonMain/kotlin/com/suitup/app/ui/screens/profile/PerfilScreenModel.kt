package com.suitup.app.ui.screens.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.domain.model.Utilizador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val utilizador: Utilizador = MockData.utilizadorActual,
    val contadorCarrinho: Int = 0,
)

sealed class PerfilUiEvent {
    data object CarrinhoClicado : PerfilUiEvent()
    data object SairClicado : PerfilUiEvent()
}

class PerfilScreenModel : ScreenModel {

    private val _state = MutableStateFlow(PerfilUiState())
    val state: StateFlow<PerfilUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    utilizador = MockData.utilizadorActual,
                    contadorCarrinho = MockData.itensCarrinho.sumOf { item -> item.quantidade },
                )
            }
        }
    }
}
