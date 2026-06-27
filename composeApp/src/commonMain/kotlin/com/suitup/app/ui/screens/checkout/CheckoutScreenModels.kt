package com.suitup.app.ui.screens.checkout

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.domain.model.EnderecoEntrega
import com.suitup.app.domain.model.PontoLevantamento
import com.suitup.app.domain.model.TipoEntrega
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─── Checkout Dados ───────────────────────────────────────────────────────────

data class CheckoutUiState(
    val nomeCompleto: String = "",
    val telefone: String = "",
    val email: String = "",
    val usarMedidasGuardadas: Boolean = false,
    val erroNome: String? = null,
    val erroTelefone: String? = null,
    val erroEmail: String? = null,
    val contadorCarrinho: Int = 0,
)

sealed class CheckoutUiEvent {
    data class NomeAlterado(val valor: String) : CheckoutUiEvent()
    data class TelefoneAlterado(val valor: String) : CheckoutUiEvent()
    data class EmailAlterado(val valor: String) : CheckoutUiEvent()
    data class MedidasGuardadasAlteradas(val usar: Boolean) : CheckoutUiEvent()
    data object ContinuarClicado : CheckoutUiEvent()
}

class CheckoutScreenModel : ScreenModel {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    private val _podeAvancar = MutableStateFlow(false)
    val podeAvancar: StateFlow<Boolean> = _podeAvancar.asStateFlow()

    fun avancarConsumido() { _podeAvancar.value = false }

    init {
        screenModelScope.launch {
            val u = MockData.utilizadorActual
            _state.update {
                it.copy(
                    nomeCompleto = u.nome,
                    telefone = u.telefone,
                    email = u.email,
                    contadorCarrinho = MockOrderStore.cartItemCount,
                )
            }
        }
    }

    fun onEvent(event: CheckoutUiEvent) {
        when (event) {
            is CheckoutUiEvent.NomeAlterado ->
                _state.update { it.copy(nomeCompleto = event.valor, erroNome = null) }
            is CheckoutUiEvent.TelefoneAlterado ->
                _state.update { it.copy(telefone = event.valor, erroTelefone = null) }
            is CheckoutUiEvent.EmailAlterado ->
                _state.update { it.copy(email = event.valor, erroEmail = null) }
            is CheckoutUiEvent.MedidasGuardadasAlteradas ->
                _state.update { it.copy(usarMedidasGuardadas = event.usar) }
            is CheckoutUiEvent.ContinuarClicado -> validarEAvancar()
        }
    }

    private fun validarEAvancar() {
        val s = _state.value
        val erroNome = if (s.nomeCompleto.isBlank()) "Nome obrigatório" else null
        val erroTel = if (s.telefone.isBlank()) "Telefone obrigatório" else null
        val erroEmail = if (s.email.isBlank()) "Email obrigatório" else null
        _state.update { it.copy(erroNome = erroNome, erroTelefone = erroTel, erroEmail = erroEmail) }
        if (erroNome == null && erroTel == null && erroEmail == null) {
            MockOrderStore.updateCheckoutCustomer(
                nome = s.nomeCompleto,
                email = s.email,
                telefone = s.telefone,
            )
            _podeAvancar.value = true
        }
    }
}

// ─── Tipo de Entrega ──────────────────────────────────────────────────────────

data class TipoEntregaUiState(
    val tipoSeleccionado: TipoEntrega = TipoEntrega.Entrega,
    val contadorCarrinho: Int = 0,
)

sealed class TipoEntregaUiEvent {
    data class TipoSeleccionado(val tipo: TipoEntrega) : TipoEntregaUiEvent()
}

class TipoEntregaScreenModel : ScreenModel {

    private val _state = MutableStateFlow(TipoEntregaUiState())
    val state: StateFlow<TipoEntregaUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            _state.update { it.copy(contadorCarrinho = MockOrderStore.cartItemCount) }
        }
    }

    fun onEvent(event: TipoEntregaUiEvent) {
        when (event) {
            is TipoEntregaUiEvent.TipoSeleccionado ->
                _state.update { it.copy(tipoSeleccionado = event.tipo) }.also {
                    MockOrderStore.updateCheckoutDeliveryType(event.tipo)
                }
        }
    }
}

// ─── Endereço / Pickup ────────────────────────────────────────────────────────

data class EnderecoUiState(
    val modo: TipoEntrega = TipoEntrega.Entrega,
    val endereco: EnderecoEntrega = EnderecoEntrega("Maputo", "Polana", "Av. Julius Nyerere, 123", "Próximo ao Shopping Polana"),
    val cidadesDisponiveis: List<String> = emptyList(),
    val bairrosDisponiveis: List<String> = emptyList(),
    val pontosLevantamento: List<PontoLevantamento> = emptyList(),
    val pontoSeleccionado: PontoLevantamento? = null,
    val contadorCarrinho: Int = 0,
    val erroPonto: String? = null,
    val erroEndereco: String? = null,
)

sealed class EnderecoUiEvent {
    data class ModoAlterado(val modo: TipoEntrega) : EnderecoUiEvent()
    data class CidadeAlterada(val valor: String) : EnderecoUiEvent()
    data class BairroAlterado(val valor: String) : EnderecoUiEvent()
    data class RuaAlterada(val valor: String) : EnderecoUiEvent()
    data class ReferenciaAlterada(val valor: String) : EnderecoUiEvent()
    data class PontoSeleccionado(val ponto: PontoLevantamento) : EnderecoUiEvent()
    data object ContinuarClicado : EnderecoUiEvent()
}

