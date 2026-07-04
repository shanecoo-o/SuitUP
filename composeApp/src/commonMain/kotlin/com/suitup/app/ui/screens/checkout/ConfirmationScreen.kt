package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Pedido
import com.suitup.app.ui.components.PaymentStatusCard
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.SuitSuccessBadge
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.util.formatMzn

@Composable
fun ConfirmationScreen(
    orderNumber: String,
    order: Pedido? = null,
    isDemo: Boolean = false,
    onSeeOrders: () -> Unit = {},
    onBackToHome: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PremiumTopBar(title = "Confirmação")
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
        ) {
            SuitSuccessBadge(size = 80.dp)
            Text(
                "Pedido criado com sucesso",
                style = SuitTextStyles.headlineLarge,
                color = SuitColors.Pearl,
                textAlign = TextAlign.Center,
            )
            Text(
                if (isDemo) {
                    "Pedido guardado em modo demo. Não foi enviado ao servidor."
                } else {
                    "Pagamento submetido com sucesso. O comprovativo foi enviado para validação."
                },
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Slate,
                textAlign = TextAlign.Center,
            )
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
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
                        "Aguardando confirmação do pagamento pelo administrador."
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PrimaryGoldButton(text = "Acompanhar pedido", onClick = onSeeOrders)
            SecondaryDarkButton(text = "Voltar ao início", onClick = onBackToHome)
        }
    }
}

@Composable
private fun ConfirmationLine(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
        Text(value, style = SuitTextStyles.titleMedium, color = SuitColors.Pearl)
    }
}
