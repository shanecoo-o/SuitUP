package com.suitup.app.data.mock

import com.suitup.app.domain.model.*

/**
 * Mock data para o Step 1.
 * Nomes, números, bairros e valores realistas moçambicanos.
 *
 * Será substituído por repositories reais no Step 4.
 */
object MockData {

    val utilizadorActual = Utilizador(
        id = "user_1",
        nome = "João da Silva",
        email = "joao@email.com",
        telefone = "+258 84 123 4567",
        medidasGuardadas = Medidas(
            alturaCm = "175", pesoKg = "72",
            ombrosCm = "47", peitoCm = "102",
            cinturaCm = "88", quadrilCm = "100",
            mangaCm = "64", calcaCm = "108",
            casacoCm = "76", pescocoCm = "39",
        )
    )

    val modelosFato: List<ModeloFato>
        get() = MockCatalogStore.getActiveModeloFatos()

    val tecidos = listOf(
        Tecido("f1", "Lã Premium", "#1F2A44"),
        Tecido("f2", "Lã Tropical", "#3B3B3B"),
        Tecido("f3", "Cashmere", "#5B5B5B"),
        Tecido("f4", "Lã com Padrões", "#2C2C2C"),
        Tecido("f5", "Linho", "#A89B82"),
        Tecido("f6", "Tweed", "#5C4A3A"),
        Tecido("f7", "Mohair", "#8B7E6E"),
        Tecido("f8", "Veludo", "#3A2A2A"),
    )

    val coresFato = listOf(
        CorFato("c1", "Azul Marinho", "#1F2A44"),
        CorFato("c2", "Cinza Grafite", "#3B3B3B"),
        CorFato("c3", "Preto", "#1A1A1A"),
        CorFato("c4", "Bege", "#C5B299"),
        CorFato("c5", "Castanho", "#6B4423"),
        CorFato("c6", "Bordeaux", "#5C1F2E"),
    )

    private val designExemplo = DesignFato(
        id = "d1",
        idModeloBase = "m2",
        nome = "Fato Slim Azul Marinho",
        partes = PartesFato(
            lapela = TipoLapela.Entalhada,
            botoes = EstiloBotao.Dois,
            forro = EstiloForro.Padrao,
        ),
        tecido = tecidos[0],
        cor = coresFato[0],
        preco = 3450
    )

    val pedidosRecentes = listOf(
        Pedido(
            id = "o1024", numero = "1024", idUtilizador = "user_1",
            designsFato = listOf(designExemplo),
            subtotal = 3450, taxaEntrega = 150, total = 3600,
            tipoEntrega = TipoEntrega.Entrega,
            enderecoEntrega = EnderecoEntrega("Maputo", "Polana", "Av. Julius Nyerere, 123", "Próximo ao Shopping Polana"),
            pontoLevantamento = null,
            pagamento = InfoPagamento(MetodoPagamento.MpesaManual, null, "84 123 4567", "SuitUP Lda", PaymentStatus.CONFIRMED),
            estado = EstadoPedido.EmProducao,
            linhaTempo = orderTimelineFor(EstadoPedido.EmProducao),
            criadoEm = "20/05/2024", actualizadoEm = "20/05/2024 10:30"
        ),
        Pedido(
            id = "o1023", numero = "1023", idUtilizador = "user_1",
            designsFato = listOf(designExemplo.copy(id = "d2", nome = "Fato Clássico Preto")),
            subtotal = 3450, taxaEntrega = 150, total = 3600,
            tipoEntrega = TipoEntrega.Entrega,
            enderecoEntrega = EnderecoEntrega("Maputo", "Sommerschield", "Rua dos Lírios, 45", null),
            pontoLevantamento = null,
            pagamento = InfoPagamento(MetodoPagamento.MpesaManual, null, "84 123 4567", "SuitUP Lda", PaymentStatus.PENDING),
            estado = EstadoPedido.AguardandoPagamento,
            linhaTempo = orderTimelineFor(EstadoPedido.AguardandoPagamento),
            criadoEm = "18/05/2024", actualizadoEm = "18/05/2024 14:12"
        ),
        Pedido(
            id = "o1022", numero = "1022", idUtilizador = "user_1",
            designsFato = listOf(designExemplo.copy(id = "d3", nome = "Fato Executivo Cinza")),
            subtotal = 4200, taxaEntrega = 150, total = 4350,
            tipoEntrega = TipoEntrega.Levantamento,
            enderecoEntrega = null,
            pontoLevantamento = PontoLevantamento("p1", "Loja Polana", "Av. 24 de Julho, 1234"),
            pagamento = InfoPagamento(MetodoPagamento.MpesaManual, null, "84 123 4567", "SuitUP Lda", PaymentStatus.CONFIRMED),
            estado = EstadoPedido.Entregue,
            linhaTempo = orderTimelineFor(EstadoPedido.Entregue),
            criadoEm = "10/05/2024", actualizadoEm = "15/05/2024 16:45"
        ),
    )

