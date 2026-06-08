package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.TipoEntrega
import com.suitup.app.ui.components.SuitDualBottomBar
import com.suitup.app.ui.components.SuitSelectableCard
import com.suitup.app.ui.components.SuitStepIndicator
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.icons.PinIcon
import com.suitup.app.ui.icons.TruckIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Ecrã 10 — Checkout · Tipo de Entrega.
 *
 * Step 2 de 4. Escolha entre Entrega (motoboy ao endereço) ou Levantamento
 * (loja física). Cada opção é um SuitSelectableCard.
 */
@Composable
fun DeliveryTypeScreen(
    selected: TipoEntrega,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onSelect: (TipoEntrega) -> Unit = {},
    onContinue: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
            centerContent = { SuitStepIndicator(currentStep = 3, totalSteps = 5) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Escolha o tipo de entrega",
                style = SuitTextStyles.headlineMedium,
                color = SuitColors.Ink,
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SuitSelectableCard(
                    title = TipoEntrega.Entrega.label,
                    description = TipoEntrega.Entrega.description,
                    selected = selected == TipoEntrega.Entrega,
                    onClick = { onSelect(TipoEntrega.Entrega) },
                    leadingIcon = { TruckIcon(size = 24.dp) }
                )
                SuitSelectableCard(
                    title = TipoEntrega.Levantamento.label,
                    description = TipoEntrega.Levantamento.description,
                    selected = selected == TipoEntrega.Levantamento,
                    onClick = { onSelect(TipoEntrega.Levantamento) },
                    leadingIcon = { PinIcon(size = 24.dp) }
                )
            }
        }

        SuitDualBottomBar(
            primaryText = "Continuar",
            onPrimaryClick = onContinue,
            onSecondaryClick = onBack,
        )
    }
}
