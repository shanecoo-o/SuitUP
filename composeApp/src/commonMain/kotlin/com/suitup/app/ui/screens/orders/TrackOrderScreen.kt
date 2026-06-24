package com.suitup.app.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.EstadoEvento
import com.suitup.app.domain.model.Pedido
import com.suitup.app.domain.model.EventoPedido
import com.suitup.app.domain.model.EstadoPedido
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitEyebrow
import com.suitup.app.ui.components.SuitStatusBadge
import com.suitup.app.ui.components.SuitStatusKind
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.icons.CheckIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Ecrã 14 — Acompanhar Pedido.
 *
 * Top bar dark com "Pedido #N". Card resumo com estado atual + timestamp.
 * Timeline vertical com 5 estados do flow do pedido.
 * Footer com "Falar com suporte".
 */
@Composable
fun TrackOrderScreen(
    order: Pedido,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onContactSupport: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            dark = true,
            title = "Pedido #${order.numero}",
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Header card com estado atual
            CurrentStatusCard(
                statusLabel = order.estado.label,
                actualizadoEm = order.actualizadoEm,
            )

            PaymentStatusCard(status = order.pagamento.status)

            // Timeline
            OrderTimeline(events = order.linhaTempo)
        }

        // Footer
        if (onContactSupport != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
            ) {
                SuitButton(
                    text = "Falar com suporte",
                    onClick = onContactSupport,
                )
            }
        }
    }
}

@Composable
private fun PaymentStatusCard(status: PaymentStatus) {
    val (description, kind) = when (status) {
        PaymentStatus.PENDING -> "Pagamento enviado. Aguardando confirmação do administrador." to SuitStatusKind.Pendente
        PaymentStatus.CONFIRMED -> "Pagamento confirmado pela equipa SuitUP" to SuitStatusKind.Success
        PaymentStatus.REJECTED -> "Pagamento rejeitado. Contacte o suporte para reenviar o comprovativo." to SuitStatusKind.Error
    }

    SuitCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SuitEyebrow("Pagamento")
                SuitStatusBadge(text = status.label, kind = kind)
            }
            Text(
                text = description,
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        }
    }
}

@Composable
private fun CurrentStatusCard(statusLabel: String, actualizadoEm: String) {
    SuitCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SuitEyebrow("Status atual")
            Text(
                text = statusLabel,
                style = SuitTextStyles.titleLarge,
                color = SuitColors.Ink,
            )
            Text(
                text = "Atualizado em $actualizadoEm",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        }
    }
}

@Composable
private fun OrderTimeline(events: List<EventoPedido>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        events.forEachIndexed { index, event ->
            val isLast = index == events.lastIndex
            TimelineRow(
                event = event,
                isLast = isLast,
            )
        }
    }
}

@Composable
private fun TimelineRow(
    event: EventoPedido,
    isLast: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        // Coluna do indicator + linha conectora
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp),
        ) {
            StatusIndicator(state = event.estadoEvento)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(
                            if (event.estadoEvento == EstadoEvento.Concluido) SuitColors.Gold
                            else SuitColors.Mist
                        )
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Texto: título + sub-label
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp, top = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = eventTitleLabel(event.estadoPedido),
                style = SuitTextStyles.titleMedium,
                color = SuitColors.Ink,
            )
            Text(
                text = eventSubLabel(event),
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        }
    }
}

@Composable
private fun StatusIndicator(state: EstadoEvento) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(
                when (state) {
                    EstadoEvento.Concluido, EstadoEvento.Actual -> SuitColors.Gold
                    EstadoEvento.Pendente -> SuitColors.SurfaceLow
                }
            )
            .border(
                width = if (state == EstadoEvento.Pendente) 1.5.dp else 0.dp,
                color = SuitColors.Mist,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            EstadoEvento.Concluido -> CheckIcon(tint = SuitColors.GoldInk, size = 14.dp)
            EstadoEvento.Actual -> Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SuitColors.GoldInk)
            )
            EstadoEvento.Pendente -> Unit
        }
    }
}

/**
 * Label curto do estado para usar como título do item da linhaTempo.
 * Difere ligeiramente do label completo do domain para encurtar.
 */
private fun eventTitleLabel(estado: EstadoPedido): String = when (estado) {
    EstadoPedido.AguardandoPagamento -> "Pedido recebido"
    EstadoPedido.PagamentoValidado -> "Pagamento confirmado"
    EstadoPedido.PagamentoRejeitado -> "Pagamento rejeitado"
    EstadoPedido.EmProducao -> "Em produção"
    EstadoPedido.ProntoParaEntrega -> "Pronto para entrega"
    EstadoPedido.Entregue -> "Entregue"
    EstadoPedido.Cancelado -> "Cancelado"
}

/**
 * Sub-label calculada conforme o estado do evento.
 * - Concluido com occurredAt → mostrar data
 * - Concluido sem occurredAt → "Aguardando validação" (caso M-Pesa pendente)
 * - Actual → "Em curso"
 * - Pendente → "Pendente"
 */
private fun eventSubLabel(event: EventoPedido): String = when (event.estadoEvento) {
    EstadoEvento.Concluido -> when {
        event.ocorridoEm != null -> event.ocorridoEm
        event.estadoPedido == EstadoPedido.AguardandoPagamento -> "Aguardando confirmação do administrador"
        else -> "Concluído"
    }
    EstadoEvento.Actual -> "Em curso"
    EstadoEvento.Pendente -> "Pendente"
}
