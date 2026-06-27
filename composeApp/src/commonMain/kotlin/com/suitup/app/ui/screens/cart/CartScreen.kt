package com.suitup.app.ui.screens.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.ItemCarrinho
import com.suitup.app.ui.components.CheckoutSummaryCard
import com.suitup.app.ui.components.CheckoutSummaryLine
import com.suitup.app.ui.components.EmptyStateCard
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.SuitGarmentMini
import com.suitup.app.ui.components.SuitQuantityStepper
import com.suitup.app.ui.icons.CloseIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.util.formatMzn
import com.suitup.app.ui.util.toComposeColorOrNull

@Composable
fun CartScreen(
    items: List<ItemCarrinho>,
    taxaEntregaMt: Int,
    cartItemCount: Int = items.sumOf { it.quantidade },
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onItemEdit: (ItemCarrinho) -> Unit = {},
    onItemRemove: (ItemCarrinho) -> Unit = {},
    onQuantityChange: (ItemCarrinho, Int) -> Unit = { _, _ -> },
    onCheckout: () -> Unit = {},
    onContinueShopping: () -> Unit = {},
) {
    val subtotal = items.sumOf { it.precoUnitarioMt * it.quantidade }
    val total = subtotal + taxaEntregaMt

    Column(modifier = Modifier.fillMaxSize()) {
        PremiumTopBar(
            title = "Carrinho",
            onBack = onBack,
            cartBadgeCount = cartItemCount,
        )
        if (items.isEmpty()) {
            EmptyStateCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp),
                title = "O seu carrinho está vazio",
                description = "Escolha um modelo e personalize o seu primeiro fato.",
                actionLabel = "Explorar catálogo",
                onAction = onContinueShopping,
                icon = {
                    SuitGarmentMini(
                        size = 72.dp,
                        garmentColor = SuitColors.Charcoal,
                        background = SuitColors.SurfaceLow,
                    )
                },
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SectionHeader(
                    eyebrow = "A SUA SELECÇÃO",
                    title = "Resumo do carrinho",
                    description = "$cartItemCount artigo(s) preparado(s) para checkout.",
                )
                items.forEach { item ->
                    CartItemCard(
                        item = item,
                        onEdit = { onItemEdit(item) },
                        onRemove = { onItemRemove(item) },
                        onQuantityChange = { onQuantityChange(item, it) },
                    )
                }
                CheckoutSummaryCard(
                    lines = listOf(
                        CheckoutSummaryLine("Subtotal", subtotal),
                        CheckoutSummaryLine("Entrega estimada", taxaEntregaMt),
                    ),
                    totalMzn = total,
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Total", style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
                    Text(formatMzn(total), style = SuitTextStyles.titleLarge, color = SuitColors.GoldChampagne)
                }
                PrimaryGoldButton(text = "Continuar para checkout", onClick = onCheckout)
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: ItemCarrinho,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit,
) {
    val garmentColor = remember(item.hexCor) {
        item.hexCor.toComposeColorOrNull() ?: SuitColors.Charcoal
    }
    PremiumCard(modifier = Modifier.fillMaxWidth(), padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SuitGarmentMini(
                    size = 72.dp,
                    garmentColor = garmentColor,
                    background = SuitColors.SurfaceLow,
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            item.nome,
                            style = SuitTextStyles.titleMedium,
                            color = SuitColors.Pearl,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier.size(32.dp).clickable(onClick = onRemove),
                            contentAlignment = Alignment.Center,
                        ) {
                            CloseIcon(tint = SuitColors.Smoke, size = 17.dp)
                        }
                    }
                    item.detalhes.filterNot { it.startsWith("Modelo:") }.take(2).forEach {
                        Text(it, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                    }
                }
            }
            HorizontalDivider(color = SuitColors.Mist)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    formatMzn(item.precoUnitarioMt),
                    style = SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = SuitColors.GoldChampagne,
                )
                Text(
                    "Editar",
                    style = SuitTextStyles.labelMedium,
                    color = SuitColors.GoldChampagne,
                    modifier = Modifier.clickable(onClick = onEdit).padding(8.dp),
                )
            }
            SuitQuantityStepper(
                quantidade = item.quantidade,
                onQuantityChange = onQuantityChange,
            )
        }
    }
}
