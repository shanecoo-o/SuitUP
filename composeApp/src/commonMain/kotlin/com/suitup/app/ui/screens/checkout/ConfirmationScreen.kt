package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.domain.model.Pedido
import com.suitup.app.ui.components.PaymentStatusCard
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonVariant
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitDetailScaffold
import com.suitup.app.ui.components.SuitFixedCtaBar
import com.suitup.app.ui.components.SuitPrimaryTopBar
import com.suitup.app.ui.components.SuitSuccessBadge
import com.suitup.app.ui.icons.ErrorIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.util.formatMzn

/**
 * Ecrã 13 — Checkout · Confirmação (Fase 9.6B).
 *
 * Migrado para a linguagem Stitch. Reflecte honestamente o estado real do
 * pagamento (Task 18/19): nunca apresenta "sucesso" incondicional quando o
 * pagamento continua Pendente/Rejeitado — o título, o texto e o distintivo
 * variam com [Pedido.pagamento]'s real status. A confirmação/rejeição em si
 * é decidida pelo administrador; este ecrã apenas relata o estado, não o
 * altera.
 */
@Composable
fun ConfirmationScreen(
    orderNumber: String,
    order: Pedido? = null,
    isDemo: Boolean = false,
    onSeeOrders: () -> Unit = {},
    onBackToHome: () -> Unit = {},
) {
    val status = order?.pagamento?.status
    val copy = resolveConfirmationCopy(isDemo, status)

    SuitDetailScaffold(
        topBar = { SuitPrimaryTopBar(title = "Confirmação") },
        fixedCta = {
            SuitFixedCtaBar {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuitButton(text = "Acompanhar pedido", onClick = onSeeOrders)
                    SuitButton(text = "Voltar ao início", onClick = onBackToHome, variant = SuitButtonVariant.Secondary)
                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ConfirmationStatusBadge(isDemo = isDemo, status = status)
            Text(
                copy.title,
                style = SuitTextStyles.headlineLarge,
                color = SuitColors.Ink,
                textAlign = TextAlign.Center,
            )
            Text(
                copy.subtitle,
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Slate,
                textAlign = TextAlign.Center,
            )
            SuitCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ConfirmationLine("Pedido", "#$orderNumber")
                    order?.designsFato?.firstOrNull()?.let { ConfirmationLine("Fato", it.nome) }
                    order?.let { ConfirmationLine("Total", formatMzn(it.total)) }
                }
            }
            order?.let {
                PaymentStatusCard(
                    status = it.pagamento.status,
                    description = if (isDemo) {
                        "Pagamento mantido apenas no fluxo local de demonstração."
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private data class ConfirmationCopy(val title: String, val subtitle: String)

private fun resolveConfirmationCopy(isDemo: Boolean, status: PaymentStatus?): ConfirmationCopy = when {
    isDemo -> ConfirmationCopy(
        title = "Pedido guardado em modo demo",
        subtitle = "Pedido guardado localmente. Não foi enviado ao servidor.",
    )
    status == PaymentStatus.CONFIRMED -> ConfirmationCopy(
        title = "Pagamento confirmado",
        subtitle = "O seu pagamento foi confirmado. O pedido vai avançar para produção.",
    )
    status == PaymentStatus.REJECTED -> ConfirmationCopy(
        title = "Pagamento rejeitado",
        subtitle = "O comprovativo foi rejeitado pelo administrador. Contacte o suporte para regularizar.",
    )
    else -> ConfirmationCopy(
        title = "Pagamento submetido com sucesso",
        subtitle = "O comprovativo foi enviado para validação. Aguarde a confirmação do administrador.",
    )
}

@Composable
private fun ConfirmationStatusBadge(isDemo: Boolean, status: PaymentStatus?) {
    when {
        !isDemo && status == PaymentStatus.REJECTED -> Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SuitColors.PaleRed),
            contentAlignment = Alignment.Center,
        ) {
            ErrorIcon(tint = SuitColors.PaleRedInk, size = 36.dp)
        }
        !isDemo && status == PaymentStatus.CONFIRMED -> SuitSuccessBadge(
            background = SuitColors.PaleGreen,
            iconTint = SuitColors.PaleGreenInk,
        )
        !isDemo -> SuitSuccessBadge(
            background = SuitColors.PaleAmber,
            iconTint = SuitColors.PaleAmberInk,
        )
        else -> SuitSuccessBadge()
    }
}

@Composable
private fun ConfirmationLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
        Text(value, style = SuitTextStyles.titleMedium, color = SuitColors.Ink)
    }
}
