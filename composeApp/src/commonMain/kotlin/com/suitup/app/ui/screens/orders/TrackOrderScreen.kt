package com.suitup.app.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.data.mock.MockCatalogStore
import com.suitup.app.domain.model.EstadoEvento
import com.suitup.app.domain.model.EventoPedido
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.domain.model.Pedido
import com.suitup.app.data.payment.CustomerTrackingEvent
import com.suitup.app.data.payment.CustomerTrackingStatus
import com.suitup.app.ui.components.OrderTimeline
import com.suitup.app.ui.components.OrderTimelineItem
import com.suitup.app.ui.components.TimelineItemStatus
import com.suitup.app.ui.components.PaymentStatusCard
import com.suitup.app.ui.components.SuitAlertBanner
import com.suitup.app.ui.components.SuitAlertVariant
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonVariant
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitDetailTopBar
import com.suitup.app.ui.components.SuitFixedCtaBar
import com.suitup.app.ui.components.SuitImageContainer
import com.suitup.app.ui.components.SuitImageContext
import com.suitup.app.ui.components.SuitDetailScaffold
import com.suitup.app.ui.components.SuitStatusBadge
import com.suitup.app.ui.screens.home.shortLabel
import com.suitup.app.ui.screens.home.toBadgeKind
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn
import com.suitup.app.ui.util.suitImageResource

/**
 * Ecrã "Detalhes do pedido / Acompanhar" (Fase 9.6C). Migrado para a
 * linguagem Stitch. A app não tem um ecrã de Order Detail separado do
 * Tracking — [OrdersScreens.TrackOrderVoyagerScreen] sempre navegou
 * directamente para aqui (ver [com.suitup.app.ui.components.SuitDetailScaffold]
 * doc: "Archetype B — pushed detail screen (Product Detail, Order
 * Detail/Tracking)") — por isso as Tasks 5-8 (Order Detail) e 10-13
 * (Tracking) deste ecrã convergem num único ficheiro, sem introduzir uma
 * segunda rota. Mantém o ScreenModel/repositório existentes
 * (AcompanharPedidoScreenModel) — apenas apresentação.
 *
 * Proof de pagamento do cliente (Task 9): [order.pagamento.caminhoImagemComprovativo]
 * já transporta o proofFileId real (RemoteMappers.kt) mas não existe, neste
 * repositório, uma rota/backend confirmado de download seguro para o cliente
 * (apenas o lado Admin consome RemoteFileRepository.download). Por isso este
 * ecrã apenas mostra o estado do comprovativo (hasProof) via
 * [PaymentStatusCard] — sem criar uma segunda API/repositório de proof nem
 * inventar uma rota de download/preview para o cliente
 * (CUSTOMER PROOF DETAIL DEFERRED — DATA/ROUTE GAP).
 */
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
    SuitDetailScaffold(
        topBar = {
            SuitDetailTopBar(
                title = "Detalhes do pedido",
                onBack = onBack,
                onCart = onCartClick,
                cartBadgeCount = cartItemCount,
            )
        },
        fixedCta = if (onContactSupport != null) {
            {
                SuitFixedCtaBar {
                    SuitButton(text = "Falar com suporte", onClick = onContactSupport)
                }
            }
        } else null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SuitTheme.responsive.horizontalContentPadding, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (noticeMessage != null) {
                SuitAlertBanner(variant = SuitAlertVariant.Offline, message = noticeMessage)
            }

            OrderSummarySection(order)

            PaymentStatusCard(
                status = order.pagamento.status,
                paymentReference = order.pagamento.referenciaTransaccao,
                orderReference = "#${order.numero}",
                hasProof = order.pagamento.caminhoImagemComprovativo != null,
            )
            if (order.pagamento.status == PaymentStatus.REJECTED) {
                SuitAlertBanner(
                    variant = SuitAlertVariant.Error,
                    message = "Pagamento rejeitado. Contacte a loja ou envie uma nova referência.",
                )
            }

            DeliverySection(order)

            SuitCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Acompanhamento", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
                    when {
                        isTimelineLoading -> Text(
                            "A carregar acompanhamento...",
                            style = SuitTextStyles.bodyMedium,
                            color = SuitColors.Slate,
                        )
                        timelineError != null -> {
                            Text(timelineError, style = SuitTextStyles.bodyMedium, color = SuitColors.Error)
                            SuitButton(
                                text = "Tentar novamente",
                                onClick = onRetryTimeline,
                                variant = SuitButtonVariant.Secondary,
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

            order.medidas?.let { medidas ->
                SuitCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Medidas", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
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
    }
}

@Composable
private fun OrderSummarySection(order: Pedido) {
    val design = order.designsFato.firstOrNull()
    SuitCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            SuitImageContainer(
                image = suitImageResource(
                    design?.idModeloBase?.let { MockCatalogStore.getModeloFatoById(it)?.urlImagemPrevia }.orEmpty(),
                ),
                contentDescription = design?.nome,
                context = SuitImageContext.ProductDetail,
                modifier = Modifier.width(96.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pedido #${order.numero}", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
                    SuitStatusBadge(text = order.estado.shortLabel(), kind = order.estado.toBadgeKind())
                }
                Text(design?.nome ?: "Fato personalizado", style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
                design?.let {
                    SummaryLine("Tecido", it.tecido.nome)
                    SummaryLine("Cor", it.cor.nome)
                    SummaryLine("Lapela", it.partes.lapela.label)
                    SummaryLine("Botões", it.partes.botoes.label)
                }
                Text(
                    "Criado em ${order.criadoEm} · actualizado em ${order.actualizadoEm}",
                    style = SuitTextStyles.bodySmall,
                    color = SuitColors.Smoke,
                )
            }
        }
    }
}

@Composable
private fun DeliverySection(order: Pedido) {
    SuitCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Cliente e entrega", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
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
