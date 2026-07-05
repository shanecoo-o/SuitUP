package com.suitup.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SuitImageCard(
    image: DrawableResource,
    title: String,
    priceMzn: Int,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    status: StatusChipType? = null,
    onClick: (() -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    PremiumCard(modifier = modifier, onClick = onClick, padding = 12.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.82f)
                    .clip(SuitTheme.shapes.md)
                    .background(SuitColors.WarmBlack),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        title,
                        style = SuitTextStyles.titleMedium,
                        color = SuitColors.Pearl,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (subtitle != null) {
                        Text(
                            subtitle,
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Slate,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (status != null) StatusChip(status)
            }
            Text(
                text = formatMzn(priceMzn),
                style = SuitTextStyles.titleLarge,
                color = SuitColors.GoldChampagne,
            )
            if (actionLabel != null && onAction != null) {
                PrimaryGoldButton(
                    text = actionLabel,
                    onClick = onAction,
                )
            }
        }
    }
}

data class CheckoutSummaryLine(
    val label: String,
    val valueMzn: Int,
)

@Composable
fun CheckoutSummaryCard(
    lines: List<CheckoutSummaryLine>,
    totalMzn: Int,
    modifier: Modifier = Modifier,
    title: String = "Resumo do pedido",
) {
    PremiumCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
            lines.forEach { line ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(line.label, style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
                    Text(formatMzn(line.valueMzn), style = SuitTextStyles.bodyMedium, color = SuitColors.Pearl)
                }
            }
            HorizontalDivider(color = SuitColors.Mist)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Total", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                Text(
                    formatMzn(totalMzn),
                    style = SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = SuitColors.GoldChampagne,
                )
            }
        }
    }
}

/**
 * Payment card foundation (Task 13). Purely visual — never performs a remote
 * action itself; callers own confirm/reject/retry behavior.
 */
@Composable
fun PaymentStatusCard(
    status: PaymentStatus,
    modifier: Modifier = Modifier,
    description: String? = null,
    paymentReference: String? = null,
    orderReference: String? = null,
    dateLabel: String? = null,
    hasProof: Boolean? = null,
) {
    val chip = when (status) {
        PaymentStatus.PENDING -> StatusChipType.Pending
        PaymentStatus.CONFIRMED -> StatusChipType.Confirmed
        PaymentStatus.REJECTED -> StatusChipType.Rejected
    }
    val resolvedDescription = description ?: when (status) {
        PaymentStatus.PENDING -> "Pagamento enviado. Aguardando confirmação do administrador."
        PaymentStatus.CONFIRMED -> "Pagamento confirmado. O pedido pode avançar."
        PaymentStatus.REJECTED -> "Pagamento rejeitado. Reveja os dados ou envie novo comprovativo."
    }
    PremiumCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Pagamento", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                StatusChip(chip)
            }
            if (paymentReference != null || orderReference != null) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (paymentReference != null) {
                        Text("Ref. pagamento: $paymentReference", style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
                    }
                    if (orderReference != null) {
                        Text("Pedido: $orderReference", style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
                    }
                }
            }
            Text(resolvedDescription, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
            if (dateLabel != null || hasProof != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (dateLabel != null) {
                        Text(dateLabel, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
                    }
                    if (hasProof != null) {
                        SuitStatusBadge(
                            text = if (hasProof) "Comprovativo anexado" else "Sem comprovativo",
                            kind = if (hasProof) SuitStatusKind.Success else SuitStatusKind.Neutral,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UploadCard(
    selectedFileName: String?,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    onRemove: () -> Unit = {},
    title: String = "Enviar comprovativo",
    hint: String = "PNG, JPG ou PDF até 10MB",
) {
    SuitUploadCard(
        uploadedFileName = selectedFileName,
        onPickFile = onSelect,
        modifier = modifier,
        title = title,
        hint = hint,
        onRemove = onRemove,
    )
}
