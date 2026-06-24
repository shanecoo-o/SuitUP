package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitStepIndicator
import com.suitup.app.ui.components.SuitSuccessBadge
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Ecrã 13 — Checkout · Confirmação.
 *
 * Step 4 de 4. Estado terminal de sucesso do flow checkout.
 * Sem costas — o utilizador escolhe entre ir aos pedidos ou voltar ao início.
 *
 * O número do pedido vem do backend (mock no Step 1).
 */
@Composable
fun ConfirmationScreen(
    orderNumber: String,
    onSeeOrders: () -> Unit = {},
    onBackToHome: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            centerContent = { SuitStepIndicator(currentStep = 5, totalSteps = 5) },
        )

        // Conteúdo centrado verticalmente
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SuitSuccessBadge(size = 80.dp)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Pedido realizado com sucesso",
                style = SuitTextStyles.headlineMedium,
                color = SuitColors.Ink,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Pagamento enviado. Aguardando confirmação do administrador.",
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Slate,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            // Número do pedido destacado
            Text(
                text = "Pedido #$orderNumber",
                style = SuitTextStyles.headlineLarge,
                color = SuitColors.Ink,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Enviaremos uma notificação assim que seu pagamento for validado.",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
                textAlign = TextAlign.Center,
            )
        }

        // Bottom actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SuitButton(
                text = "Ver meus pedidos",
                onClick = onSeeOrders,
            )
            Text(
                text = "Voltar ao início",
                style = SuitTextStyles.labelMedium,
                color = SuitColors.Slate,
                modifier = Modifier
                    .clickable(onClick = onBackToHome)
                    .padding(12.dp),
            )
        }
    }
}
