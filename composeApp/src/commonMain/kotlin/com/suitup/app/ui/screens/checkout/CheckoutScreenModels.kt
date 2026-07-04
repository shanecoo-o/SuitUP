package com.suitup.app.ui.screens.checkout

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.order.CreateCustomerOrderResult
import com.suitup.app.data.order.CustomerOrderRepository
import com.suitup.app.data.order.OrderRuntime
import com.suitup.app.data.order.generateOrderIdempotencyKey
import com.suitup.app.data.payment.PaymentSubmitResult
import com.suitup.app.data.payment.PaymentTrackingDataSource
import com.suitup.app.data.payment.PaymentTrackingRepository
import com.suitup.app.data.payment.PaymentTrackingRuntime
import com.suitup.app.data.payment.ProofUploadResult
import com.suitup.app.domain.model.EnderecoEntrega
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.domain.model.PontoLevantamento
import com.suitup.app.domain.model.TipoEntrega
import com.suitup.app.ui.platform.SelectedProofFile
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
    val criandoPedido: Boolean = false,
    val erroPedido: String? = null,
    val pedidoCriadoId: String? = null,
    val sessaoExpirada: Boolean = false,
    val fallbackMockDisponivel: Boolean = false,
)

sealed class EnderecoUiEvent {
    data class ModoAlterado(val modo: TipoEntrega) : EnderecoUiEvent()
    data class CidadeAlterada(val valor: String) : EnderecoUiEvent()
    data class BairroAlterado(val valor: String) : EnderecoUiEvent()
    data class RuaAlterada(val valor: String) : EnderecoUiEvent()
    data class ReferenciaAlterada(val valor: String) : EnderecoUiEvent()
    data class PontoSeleccionado(val ponto: PontoLevantamento) : EnderecoUiEvent()
    data object ContinuarClicado : EnderecoUiEvent()
    data object ContinuarModoDemoClicado : EnderecoUiEvent()
}