    /**
     * Pedido recém-criado — usado no ecrã "Acompanhar Pedido" (#14).
     */
    val novoPedido = Pedido(
        id = "o1025", numero = "1025", idUtilizador = "user_1",
        designsFato = listOf(designExemplo),
        subtotal = 3450, taxaEntrega = 150, total = 3600,
        tipoEntrega = TipoEntrega.Entrega,
        enderecoEntrega = EnderecoEntrega("Maputo", "Polana", "Av. Julius Nyerere, 123", "Próximo ao Shopping Polana"),
        pontoLevantamento = null,
            pagamento = InfoPagamento(MetodoPagamento.MpesaManual, null, "84 123 4567", "SuitUP Lda", PaymentStatus.PENDING),
        estado = EstadoPedido.AguardandoPagamento,
        linhaTempo = orderTimelineFor(EstadoPedido.AguardandoPagamento),
        criadoEm = "20/05/2024", actualizadoEm = "20/05/2024 10:30"
    )

    val itensCarrinho = listOf(
        ItemCarrinho(
            id = "ci-1",
            nome = "Fato Slim Azul Marinho",
            precoUnitarioMt = 3450,
            quantidade = 1,
            hexCor = "#1F2A44",
            detalhes = listOf(
                "Tecido: Lã Premium",
                "Forro: Azul com padrões",
                "Lapela: Entalhada",
            ),
        ),
    )

    val taxaEntregaMt = 150

    val pontosLevantamento = listOf(
        PontoLevantamento("p1", "Loja Polana", "Av. 24 de Julho, 1234, Maputo"),
        PontoLevantamento("p2", "Loja Baixa", "Rua da Argélia, 56, Maputo"),
    )

    val cidadesMocambicanas = listOf("Maputo", "Matola", "Beira", "Nampula", "Quelimane")
    val bairrosMaputo = listOf("Polana", "Sommerschield", "Alto Maé", "Costa do Sol", "Malhangalene", "Maxaquene")

    val numeroMpesa = "84 123 4567"
    val titularMpesa = "SuitUP Lda"

    /**
     * Constrói a linhaTempo de um pedido baseado no estado atual.
     *
     * Convenção visual:
     * - Estados anteriores ao atual → Concluido (com data)
     * - Estado atual → Actual (em curso)
     * - Estados futuros → Pendente
     *
     * Exceção: AguardandoPagamento é um estado "à espera de ação do admin" —
     * o utilizador já fez a sua parte (enviou o comprovativo). Por isso o
     * passo 1 fica Concluido (com sub-label "Aguardando validação"), os outros Pendente.
     */
    private fun orderTimelineFor(current: EstadoPedido): List<EventoPedido> {
        val flow = listOf(
            EstadoPedido.AguardandoPagamento,
            EstadoPedido.PagamentoValidado,
            EstadoPedido.EmProducao,
            EstadoPedido.ProntoParaEntrega,
            EstadoPedido.Entregue,
        )

        // Caso especial: AguardandoPagamento → passo 1 é Concluido sem data + restantes Pendente
        if (current == EstadoPedido.AguardandoPagamento) {
            return flow.mapIndexed { i, st ->
                EventoPedido(
                    estadoPedido = st,
                    estadoEvento = if (i == 0) EstadoEvento.Concluido else EstadoEvento.Pendente,
                    ocorridoEm = null, // Concluido sem data → UI mostra "Aguardando validação"
                )
            }
        }

        val currentIndex = flow.indexOf(current).coerceAtLeast(0)
        return flow.mapIndexed { i, st ->
            val state = when {
                i < currentIndex -> EstadoEvento.Concluido
                i == currentIndex -> EstadoEvento.Actual
                else -> EstadoEvento.Pendente
            }
            EventoPedido(
                estadoPedido = st,
                estadoEvento = state,
                ocorridoEm = if (state == EstadoEvento.Concluido) "20/05/2024" else null
            )
        }
    }
}
