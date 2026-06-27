package com.suitup.app.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.EstadoEvento
import com.suitup.app.domain.model.EventoPedido
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.domain.model.Pedido
import com.suitup.app.ui.components.OrderTimeline
import com.suitup.app.ui.components.OrderTimelineItem
import com.suitup.app.ui.components.PaymentStatusCard
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.TimelineItemStatus
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.util.formatMzn

@Composable
fun TrackOrderScreen(
    order: Pedido,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onContactSupport: (() -> Unit)? = null,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PremiumTopBar(
            title = "Acompanhar pedido",
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            SectionHeader(
                eyebrow = "PEDIDO #${order.numero}",
                title = order.estado.label,
                description = "Actualizado em ${order.actualizadoEm}",
            )
            PaymentStatusCard(status = order.pagamento.status)
            if (order.pagamento.status == PaymentStatus.REJECTED) {
                PremiumCard {
                    Text(
                        "Pagamento rejeitado. Contacte a loja ou envie uma nova referência.",
                        style = SuitTextStyles.bodyMedium,
                        color = SuitColors.Error,
                    )
                }
            }
            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Progresso", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                    OrderTimeline(items = order.linhaTempo.map(::timelineItem))
                }
            }
            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Resumo do pedido", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                    SummaryLine("Fato", order.designsFato.firstOrNull()?.nome ?: "Fato personalizado")
                    SummaryLine("Cliente", order.cliente?.nome ?: "Cliente SuitUP")
                    SummaryLine(
                        "Recepção",
                        order.enderecoEntrega?.let { "${it.bairro}, ${it.cidade}" }
                            ?: order.pontoLevantamento?.nome
                            ?: order.tipoEntrega.label,
                    )
                    SummaryLine("Total", formatMzn(order.total))
                }
            }
        }
        if (onContactSupport != null) {
            PrimaryGoldButton(
                text = "Falar com suporte",
                onClick = onContactSupport,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            )
        }
    }
}

private fun timelineItem(event: EventoPedido): OrderTimelineItem = OrderTimelineItem(
    label = event.estadoPedido.label,
    description = event.ocorridoEm ?: when (event.estadoEvento) {
        EstadoEvento.Concluido -> "Concluído"
        EstadoEvento.Actual -> "Em curso"
        EstadoEvento.Pendente -> "Pendente"
    },
    status = when {
        event.estadoPedido.label.contains("rejeitado", ignoreCase = true) -> TimelineItemStatus.Rejected
        event.estadoEvento == EstadoEvento.Concluido -> TimelineItemStatus.Completed
        event.estadoEvento == EstadoEvento.Actual -> TimelineItemStatus.Current
        else -> TimelineItemStatus.Pending
    },
)

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
        Text(value, style = SuitTextStyles.bodyMedium, color = SuitColors.Pearl)
    }
}
