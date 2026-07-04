package com.suitup.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain models do SuitUP.
 *
 * Imutáveis, sem dependências de framework. São a fonte da verdade
 * para todos os ecrãs. O backend Ktor (Step 4) terá DTOs separados
 * que mapeiam para estes.
 */

@Serializable
data class Utilizador(
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val iniciais: String = nome.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
    val medidasGuardadas: Medidas? = null,
)

@Serializable
/**
 * Medidas do cliente para produção do fato sob medida.
 * Campos em String para suportar entrada gradual em formulário.
 * Step 4: converter para Int/Double ao persistir no backend.
 *
 * Obrigatórios: alturaCm, ombrosCm, peitoCm, cinturaCm, mangaCm, calcaCm.
 * Opcionais: pesoKg, quadrilCm, casacoCm, pescocoCm, observacoes.
 */
data class Medidas(
    val alturaCm: String = "",
    val pesoKg: String = "",
    val ombrosCm: String = "",
    val peitoCm: String = "",
    val cinturaCm: String = "",
    val quadrilCm: String = "",
    val mangaCm: String = "",
    val calcaCm: String = "",
    val casacoCm: String = "",
    val pescocoCm: String = "",
    val observacoes: String = "",
)

/**
 * Modelo de fato disponível no catálogo (template inicial).
 */
@Serializable
data class ModeloFato(
    val id: String,
    val nome: String,
    val categoria: CategoriaFato,
    val precoBase: Int,            // em meticais (MZN)
    val urlImagemPrevia: String,   // mock — Coil resolve, ou usamos drawable interno
)

@Serializable
data class SuitModel(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val basePrice: Int,
    val imageKey: String,
    val fabricType: String,
    val color: String,
    val available: Boolean = true,
    val currency: String = "MZN",
    val primaryImageFileId: String? = null,
)

enum class CategoriaFato(val label: String) {
    Classico("Clássico"),
    CorteSlim("Slim Fit"),
    Executivo("Executivo"),
    Casual("Casual"),
    Premium("Premium"),
    Gala("Gala");

    companion object {
        fun all() = entries.toList()
    }
}

/**
 * Configuração personalizada de um fato — o que sai do editor 2D/3D.
 */
@Serializable
data class DesignFato(
    val id: String,
    val idModeloBase: String,
    val nome: String,                  // ex: "Fato Slim Azul Marinho"
    val partes: PartesFato,
    val tecido: Tecido,
    val cor: CorFato,
    val preco: Int,
)

@Serializable
data class PartesFato(
    val gola: TipoGola = TipoGola.Padrao,
    val lapela: TipoLapela = TipoLapela.Entalhada,
    val bolso: TipoBolso = TipoBolso.Aba,
    val botoes: EstiloBotao = EstiloBotao.Dois,
    val mangas: EstiloManga = EstiloManga.Padrao,
    val forro: EstiloForro = EstiloForro.Liso,
    val costas: EstiloCostas = EstiloCostas.AberturaSimples,
    val ajusteLargura: Float = 0.5f,     // 0..1, "Largura" slider
)

enum class TipoGola { Padrao, Alta, Aberta }
enum class TipoLapela(val label: String) { Entalhada("Entalhada"), Pontiaguda("Pontiaguda"), Xale("Xale") }
enum class TipoBolso { Aba, Aplicado, Embutido }
enum class EstiloBotao(val label: String) { Um("1 botão"), Dois("2 botões"), Tres("3 botões") }
enum class EstiloManga { Padrao, Funcional, Cirurgiao }
enum class EstiloForro(val label: String) { Liso("Liso"), Padrao("Com padrões"), Seda("Seda") }
enum class EstiloCostas { AberturaSimples, AberturaDupla, SemAbertura }

@Serializable
data class Tecido(
    val id: String,
    val nome: String,           // ex: "Lã Premium"
    val hexAmostra: String,      // amostra para UI
)

@Serializable
data class CorFato(
    val id: String,
    val nome: String,
    val hex: String,
)

/**
 * Pedido feito pelo cliente.
 */
@Serializable
data class Pedido(
    val id: String,
    val numero: String,                  // ex: "1025"
    val idUtilizador: String,
    val cliente: DadosClientePedido? = null,
    val medidas: Medidas? = null,
    val designsFato: List<DesignFato>,
    val subtotal: Int,
    val taxaEntrega: Int,
    val total: Int,
    val tipoEntrega: TipoEntrega,
    val enderecoEntrega: EnderecoEntrega?,
    val pontoLevantamento: PontoLevantamento?,
    val pagamento: InfoPagamento,
    val estado: EstadoPedido,
    val linhaTempo: List<EventoPedido>,
    val criadoEm: String,               // ISO date string mock
    val actualizadoEm: String,
    val backendStatus: String? = null,
)

@Serializable
data class DadosClientePedido(
    val nome: String,
    val email: String,
    val telefone: String,
)

enum class TipoEntrega(val label: String, val description: String) {
    Entrega("Entrega", "Receba no endereço desejado"),
    Levantamento("Levantamento", "Levante em um dos nossos pontos")
}

@Serializable
data class EnderecoEntrega(
    val cidade: String,
    val bairro: String,            // bairro
    val rua: String,
    val referencia: String? = null,
)

@Serializable
data class PontoLevantamento(
    val id: String,
    val nome: String,
    val endereco: String,
)

enum class EstadoPedido(val label: String) {
    AguardandoPagamento("Aguardando confirmação do pagamento"),
    PagamentoValidado("Pagamento confirmado"),
    PagamentoRejeitado("Pagamento rejeitado"),
    EmProducao("Em produção"),
    ProntoParaEntrega("Pronto para entrega"),
    Entregue("Entregue"),
    Cancelado("Cancelado");
}

@Serializable
data class EventoPedido(
    val estadoPedido: EstadoPedido,
    val estadoEvento: EstadoEvento,
    val ocorridoEm: String? = null,      // null = pendente
)

enum class EstadoEvento { Concluido, Actual, Pendente }

@Serializable
data class InfoPagamento(
    val metodo: MetodoPagamento,
    val caminhoImagemComprovativo: String? = null,
    val numeroMpesa: String,
    val titular: String,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val referenciaTransaccao: String? = null,
    val idPagamento: String? = null,
)

enum class MetodoPagamento { MpesaManual }

enum class PaymentStatus(val label: String) {
    PENDING("Pendente"),
    CONFIRMED("Confirmado"),
    REJECTED("Rejeitado"),
}

/**
 * Item no carrinho — referencia um DesignFato + quantidade.
 */
@Serializable
data class ItemCarrinho(
    val id: String,
    val nome: String,
    val precoUnitarioMt: Int,        // preço unitário em meticais (Int)
    val quantidade: Int = 1,
    val hexCor: String,         // para o thumbnail
    val detalhes: List<String> = emptyList(),  // ex: "Tecido: Lã Premium"
) {
    val totalLinhaMt: Int get() = precoUnitarioMt * quantidade
}
