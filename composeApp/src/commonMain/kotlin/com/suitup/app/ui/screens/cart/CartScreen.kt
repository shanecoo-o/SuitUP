package com.suitup.app.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.ItemCarrinho
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitGarmentMini
import com.suitup.app.ui.components.SuitQuantityStepper
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.icons.CloseIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMetical
import com.suitup.app.ui.util.toComposeColorOrNull

/**
 * Ecrã 16 — Carrinho.
 *
 * Lista de items + resumo (subtotal/entrega/total) em Meticais + CTA Finalizar.
 * Empty state quando vazio.
 */
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            onBack = onBack,
            title = "Carrinho",
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )

        if (items.isEmpty()) {
            EmptyCart(
                modifier = Modifier.weight(1f),
                onContinueShopping = onContinueShopping,
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items.forEach { item ->
                    CartItemCard(
                        item = item,
                        onEdit = { onItemEdit(item) },
                        onRemove = { onItemRemove(item) },
                        onQuantityChange = { newQty -> onQuantityChange(item, newQty) },
                    )
                }

                CartSummary(
                    subtotal = subtotal,
                    taxaEntregaMt = taxaEntregaMt,
                    total = total,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
            ) {
                SuitButton(
                    text = "Finalizar pedido",
                    onClick = onCheckout,
                )
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
    val garmentColor = remember(item) {
        item.hexCor.toComposeColorOrNull() ?: SuitColors.Ink
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SuitTheme.shapes.card)
            .background(SuitColors.SurfaceWhite)
            .border(1.dp, SuitColors.Mist, SuitTheme.shapes.card)
            .padding(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SuitGarmentMini(
                    size = 64.dp,
                    garmentColor = garmentColor,
                    background = SuitColors.Pearl,
                    showShirt = true,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = item.nome,
                            style = SuitTextStyles.titleMedium,
                            color = SuitColors.Ink,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(onClick = onRemove),
                            contentAlignment = Alignment.Center
                        ) {
                            CloseIcon(tint = SuitColors.Slate, size = 16.dp)
                        }
                    }

                    item.detalhes.forEach { detail ->
                        Text(
                            text = detail,
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Slate,
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = SuitColors.Mist)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatMetical(item.precoUnitarioMt),
                    style = SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = SuitColors.Ink,
                )
                Text(
                    text = "Editar",
                    style = SuitTextStyles.labelMedium,
                    color = SuitColors.Slate,
                    modifier = Modifier
                        .clickable(onClick = onEdit)
                        .padding(4.dp),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                SuitQuantityStepper(
                    quantidade = item.quantidade,
                    onQuantityChange = onQuantityChange,
                )
            }
        }
    }
}

@Composable
private fun CartSummary(
    subtotal: Int,
    taxaEntregaMt: Int,
    total: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Resumo",
            style = SuitTextStyles.titleMedium,
            color = SuitColors.Ink,
        )

        SummaryRow(label = "Subtotal", valueMt = subtotal)
        SummaryRow(label = "Entrega", valueMt = taxaEntregaMt)

        HorizontalDivider(thickness = 1.dp, color = SuitColors.Mist)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Total",
                style = SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = SuitColors.Ink,
            )
            Text(
                text = formatMetical(total),
                style = SuitTextStyles.headlineMedium,
                color = SuitColors.Ink,
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, valueMt: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = SuitTextStyles.bodyMedium,
            color = SuitColors.Slate,
        )
        Text(
            text = formatMetical(valueMt),
            style = SuitTextStyles.bodyMedium,
            color = SuitColors.Ink,
        )
    }
}

@Composable
private fun EmptyCart(
    modifier: Modifier = Modifier,
    onContinueShopping: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Carrinho vazio",
            style = SuitTextStyles.headlineMedium,
            color = SuitColors.Ink,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Adicione um fato para começar.",
            style = SuitTextStyles.bodyMedium,
            color = SuitColors.Slate,
        )
        Spacer(Modifier.height(20.dp))
        SuitButton(
            text = "Explorar modelos",
            onClick = onContinueShopping,
            fullWidth = false,
        )
    }
}