class EnderecoScreenModel(private val modoInicial: TipoEntrega) : ScreenModel {

    private val _state = MutableStateFlow(EnderecoUiState(modo = modoInicial))
    val state: StateFlow<EnderecoUiState> = _state.asStateFlow()

    private val _podeAvancar = MutableStateFlow(false)
    val podeAvancar: StateFlow<Boolean> = _podeAvancar.asStateFlow()

    fun avancarConsumido() { _podeAvancar.value = false }

    init {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    cidadesDisponiveis = MockData.cidadesMocambicanas,
                    bairrosDisponiveis = MockData.bairrosMaputo,
                    pontosLevantamento = MockData.pontosLevantamento,
                    contadorCarrinho = MockOrderStore.cartItemCount,
                )
            }
        }
    }

    fun onEvent(event: EnderecoUiEvent) {
        when (event) {
            is EnderecoUiEvent.ModoAlterado ->
                _state.update { it.copy(modo = event.modo, erroPonto = null, erroEndereco = null) }
            is EnderecoUiEvent.CidadeAlterada ->
                _state.update { it.copy(endereco = it.endereco.copy(cidade = event.valor)) }
            is EnderecoUiEvent.BairroAlterado ->
                _state.update { it.copy(endereco = it.endereco.copy(bairro = event.valor)) }
            is EnderecoUiEvent.RuaAlterada ->
                _state.update { it.copy(endereco = it.endereco.copy(rua = event.valor)) }
            is EnderecoUiEvent.ReferenciaAlterada ->
                _state.update { it.copy(endereco = it.endereco.copy(referencia = event.valor)) }
            is EnderecoUiEvent.PontoSeleccionado ->
                _state.update { it.copy(pontoSeleccionado = event.ponto, erroPonto = null) }
            is EnderecoUiEvent.ContinuarClicado -> validarEAvancar()
        }
    }

    private fun validarEAvancar() {
        val s = _state.value
        when (s.modo) {
            TipoEntrega.Entrega -> {
                val erro = if (s.endereco.rua.isBlank()) "Morada obrigatória" else null
                _state.update { it.copy(erroEndereco = erro) }
                if (erro == null) {
                    MockOrderStore.updateCheckoutDelivery(
                        tipoEntrega = s.modo,
                        enderecoEntrega = s.endereco,
                        pontoLevantamento = null,
                    )
                    _podeAvancar.value = true
                }
            }
            TipoEntrega.Levantamento -> {
                val erro = if (s.pontoSeleccionado == null) "Seleccione um ponto de levantamento" else null
                _state.update { it.copy(erroPonto = erro) }
                if (erro == null) {
                    MockOrderStore.updateCheckoutDelivery(
                        tipoEntrega = s.modo,
                        enderecoEntrega = null,
                        pontoLevantamento = s.pontoSeleccionado,
                    )
                    _podeAvancar.value = true
                }
            }
        }
    }
}

// ─── Pagamento M-Pesa ─────────────────────────────────────────────────────────

data class PagamentoUiState(
    val numeroMpesa: String = "",
    val titularMpesa: String = "",
    val nomeFicheiroCarregado: String? = null,
    val numeroPedidoCriado: String? = null,
    val totalPedidoMt: Int = 0,
    val contadorCarrinho: Int = 0,
) {
    val podeEnviar: Boolean get() = nomeFicheiroCarregado != null
}

sealed class PagamentoUiEvent {
    data object EscolherFicheiroClicado : PagamentoUiEvent()
    data object RemoverFicheiroClicado : PagamentoUiEvent()
    data object CopiarNumeroClicado : PagamentoUiEvent()
    data object EnviarComprovativoClicado : PagamentoUiEvent()
}

class PagamentoScreenModel : ScreenModel {

    private val _state = MutableStateFlow(PagamentoUiState())
    val state: StateFlow<PagamentoUiState> = _state.asStateFlow()

    private val _podeAvancar = MutableStateFlow(false)
    val podeAvancar: StateFlow<Boolean> = _podeAvancar.asStateFlow()

    fun avancarConsumido() { _podeAvancar.value = false }

    init {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    numeroMpesa = MockData.numeroMpesa,
                    titularMpesa = MockData.titularMpesa,
                    totalPedidoMt = MockOrderStore.cartItems.sumOf { it.precoUnitarioMt * it.quantidade } +
                        if (MockOrderStore.cartItems.isEmpty()) 0 else MockData.taxaEntregaMt,
                    contadorCarrinho = MockOrderStore.cartItemCount,
                )
            }
        }
    }

    fun onEvent(event: PagamentoUiEvent) {
        when (event) {
            is PagamentoUiEvent.EscolherFicheiroClicado ->
                // Demo: simula ficheiro seleccionado.
                _state.update { it.copy(nomeFicheiroCarregado = "comprovativo_mpesa.jpg") }
            is PagamentoUiEvent.RemoverFicheiroClicado ->
                _state.update { it.copy(nomeFicheiroCarregado = null) }
            is PagamentoUiEvent.CopiarNumeroClicado ->
                { /* Step 5: Clipboard expect/actual */ }
            is PagamentoUiEvent.EnviarComprovativoClicado -> {
                if (_state.value.podeEnviar && _state.value.numeroPedidoCriado == null) {
                    val order = MockOrderStore.createOrder(comprovativo = _state.value.nomeFicheiroCarregado)
                    _state.update {
                        it.copy(
                            numeroPedidoCriado = order.numero,
                            contadorCarrinho = MockOrderStore.cartItemCount,
                        )
                    }
                    _podeAvancar.value = true
                }
            }
        }
    }
}
