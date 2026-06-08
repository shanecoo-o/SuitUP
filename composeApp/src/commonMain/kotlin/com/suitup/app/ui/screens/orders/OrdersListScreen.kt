package com.suitup.app.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Pedido
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitGarmentMini
import com.suitup.app.ui.components.SuitStatusBadge
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.icons.ForwardChevronIcon
import com.suitup.app.ui.screens.home.shortLabel
import com.suitup.app.ui.screens.home.toBadgeKind
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.util.toComposeColorOrNull

/**
 * Ecrã 17 — Lista de Pedidos (novo, não estava no mockup).
 *
 * Primeira screen da tab Pedidos. Mostra todos os pedidos do user
 * em lista vertical, cada um clicável para abrir TrackOrderScreen.
 *
 * Empty state quando não há pedidos.
 */
@Composable
fun OrdersListScreen(
    orders: List<Pedido>,
    cartItemCount: Int = 0,
    onOrderClick: (Pedido) -> Unit = {},
    onCartClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )

        Text(
            text = "Meus pedidos",
            style = SuitTextStyles.headlineLarge,
            color = SuitColors.Ink,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        if (orders.isEmpty()) {
            EmptyOrders(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderListRow(order = order, onClick = { onOrderClick(order) })
                }
            }
        }
    }
}

@Composable
private fun OrderListRow(order: Pedido, onClick: () -> Unit) {
    SuitCard(
        onClick = onClick,
        padding = 12.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SuitGarmentMini(
                size = 56.dp,
                garmentColor = order.designsFato.firstOrNull()?.cor?.hex?.toComposeColorOrNull()
                    ?: SuitColors.Ink,
                background = SuitColors.Pearl,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Pedido #${order.numero}",
                    style = SuitTextStyles.titleMedium,
                    color = SuitColors.Ink,
                )
                SuitStatusBadge(
                    text = order.estado.shortLabel(),
                    kind = order.estado.toBadgeKind(),
                )
            }

            ForwardChevronIcon(tint = SuitColors.Slate, size = 20.dp)
        }
    }
}

@Composable
private fun EmptyOrders(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Ainda não tem pedidos",
            style = SuitTextStyles.titleMedium,
            color = SuitColors.Ink,
        )
        Text(
            text = "O seu primeiro fato começa no separador Início.",
            style = SuitTextStyles.bodyMedium,
            color = SuitColors.Slate,
        )
    }
}
