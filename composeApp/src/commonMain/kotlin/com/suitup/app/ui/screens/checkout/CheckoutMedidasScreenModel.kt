package com.suitup.app.ui.screens.checkout

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.domain.model.Medidas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─── UiState ─────────────────────────────────────────────────────────────────

data class CheckoutMedidasUiState(
    val medidas: Medidas = Medidas(),
    val erro: String? = null,
    val contadorCarrinho: Int = 0,
) {
    /** True quando todos os campos obrigatórios estão preenchidos. */
    val podeContinuar: Boolean
        get() = medidas.alturaCm.isNotBlank()
            && medidas.ombrosCm.isNotBlank()
            && medidas.peitoCm.isNotBlank()
            && medidas.cinturaCm.isNotBlank()
            && medidas.mangaCm.isNotBlank()
            && medidas.calcaCm.isNotBlank()
}

// ─── UiEvent ─────────────────────────────────────────────────────────────────

sealed class CheckoutMedidasUiEvent {
    data class AlturaAlterada(val valor: String) : CheckoutMedidasUiEvent()
    data class PesoAlterado(val valor: String) : CheckoutMedidasUiEvent()
    data class OmbrosAlterados(val valor: String) : CheckoutMedidasUiEvent()
    data class PeitoAlterado(val valor: String) : CheckoutMedidasUiEvent()
    data class CinturaAlterada(val valor: String) : CheckoutMedidasUiEvent()
    data class QuadrilAlterado(val valor: String) : CheckoutMedidasUiEvent()
    data class MangaAlterada(val valor: String) : CheckoutMedidasUiEvent()
    data class CalcaAlterada(val valor: String) : CheckoutMedidasUiEvent()
    data class CasacoAlterado(val valor: String) : CheckoutMedidasUiEvent()
    data class PescocoAlterado(val valor: String) : CheckoutMedidasUiEvent()
    data class ObservacoesAlteradas(val valor: String) : CheckoutMedidasUiEvent()
    data object ContinuarClicado : CheckoutMedidasUiEvent()
}

// ─── ScreenModel ─────────────────────────────────────────────────────────────

/**
 * ScreenModel do ecrã Checkout · Medidas do Cliente.
 *
 * @param usarMedidasGuardadas Se true, pré-preenche com as medidas guardadas
 *   do utilizador actual. Se false, inicia com campos vazios.
 *
 * Validação: exige alturaCm, ombrosCm, peitoCm, cinturaCm, mangaCm, calcaCm.
 * Step 4: persistir medidas no pedido antes de avançar via PedidoRepository.
 */
class CheckoutMedidasScreenModel(
    private val usarMedidasGuardadas: Boolean = false,
) : ScreenModel {

    private val _state = MutableStateFlow(CheckoutMedidasUiState())
    val state: StateFlow<CheckoutMedidasUiState> = _state.asStateFlow()

    private val _podeAvancar = MutableStateFlow(false)
    val podeAvancar: StateFlow<Boolean> = _podeAvancar.asStateFlow()

    fun avancarConsumido() { _podeAvancar.value = false }

    init {
        screenModelScope.launch {
            val medidasIniciais = if (usarMedidasGuardadas) {
                MockData.utilizadorActual.medidasGuardadas ?: Medidas()
            } else {
                Medidas()
            }
            _state.update {
                it.copy(
                    medidas = medidasIniciais,
                    contadorCarrinho = MockOrderStore.cartItemCount,
                )
            }
        }
    }

    fun onEvent(event: CheckoutMedidasUiEvent) {
        when (event) {
            is CheckoutMedidasUiEvent.AlturaAlterada ->
                actualizarMedida { it.copy(alturaCm = event.valor) }
            is CheckoutMedidasUiEvent.PesoAlterado ->
                actualizarMedida { it.copy(pesoKg = event.valor) }
            is CheckoutMedidasUiEvent.OmbrosAlterados ->
                actualizarMedida { it.copy(ombrosCm = event.valor) }
            is CheckoutMedidasUiEvent.PeitoAlterado ->
                actualizarMedida { it.copy(peitoCm = event.valor) }
            is CheckoutMedidasUiEvent.CinturaAlterada ->
                actualizarMedida { it.copy(cinturaCm = event.valor) }
            is CheckoutMedidasUiEvent.QuadrilAlterado ->
                actualizarMedida { it.copy(quadrilCm = event.valor) }
            is CheckoutMedidasUiEvent.MangaAlterada ->
                actualizarMedida { it.copy(mangaCm = event.valor) }
            is CheckoutMedidasUiEvent.CalcaAlterada ->
                actualizarMedida { it.copy(calcaCm = event.valor) }
            is CheckoutMedidasUiEvent.CasacoAlterado ->
                actualizarMedida { it.copy(casacoCm = event.valor) }
            is CheckoutMedidasUiEvent.PescocoAlterado ->
                actualizarMedida { it.copy(pescocoCm = event.valor) }
            is CheckoutMedidasUiEvent.ObservacoesAlteradas ->
                actualizarMedida { it.copy(observacoes = event.valor) }
            is CheckoutMedidasUiEvent.ContinuarClicado -> validarEAvancar()
        }
    }

    private fun actualizarMedida(bloco: (Medidas) -> Medidas) {
        _state.update { s -> s.copy(medidas = bloco(s.medidas), erro = null) }
    }

    private fun validarEAvancar() {
        if (_state.value.podeContinuar) {
            _state.update { it.copy(erro = null) }
            MockOrderStore.updateCheckoutMeasurements(_state.value.medidas)
            _podeAvancar.value = true
        } else {
            _state.update {
                it.copy(erro = "Preencha as medidas obrigatórias antes de continuar.")
            }
        }
    }
}
