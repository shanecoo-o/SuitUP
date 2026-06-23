package com.suitup.app.data.mock

import com.suitup.app.domain.model.CorFato
import com.suitup.app.domain.model.DesignFato
import com.suitup.app.domain.model.EnderecoEntrega
import com.suitup.app.domain.model.EstadoEvento
import com.suitup.app.domain.model.EstadoPedido
import com.suitup.app.domain.model.EventoPedido
import com.suitup.app.domain.model.InfoPagamento
import com.suitup.app.domain.model.ItemCarrinho
import com.suitup.app.domain.model.MetodoPagamento
import com.suitup.app.domain.model.ModeloFato
import com.suitup.app.domain.model.PartesFato
import com.suitup.app.domain.model.Pedido
import com.suitup.app.domain.model.PontoLevantamento
import com.suitup.app.domain.model.Tecido
import com.suitup.app.domain.model.TipoEntrega
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MockDesignDraft(
    val modelo: ModeloFato,
    val partes: PartesFato = PartesFato(),
    val tecido: Tecido = MockData.tecidos.first(),
    val cor: CorFato = MockData.coresFato.first(),
)

private data class CartEntry(
    val id: String,
    val design: DesignFato,
    val quantity: Int,
)

object MockOrderStore {
    private var designSequence = 1
    private var cartSequence = 1
    private var orderSequence = 1025

    private val _draft = MutableStateFlow<MockDesignDraft?>(null)
    val draft: StateFlow<MockDesignDraft?> = _draft.asStateFlow()

    private val _cartEntries = MutableStateFlow<List<CartEntry>>(emptyList())
    private val _orders = MutableStateFlow(MockData.pedidosRecentes)
    val orders: StateFlow<List<Pedido>> = _orders.asStateFlow()

    val cartItems: List<ItemCarrinho>
        get() = _cartEntries.value.map { it.toCartItem() }

    val cartItemCount: Int
        get() = _cartEntries.value.sumOf { it.quantity }

    fun startDraft(modelo: ModeloFato): MockDesignDraft {
        val existing = _draft.value
        if (existing?.modelo?.id == modelo.id) return existing

        val draft = MockDesignDraft(
            modelo = modelo,
            cor = MockData.coresFato.firstOrNull { modelo.nome.contains(it.nome, ignoreCase = true) }
                ?: MockData.coresFato.first(),
            tecido = MockData.tecidos.first(),
        )
        _draft.value = draft
        return draft
    }

    fun ensureDraft(modeloId: String): MockDesignDraft {
        val modelo = MockCatalogStore.getModeloFatoById(modeloId)
            ?: MockCatalogStore.getActiveModeloFatos().first()
        return _draft.value?.takeIf { it.modelo.id == modeloId } ?: startDraft(modelo)
    }

    fun updatePartes(partes: PartesFato) {
        _draft.update { it?.copy(partes = partes) }
    }

    fun updateCor(cor: CorFato) {
        _draft.update { it?.copy(cor = cor) }
    }

    fun updateTecido(tecido: Tecido) {
        _draft.update { it?.copy(tecido = tecido) }
    }

    fun currentDesign(modeloId: String): DesignFato {
        val draft = ensureDraft(modeloId)
        return draft.toDesign("draft_preview")
    }

    fun addCurrentDesignToCart(modeloId: String): ItemCarrinho {
        val design = ensureDraft(modeloId).toDesign("d_mock_${designSequence++}")
        val cartId = "ci_mock_${cartSequence++}"
        _cartEntries.update { entries ->
            val matching = entries.firstOrNull { it.design.sameConfigurationAs(design) }
            if (matching != null) {
                entries.map { if (it.id == matching.id) it.copy(quantity = it.quantity + 1) else it }
            } else {
                entries + CartEntry(cartId, design, 1)
            }
        }
        return cartItems.first { it.id == cartId || it.nome == design.nome }
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        val newQuantity = quantity.coerceAtLeast(1)
        _cartEntries.update { entries ->
            entries.map { if (it.id == itemId) it.copy(quantity = newQuantity) else it }
        }
    }

    fun removeItem(itemId: String) {
        _cartEntries.update { entries -> entries.filterNot { it.id == itemId } }
    }

