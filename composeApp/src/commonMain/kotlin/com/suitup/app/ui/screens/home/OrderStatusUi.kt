package com.suitup.app.ui.screens.home

import com.suitup.app.domain.model.EstadoPedido
import com.suitup.app.ui.components.SuitStatusKind

/**
 * Mapeia um EstadoPedido do domain para o SuitStatusKind visual usado nos badges.
 *
 * - Pendentes (sem pagamento ou aguardando) → Pendente (âmbar)
 * - Em fluxo (validado, em produção, pronto) → Info (azul)
 * - Entregue → Success (verde)
 * - Cancelado → Error (vermelho)
 */
fun EstadoPedido.toBadgeKind(): SuitStatusKind = when (this) {
    EstadoPedido.AguardandoPagamento -> SuitStatusKind.Pendente
    EstadoPedido.PagamentoValidado -> SuitStatusKind.Info
    EstadoPedido.PagamentoRejeitado -> SuitStatusKind.Error
    EstadoPedido.EmProducao -> SuitStatusKind.Info
    EstadoPedido.ProntoParaEntrega -> SuitStatusKind.Info
    EstadoPedido.Entregue -> SuitStatusKind.Success
    EstadoPedido.Cancelado -> SuitStatusKind.Error
}

/**
 * Label curto para usar nos cards de pedido (mais conciso que o label completo do domain).
 */
fun EstadoPedido.shortLabel(): String = when (this) {
    EstadoPedido.AguardandoPagamento -> "Pagamento pendente"
    EstadoPedido.PagamentoValidado -> "Pagamento confirmado"
    EstadoPedido.PagamentoRejeitado -> "Pagamento rejeitado"
    EstadoPedido.EmProducao -> "Em produção"
    EstadoPedido.ProntoParaEntrega -> "Pronto para entrega"
    EstadoPedido.Entregue -> "Entregue"
    EstadoPedido.Cancelado -> "Cancelado"
}
