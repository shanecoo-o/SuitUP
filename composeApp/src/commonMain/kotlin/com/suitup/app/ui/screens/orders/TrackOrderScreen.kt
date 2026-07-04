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
import com.suitup.app.data.payment.CustomerTrackingEvent
import com.suitup.app.data.payment.CustomerTrackingStatus
import com.suitup.app.ui.components.OrderTimeline
import com.suitup.app.ui.components.OrderTimelineItem
import com.suitup.app.ui.components.PaymentStatusCard
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.TimelineItemStatus
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.util.formatMzn

@Composable
fun TrackOrderScreen(
    order: Pedido,
    cartItemCount: Int = 0,
    backendTimeline: List<CustomerTrackingEvent>? = null,
    isTimelineLoading: Boolean = false,
    timelineError: String? = null,
    noticeMessage: String? = null,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onContactSupport: (() -> Unit)? = null,
    onRetryTimeline: () -> Unit = {},
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
            if (noticeMessage != null) {
                PremiumCard {
                    Text(noticeMessage, style = SuitTextStyles.bodySmall, color = SuitColors.Error)
                }
            }
            SectionHeader(
                eyebrow = "PEDIDO #${order.numero}",
                title = order.estado.label,
                description = "Criado em ${order.criadoEm} · actualizado em ${order.actualizadoEm}",
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
                    when {
                        isTimelineLoading -> Text(
                            "A carregar acompanhamento...",
                            style = SuitTextStyles.bodyMedium,
                            color = SuitColors.Slate,
                        )
                        timelineError != null -> {
                            Text(timelineError, style = SuitTextStyles.bodyMedium, color = SuitColors.Error)
                            SecondaryDarkButton(
                                text = "Tentar novamente",
                                onClick = onRetryTimeline,
                                fullWidth = false,
                            )
                        }
                        backendTimeline != null && backendTimeline.isEmpty() -> Text(
                            "Ainda não existe histórico para este pedido.",
                            style = SuitTextStyles.bodyMedium,
                            color = SuitColors.Slate,
                        )
                        backendTimeline != null -> OrderTimeline(items = backendTimeline.map(::timelineItem))
                        else -> OrderTimeline(items = order.linhaTempo.map(::timelineItem))
                    }
                }
            }
            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Resumo do pedido", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                    val design = order.designsFato.firstOrNull()
                    SummaryLine("Fato", design?.nome ?: "Fato personalizado")
                    design?.let {
                        SummaryLine("Tecido", it.tecido.nome)
                        SummaryLine("Cor", it.cor.nome)
                        SummaryLine("Lapela", it.partes.lapela.label)
                        SummaryLine("Botões", it.partes.botoes.label)
                    }
                    SummaryLine("Cliente", order.cliente?.nome ?: "Cliente SuitUP")
                    SummaryLine(
                        "Recepção",
                        order.enderecoEntrega?.let { "${it.bairro}, ${it.cidade}" }
                            ?: order.pontoLevantamento?.nome
                            ?: order.tipoEntrega.label,
                    )
                    SummaryLine("Subtotal", formatMzn(order.subtotal))
                    SummaryLine("Entrega", formatMzn(order.taxaEntrega))
                    SummaryLine("Total", formatMzn(order.total))
                }
            }
            order.medidas?.let { medidas ->
                PremiumCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Medidas", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                        MeasurementLine("Altura", medidas.alturaCm)
                        MeasurementLine("Peito", medidas.peitoCm)
                        MeasurementLine("Cintura", medidas.cinturaCm)
                        MeasurementLine("Ombros", medidas.ombrosCm)
                        MeasurementLine("Manga", medidas.mangaCm)
                        MeasurementLine("Calça", medidas.calcaCm)
                    }
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

private fun timelineItem(event: CustomerTrackingEvent): OrderTimelineItem = OrderTimelineItem(
    label = when (event.status) {
        CustomerTrackingStatus.RECEIVED -> "Pedido recebido"
        CustomerTrackingStatus.IN_ANALYSIS -> "Em análise"
        CustomerTrackingStatus.MEASUREMENTS_CONFIRMED -> "Medidas confirmadas"
        CustomerTrackingStatus.IN_PRODUCTION -> "Em produção"
        CustomerTrackingStatus.READY_FOR_DELIVERY -> "Pronto para entrega"
        CustomerTrackingStatus.DELIVERED -> "Entregue"
        CustomerTrackingStatus.CANCELLED -> "Cancelado"
    },
    description = event.occurredAt.ifBlank { if (event.isCurrent) "Estado actual" else "Registado" },
    status = when {
        event.status == CustomerTrackingStatus.CANCELLED -> TimelineItemStatus.Rejected
        event.isCurrent -> TimelineItemStatus.Current
        else -> TimelineItemStatus.Completed
    },
)

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
        Text(value, style = SuitTextStyles.bodyMedium, color = SuitColors.Pearl)
    }
}

@Composable
private fun MeasurementLine(label: String, value: String) {
    if (value.isNotBlank()) SummaryLine(label, "$value cm")
}