    fun createOrder(
        tipoEntrega: TipoEntrega = TipoEntrega.Entrega,
        enderecoEntrega: EnderecoEntrega? = EnderecoEntrega("Maputo", "Polana", "Av. Julius Nyerere, 123", "Proximo ao Shopping Polana"),
        pontoLevantamento: PontoLevantamento? = null,
        comprovativo: String? = null,
    ): Pedido {
        val entries = _cartEntries.value
        val designs = entries.flatMap { entry -> List(entry.quantity) { entry.design } }
        val subtotal = entries.sumOf { it.design.preco * it.quantity }
        val taxaEntrega = if (entries.isEmpty()) 0 else MockData.taxaEntregaMt
        val numero = orderSequence++.toString()
        val now = "20/05/2024 10:30"
        val order = Pedido(
            id = "o$numero",
            numero = numero,
            idUtilizador = MockData.utilizadorActual.id,
            designsFato = designs,
            subtotal = subtotal,
            taxaEntrega = taxaEntrega,
            total = subtotal + taxaEntrega,
            tipoEntrega = tipoEntrega,
            enderecoEntrega = if (tipoEntrega == TipoEntrega.Entrega) enderecoEntrega else null,
            pontoLevantamento = if (tipoEntrega == TipoEntrega.Levantamento) pontoLevantamento else null,
            pagamento = InfoPagamento(
                metodo = MetodoPagamento.MpesaManual,
                caminhoImagemComprovativo = comprovativo,
                numeroMpesa = MockData.numeroMpesa,
                titular = MockData.titularMpesa,
            ),
            estado = EstadoPedido.AguardandoPagamento,
            linhaTempo = timelineFor(EstadoPedido.AguardandoPagamento),
            criadoEm = "20/05/2024",
            actualizadoEm = now,
        )
        _orders.update { listOf(order) + it }
        _cartEntries.value = emptyList()
        return order
    }

    private fun MockDesignDraft.toDesign(id: String): DesignFato = DesignFato(
        id = id,
        idModeloBase = modelo.id,
        nome = "${modelo.nome} ${cor.nome}",
        partes = partes,
        tecido = tecido,
        cor = cor,
        preco = modelo.precoBase,
    )

    private fun CartEntry.toCartItem(): ItemCarrinho = ItemCarrinho(
        id = id,
        nome = design.nome,
        precoUnitarioMt = design.preco,
        quantidade = quantity,
        hexCor = design.cor.hex,
        detalhes = listOf(
            "Modelo: ${design.idModeloBase}",
            "Tecido: ${design.tecido.nome}",
            "Cor: ${design.cor.nome}",
            "Lapela: ${design.partes.lapela.label}",
            "Botoes: ${design.partes.botoes.label}",
        ),
    )

    private fun DesignFato.sameConfigurationAs(other: DesignFato): Boolean =
        idModeloBase == other.idModeloBase &&
            partes == other.partes &&
            tecido.id == other.tecido.id &&
            cor.id == other.cor.id

    private fun timelineFor(current: EstadoPedido): List<EventoPedido> {
        val flow = listOf(
            EstadoPedido.AguardandoPagamento,
            EstadoPedido.PagamentoValidado,
            EstadoPedido.EmProducao,
            EstadoPedido.ProntoParaEntrega,
            EstadoPedido.Entregue,
        )
        if (current == EstadoPedido.AguardandoPagamento) {
            return flow.mapIndexed { index, state ->
                EventoPedido(
                    estadoPedido = state,
                    estadoEvento = if (index == 0) EstadoEvento.Concluido else EstadoEvento.Pendente,
                    ocorridoEm = null,
                )
            }
        }
        val currentIndex = flow.indexOf(current).coerceAtLeast(0)
        return flow.mapIndexed { index, state ->
            val eventState = when {
                index < currentIndex -> EstadoEvento.Concluido
                index == currentIndex -> EstadoEvento.Actual
                else -> EstadoEvento.Pendente
            }
            EventoPedido(
                estadoPedido = state,
                estadoEvento = eventState,
                ocorridoEm = if (eventState == EstadoEvento.Concluido) "20/05/2024" else null,
            )
        }
    }
}
