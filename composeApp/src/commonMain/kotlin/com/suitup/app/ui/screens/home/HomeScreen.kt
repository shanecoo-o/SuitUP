package com.suitup.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Pedido
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonSize
import com.suitup.app.ui.components.SuitButtonVariant
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitGarmentMini
import com.suitup.app.ui.components.SuitLogoInline
import com.suitup.app.ui.components.SuitStatusBadge
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.icons.ForwardChevronIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.toComposeColorOrNull

/**
 * Ecrã 04 — Home.
 *
 * Top bar escura com logo + cart, hero card escuro com CTA dourado,
 * lista de pedidos recentes, bottom nav.
 */
/**
 * NOTA arquitetural: bottom nav vive no MainShellScreen (ui/navigation), não aqui.
 * Este composable só renderiza o **conteúdo** da tab Home.
 */
@Composable
fun HomeScreen(
    pedidosRecentes: List<Pedido>,
    cartItemCount: Int,
    onCreateNewSuit: () -> Unit = {},
    onOrderClick: (Pedido) -> Unit = {},
    onSeeAllOrders: () -> Unit = {},
    onCartClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            dark = true,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
            centerContent = { SuitLogoInline(markSize = 24.dp, tint = SuitColors.SurfaceWhite) }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item("hero") {
                HeroCard(onCreateNewSuit = onCreateNewSuit)
            }

            item("orders-header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Meus pedidos",
                        style = SuitTextStyles.headlineMedium,
                        color = SuitColors.Ink,
                    )
                    Text(
                        text = "Ver todos",
                        style = SuitTextStyles.labelMedium,
                        color = SuitColors.Slate,
                        modifier = Modifier
                            .clickable(onClick = onSeeAllOrders)
                            .padding(4.dp)
                    )
                }
            }

            if (pedidosRecentes.isEmpty()) {
                item("empty") { EmptyOrders() }
            } else {
                items(pedidosRecentes, key = { it.id }) { order ->
                    OrderRow(order = order, onClick = { onOrderClick(order) })
                }
            }
        }
    }
}

@Composable
private fun HeroCard(onCreateNewSuit: () -> Unit) {
    SuitCard(
        background = SuitColors.SurfaceLow,
        border = true,
        padding = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 0.dp, top = 24.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(
                    text = "Crie o seu estilo único e exclusivo",
                    style = SuitTextStyles.headlineMedium,
                    color = SuitColors.SurfaceWhite,
                )
                SuitButton(
                    text = "Criar novo fato",
                    onClick = onCreateNewSuit,
                    variant = SuitButtonVariant.Gold,
                    size = SuitButtonSize.Medium,
                    fullWidth = false,
                )
            }

            Spacer(Modifier.width(12.dp))

            // Mini suit posicionado à direita do card
            Box(
                modifier = Modifier
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                SuitGarmentMini(
                    size = 96.dp,
                    garmentColor = SuitColors.Charcoal,
                    background = Color.Transparent,
                    showShirt = true,
                )
            }
        }
    }
}

@Composable
private fun OrderRow(order: Pedido, onClick: () -> Unit) {
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
                garmentColor = order.designsFato.firstOrNull()?.cor?.hex?.toComposeColorOrNull() ?: SuitColors.Charcoal,
                background = SuitColors.SurfaceLow,
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
private fun EmptyOrders() {
    SuitCard(modifier = Modifier.fillMaxWidth(), padding = 18.dp) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Ainda não tem pedidos",
                style = SuitTextStyles.titleMedium,
                color = SuitColors.Ink,
            )
            Text(
                text = "Comece pelo seu primeiro fato.",
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Slate,
            )
        }
    }
}
