package com.suitup.app.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Pedido
import com.suitup.app.ui.components.EmptyStateCard
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumOrderCard
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

@Composable
fun OrdersListScreen(
    orders: List<Pedido>,
    cartItemCount: Int = 0,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    isUsingMockFallback: Boolean = false,
    onOrderClick: (Pedido) -> Unit = {},
    onCartClick: () -> Unit = {},
    onExploreCatalog: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PremiumTopBar(title = "Meus pedidos", onCart = onCartClick, cartBadgeCount = cartItemCount)

        if (errorMessage != null && orders.isNotEmpty()) {
            PremiumCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                padding = 14.dp,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(errorMessage, style = SuitTextStyles.bodyMedium, color = SuitColors.Error)
                    if (isUsingMockFallback) {
                        Text(
                            "A mostrar pedidos locais em modo demo.",
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Slate,
                        )
                    }
                    SecondaryDarkButton(text = "Tentar novamente", onClick = onRetry, fullWidth = false)
                }
            }
        }

        if (orders.isEmpty()) {
            EmptyStateCard(
                modifier = Modifier.weight(1f).padding(24.dp),
                title = when {
                    isLoading -> "A carregar pedidos..."
                    errorMessage != null -> errorMessage
                    else -> "Ainda não existem pedidos."
                },
                description = when {
                    isLoading -> "A obter o seu histórico de encomendas."
                    errorMessage != null -> "Verifique a ligação e tente novamente."
                    else -> "Quando encomendar um fato, poderá acompanhar o progresso aqui."
                },
                actionLabel = if (errorMessage != null) "Tentar novamente" else "Explorar catálogo",
                onAction = if (errorMessage != null) onRetry else onExploreCatalog,
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    SectionHeader(
                        eyebrow = "HISTÓRICO",
                        title = "As suas encomendas",
                        description = "${orders.size} pedido(s) registado(s).",
                    )
                }
                items(orders, key = { it.id }) { order ->
                    PremiumOrderCard(order = order, onClick = { onOrderClick(order) })
                }
            }
        }
    }
}