class EnderecoScreenModel(
    private val modoInicial: TipoEntrega,
    private val orderRepository: CustomerOrderRepository = OrderRuntime.repository,
) : ScreenModel {

    private val _state = MutableStateFlow(EnderecoUiState(modo = modoInicial))
    val state: StateFlow<EnderecoUiState> = _state.asStateFlow()

    private var idempotencyKey: String? = null

    fun pedidoCriadoConsumido() {
        _state.update { it.copy(pedidoCriadoId = null) }
    }

    fun sessaoExpiradaConsumida() {
        _state.update { it.copy(sessaoExpirada = false) }
    }

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
                updateCheckoutInput { it.copy(modo = event.modo, erroPonto = null, erroEndereco = null) }
            is EnderecoUiEvent.CidadeAlterada ->
                updateCheckoutInput { it.copy(endereco = it.endereco.copy(cidade = event.valor)) }
            is EnderecoUiEvent.BairroAlterado ->
                updateCheckoutInput { it.copy(endereco = it.endereco.copy(bairro = event.valor)) }
            is EnderecoUiEvent.RuaAlterada ->
                updateCheckoutInput { it.copy(endereco = it.endereco.copy(rua = event.valor)) }
            is EnderecoUiEvent.ReferenciaAlterada ->
                updateCheckoutInput { it.copy(endereco = it.endereco.copy(referencia = event.valor)) }
            is EnderecoUiEvent.PontoSeleccionado ->
                updateCheckoutInput { it.copy(pontoSeleccionado = event.ponto, erroPonto = null) }
            is EnderecoUiEvent.ContinuarClicado -> validarEAvancar()
            EnderecoUiEvent.ContinuarModoDemoClicado -> criarPedidoDemo()
        }
    }

    private fun updateCheckoutInput(transform: (EnderecoUiState) -> EnderecoUiState) {
        if (_state.value.criandoPedido) return
        idempotencyKey = null
        _state.update { transform(it).copy(erroPedido = null, fallbackMockDisponivel = false) }
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
                    criarPedido()
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
                    criarPedido()
                }
            }
        }
    }

    private fun criarPedido() {
        if (_state.value.criandoPedido) return
        val key = idempotencyKey ?: generateOrderIdempotencyKey().also { idempotencyKey = it }
        screenModelScope.launch {
            _state.update { it.copy(criandoPedido = true, erroPedido = null) }
            when (val result = orderRepository.createCurrentCheckout(key)) {
                is CreateCustomerOrderResult.Success -> _state.update {
                    it.copy(
                        criandoPedido = false,
                        pedidoCriadoId = result.order.id,
                        contadorCarrinho = MockOrderStore.cartItemCount,
                    )
                }
                is CreateCustomerOrderResult.Failure -> _state.update {
                    it.copy(
                        criandoPedido = false,
                        erroPedido = result.message,
                        sessaoExpirada = result.sessionExpired,
                        fallbackMockDisponivel = result.canUseMockFallback,
                    )
                }
            }
        }
    }

    private fun criarPedidoDemo() {
        if (_state.value.criandoPedido || !_state.value.fallbackMockDisponivel) return
        screenModelScope.launch {
            _state.update { it.copy(criandoPedido = true, erroPedido = null) }
            when (val result = orderRepository.createMockCheckout()) {
                is CreateCustomerOrderResult.Success -> _state.update {
                    it.copy(
                        criandoPedido = false,
                        pedidoCriadoId = result.order.id,
                        contadorCarrinho = MockOrderStore.cartItemCount,
                        fallbackMockDisponivel = false,
                    )
                }
                is CreateCustomerOrderResult.Failure -> _state.update {
                    it.copy(criandoPedido = false, erroPedido = result.message)
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
    val referenciaTransaccao: String = "",
    val statusPagamento: PaymentStatus? = null,
    val carregando: Boolean = false,
    val mensagemSucesso: String? = null,
    val erro: String? = null,
    val pagamentoSubmetido: Boolean = false,
    val comprovativoEnviado: Boolean = false,
    val sessaoExpirada: Boolean = false,
    val fallbackMockDisponivel: Boolean = false,
) {
    val podeEnviar: Boolean
        get() = nomeFicheiroCarregado != null &&
            referenciaTransaccao.isNotBlank() &&
            totalPedidoMt > 0 &&
            !carregando
}

sealed class PagamentoUiEvent {
    data object EscolherFicheiroClicado : PagamentoUiEvent()
    data class FicheiroSeleccionado(val ficheiro: SelectedProofFile) : PagamentoUiEvent()
    data class FalhaSeleccao(val mensagem: String) : PagamentoUiEvent()
    data object RemoverFicheiroClicado : PagamentoUiEvent()
    data class ReferenciaAlterada(val valor: String) : PagamentoUiEvent()
    data object CopiarNumeroClicado : PagamentoUiEvent()
    data object EnviarComprovativoClicado : PagamentoUiEvent()
    data object ContinuarModoDemoClicado : PagamentoUiEvent()
}

class PagamentoScreenModel(
    private val orderId: String,
    private val orderRepository: CustomerOrderRepository = OrderRuntime.repository,
    private val paymentRepository: PaymentTrackingRepository = PaymentTrackingRuntime.repository,
) : ScreenModel {

    private val _state = MutableStateFlow(PagamentoUiState())
    val state: StateFlow<PagamentoUiState> = _state.asStateFlow()

    private val _podeAvancar = MutableStateFlow(false)
    val podeAvancar: StateFlow<Boolean> = _podeAvancar.asStateFlow()
    private var ficheiroSeleccionado: SelectedProofFile? = null

    fun avancarConsumido() { _podeAvancar.value = false }

    init {
        screenModelScope.launch {
            val cachedOrder = orderRepository.cachedOrder(orderId)
            val detail = if (cachedOrder == null) orderRepository.getOrder(orderId) else null
            val backendOrder = cachedOrder ?: detail?.order
            _state.update {
                it.copy(
                    numeroMpesa = MockData.numeroMpesa,
                    titularMpesa = MockData.titularMpesa,
                    totalPedidoMt = backendOrder?.total
                        ?: MockOrderStore.cartItems.sumOf { item -> item.precoUnitarioMt * item.quantidade } +
                            if (MockOrderStore.cartItems.isEmpty()) 0 else MockData.taxaEntregaMt,
                    contadorCarrinho = MockOrderStore.cartItemCount,
                    sessaoExpirada = detail?.sessionExpired == true,
                    erro = detail?.errorMessage,
                )
            }
        }
    }

    fun onEvent(event: PagamentoUiEvent) {
        when (event) {
            is PagamentoUiEvent.EscolherFicheiroClicado -> Unit
            is PagamentoUiEvent.FicheiroSeleccionado -> {
                ficheiroSeleccionado = event.ficheiro
                _state.update {
                    it.copy(
                        nomeFicheiroCarregado = event.ficheiro.filename,
                        erro = null,
                        mensagemSucesso = null,
                    )
                }
            }
            is PagamentoUiEvent.FalhaSeleccao ->
                _state.update { it.copy(erro = event.mensagem, mensagemSucesso = null) }
            is PagamentoUiEvent.RemoverFicheiroClicado -> if (!_state.value.pagamentoSubmetido) {
                ficheiroSeleccionado = null
                _state.update { it.copy(nomeFicheiroCarregado = null, erro = null) }
            }
            is PagamentoUiEvent.ReferenciaAlterada -> if (!_state.value.pagamentoSubmetido) {
                _state.update { it.copy(referenciaTransaccao = event.valor, erro = null) }
            }
            is PagamentoUiEvent.CopiarNumeroClicado ->
                { /* Step 5: Clipboard expect/actual */ }
            is PagamentoUiEvent.EnviarComprovativoClicado -> submeter()
            is PagamentoUiEvent.ContinuarModoDemoClicado -> submeterModoDemo()
        }
    }

    fun sessaoExpiradaConsumida() {
        _state.update { it.copy(sessaoExpirada = false) }
    }

    private fun submeter() {
        val state = _state.value
        val proof = ficheiroSeleccionado ?: return
        if (!state.podeEnviar || state.numeroPedidoCriado != null) return
        _state.update { it.copy(carregando = true, erro = null, mensagemSucesso = null) }
        screenModelScope.launch {
            if (state.pagamentoSubmetido) {
                enviarComprovativo(proof)
                return@launch
            }
            when (val result = paymentRepository.submitPayment(
                orderId = orderId,
                amountMzn = state.totalPedidoMt,
                reference = state.referenciaTransaccao,
            )) {
                is PaymentSubmitResult.Success -> {
                    _state.update {
                        it.copy(
                            pagamentoSubmetido = true,
                            statusPagamento = result.payment?.status ?: PaymentStatus.PENDING,
                            mensagemSucesso = "Pagamento submetido com sucesso.",
                            fallbackMockDisponivel = false,
                        )
                    }
                    if (result.source == PaymentTrackingDataSource.MOCK) {
                        concluirModoDemo()
                    } else {
                        enviarComprovativo(proof)
                    }
                }
                is PaymentSubmitResult.Failure -> _state.update {
                    it.copy(
                        carregando = false,
                        erro = result.message,
                        sessaoExpirada = result.sessionExpired,
                        fallbackMockDisponivel = result.canUseMockFallback,
                    )
                }
            }
        }
    }

    private suspend fun enviarComprovativo(proof: SelectedProofFile) {
        when (val result = paymentRepository.uploadPaymentProof(
            orderId = orderId,
            filename = proof.filename,
            contentType = proof.contentType,
            bytes = proof.bytes,
        )) {
            is ProofUploadResult.Success -> {
                orderRepository.refreshOrders()
                _state.update {
                    it.copy(
                        carregando = false,
                        comprovativoEnviado = true,
                        numeroPedidoCriado = orderId,
                        mensagemSucesso = "Comprovativo enviado com sucesso.",
                    )
                }
                _podeAvancar.value = true
            }
            is ProofUploadResult.Failure -> _state.update {
                it.copy(
                    carregando = false,
                    erro = result.message,
                    sessaoExpirada = result.sessionExpired,
                )
            }
        }
    }

    private fun submeterModoDemo() {
        val state = _state.value
        if (!state.fallbackMockDisponivel || state.carregando) return
        _state.update { it.copy(carregando = true, erro = null) }
        screenModelScope.launch {
            when (val result = paymentRepository.submitMockPayment(
                orderId,
                state.referenciaTransaccao,
                state.nomeFicheiroCarregado,
            )) {
                is PaymentSubmitResult.Success -> concluirModoDemo()
                is PaymentSubmitResult.Failure -> _state.update {
                    it.copy(carregando = false, erro = result.message)
                }
            }
        }
    }

    private fun concluirModoDemo() {
        _state.update {
            it.copy(
                carregando = false,
                pagamentoSubmetido = true,
                comprovativoEnviado = true,
                statusPagamento = PaymentStatus.PENDING,
                numeroPedidoCriado = orderId,
                mensagemSucesso = "Pagamento guardado em modo demo.",
                fallbackMockDisponivel = false,
            )
        }
        _podeAvancar.value = true
    }
}
