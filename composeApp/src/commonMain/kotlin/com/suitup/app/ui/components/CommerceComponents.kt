package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.domain.model.Pedido
import com.suitup.app.domain.model.Utilizador
import com.suitup.app.ui.screens.home.shortLabel
import com.suitup.app.ui.screens.home.toBadgeKind
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.util.formatMzn

@Composable
fun CheckoutStepIndicator(currentStep: Int, modifier: Modifier = Modifier) {
    val labels = listOf("Dados", "Medidas", "Entrega", "Pagamento")
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        labels.forEachIndexed { index, label ->
            val step = index + 1
            val reached = step <= currentStep
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (reached) SuitColors.Gold else SuitColors.SurfaceLow)
                        .border(1.dp, if (reached) SuitColors.Gold else SuitColors.Mist, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        step.toString(),
                        style = SuitTextStyles.labelMedium,
                        color = if (reached) SuitColors.GoldInk else SuitColors.Smoke,
                    )
                }
                Text(
                    label,
                    style = SuitTextStyles.labelSmall,
                    color = if (step == currentStep) SuitColors.GoldChampagne else SuitColors.Smoke,
                )
            }
        }
    }
}

@Composable
fun PremiumOrderCard(
    order: Pedido,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val paymentType = when (order.pagamento.status) {
        PaymentStatus.PENDING -> StatusChipType.Pending
        PaymentStatus.CONFIRMED -> StatusChipType.Confirmed
        PaymentStatus.REJECTED -> StatusChipType.Rejected
    }
    PremiumCard(modifier = modifier, onClick = onClick, padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("Pedido #${order.numero}", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                    Text(
                        order.designsFato.firstOrNull()?.nome ?: "Fato personalizado",
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Slate,
                    )
                }
                SuitStatusBadge(text = order.estado.shortLabel(), kind = order.estado.toBadgeKind())
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(formatMzn(order.total), style = SuitTextStyles.titleMedium, color = SuitColors.GoldChampagne)
                    Text(paymentType.label, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
                }
                Text("Acompanhar", style = SuitTextStyles.labelMedium, color = SuitColors.GoldChampagne)
            }
        }
    }
}

@Composable
fun ProfileHeaderCard(
    user: Utilizador,
    orderCount: Int,
    cartCount: Int,
    modifier: Modifier = Modifier,
) {
    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SuitAvatar(iniciais = user.iniciais, size = 58.dp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(user.nome, style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                    Text(user.email, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                    Text(user.telefone, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AccountMetric("Pedidos", orderCount.toString(), Modifier.weight(1f))
                AccountMetric("No carrinho", cartCount.toString(), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AccountMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SuitColors.WarmBlack)
            .border(1.dp, SuitColors.Mist, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(value, style = SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.Bold), color = SuitColors.GoldChampagne)
        Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
    }
}
