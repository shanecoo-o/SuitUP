package com.suitup.app.ui.screens.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.ItemCarrinho
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonVariant
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitDetailScaffold
import com.suitup.app.ui.components.SuitDetailTopBar
import com.suitup.app.ui.components.SuitEyebrow
import com.suitup.app.ui.components.SuitFixedCtaBar
import com.suitup.app.ui.components.SuitImageContainer
import com.suitup.app.ui.components.SuitImageContext
import com.suitup.app.ui.components.SuitQuantityStepper
import com.suitup.app.ui.icons.CloseIcon
import com.suitup.app.ui.icons.ShirtIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn
import com.suitup.app.ui.util.suitImageResource

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

    SuitDetailScaffold(
        topBar = {
            SuitDetailTopBar(
                onBack = onBack,
                title = "Carrinho",
                onCart = onCartClick,
                cartBadgeCount = cartItemCount,
            )
        },
        fixedCta = if (items.isEmpty()) null else {
            {
                SuitFixedCtaBar {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Total", style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
                            Text(formatMzn(total), style = SuitTextStyles.titleLarge, color = SuitColors.GoldChampagne)
                        }
                        SuitButton(text = "Continuar para checkout", onClick = onCheckout)
                    }
                }
            }
        },
    ) {
        if (items.isEmpty()) {
            CartEmptyState(
                modifier = Modifier.padding(24.dp),
                onContinueShopping = onContinueShopping,
            )
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SuitEyebrow(text = "A sua selecção")
                Text(
                    "Resumo do carrinho",
                    style = SuitTextStyles.titleLarge,
                    color = SuitColors.Ink,
                )
                Text(
                    "$cartItemCount artigo(s) preparado(s) para checkout.",
                    style = SuitTextStyles.bodySmall,
                    color = SuitColors.Slate,
                )
                items.forEach { item ->
                    CartItemCard(
                        item = item,
                        onEdit = { onItemEdit(item) },
                        onRemove = { onItemRemove(item) },
                        onQuantityChange = { onQuantityChange(item, it) },
                    )
                }
                CartOrderSummaryCard(subtotal = subtotal, taxaEntregaMt = taxaEntregaMt, total = total)
            }
        }
    }
}

@Composable
private fun CartEmptyState(
    modifier: Modifier = Modifier,
    onContinueShopping: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier.size(72.dp),
            contentAlignment = Alignment.Center,
        ) {
            ShirtIcon(tint = SuitColors.Smoke, size = 40.dp)
        }
        Text(
            "O seu carrinho está vazio",
            style = SuitTextStyles.titleMedium,
            color = SuitColors.Ink,
        )
        Text(
            "Escolha um modelo e personalize o seu primeiro fato.",
            style = SuitTextStyles.bodySmall,
            color = SuitColors.Slate,
        )
        SuitButton(
            text = "Explorar catálogo",
            onClick = onContinueShopping,
            variant = SuitButtonVariant.Secondary,
            fullWidth = false,
        )
    }
}

@Composable
private fun CartOrderSummaryCard(subtotal: Int, taxaEntregaMt: Int, total: Int) {
    SuitCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CartSummaryLine(label = "Subtotal", valueMt = subtotal)
            CartSummaryLine(label = "Entrega estimada", valueMt = taxaEntregaMt)
            HorizontalDivider(color = SuitColors.Mist)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Total", style = SuitTextStyles.titleMedium, color = SuitColors.Ink)
                Text(formatMzn(total), style = SuitTextStyles.titleLarge, color = SuitColors.GoldChampagne)
            }
        }
    }
}

@Composable
private fun CartSummaryLine(label: String, valueMt: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
        Text(formatMzn(valueMt), style = SuitTextStyles.bodyMedium, color = SuitColors.Ink)
    }
}

@Composable
private fun CartItemCard(
    item: ItemCarrinho,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit,
) {
    SuitCard(modifier = Modifier.fillMaxWidth(), padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SuitImageContainer(
                    image = suitImageResource(item.imagemKey),
                    contentDescription = item.nome,
                    context = SuitImageContext.Thumbnail,
                    modifier = Modifier.size(72.dp),
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
                            color = SuitColors.Ink,
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
