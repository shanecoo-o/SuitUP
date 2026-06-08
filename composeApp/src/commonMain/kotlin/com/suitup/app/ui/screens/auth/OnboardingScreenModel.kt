package com.suitup.app.ui.screens.auth

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OnboardingUiState(
    val paginaActual: Int = 0,
    val totalPaginas: Int = 3,
) {
    val eUltimaPagina: Boolean get() = paginaActual >= totalPaginas - 1
}

sealed class OnboardingUiEvent {
    data class PaginaAlterada(val pagina: Int) : OnboardingUiEvent()
    data object SeguinteClicado : OnboardingUiEvent()
    data object SaltarClicado : OnboardingUiEvent()
}

class OnboardingScreenModel : ScreenModel {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _navegarParaLogin = MutableStateFlow(false)
    val navegarParaLogin: StateFlow<Boolean> = _navegarParaLogin.asStateFlow()

    fun navegacaoConsumida() { _navegarParaLogin.value = false }

    fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            is OnboardingUiEvent.PaginaAlterada ->
                _state.update { it.copy(paginaActual = event.pagina) }
            is OnboardingUiEvent.SeguinteClicado -> {
                if (_state.value.eUltimaPagina) _navegarParaLogin.value = true
                else _state.update { it.copy(paginaActual = it.paginaActual + 1) }
            }
            is OnboardingUiEvent.SaltarClicado ->
                _navegarParaLogin.value = true
        }
    }
}
