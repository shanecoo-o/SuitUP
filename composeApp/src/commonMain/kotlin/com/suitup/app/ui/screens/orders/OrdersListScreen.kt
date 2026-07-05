package com.suitup.app.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.suitup.app.data.mock.MockCatalogStore
import com.suitup.app.domain.model.DesignFato
import com.suitup.app.domain.model.Pedido
import com.suitup.app.ui.components.EmptyStateCard
import com.suitup.app.ui.components.ErrorStateCard
import com.suitup.app.ui.components.SuitAlertBanner
import com.suitup.app.ui.components.SuitAlertVariant
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitContentLoading
import com.suitup.app.ui.components.SuitImageContext
import com.suitup.app.ui.components.SuitImageContainer
import com.suitup.app.ui.components.SuitPrimaryTopBar
import com.suitup.app.ui.components.SuitStatusBadge
import com.suitup.app.ui.screens.home.shortLabel
import com.suitup.app.ui.screens.home.toBadgeKind
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn
import com.suitup.app.ui.util.suitImageResource

/**
 * Ecrã "Meus pedidos" (Fase 9.6C). Migrado para a linguagem Stitch. Mantém o
 * ScreenModel/repositório existentes (ListaPedidosScreenModel) — este ecrã
 * apenas apresenta o [state][ListaPedidosUiState] já produzido, sem posse de
 * dados nova. Sem paginação: a lista actual não suporta paginação incremental,
 * apenas refresh total (Task 1).
 */
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
    val horizontalPadding = SuitTheme.responsive.horizontalContentPadding

    Column(modifier = Modifier.fillMaxSize().background(SuitColors.Bone)) {
        SuitPrimaryTopBar(title = "Meus pedidos", onCart = onCartClick, cartBadgeCount = cartItemCount)

        when {
            isLoading && orders.isEmpty() -> SuitContentLoading(
                modifier = Modifier.weight(1f),
                message = "A carregar pedidos...",
            )
            errorMessage != null && orders.isEmpty() -> ErrorStateCard(
                title = "Não foi possível carregar os pedidos",
                description = errorMessage,
                retryLabel = "Tentar novamente",
                onRetry = onRetry,
                modifier = Modifier.weight(1f).padding(24.dp),
            )
            orders.isEmpty() -> EmptyStateCard(
                title = "Ainda não existem pedidos.",
                description = "Quando encomendar um fato, poderá acompanhar o progresso aqui.",
                actionLabel = "Explorar catálogo",
                onAction = onExploreCatalog,
                modifier = Modifier.weight(1f).padding(24.dp),
            )
            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (errorMessage != null) {
                    item("inline-error") {
                        SuitAlertBanner(
                            variant = SuitAlertVariant.Error,
                            message = errorMessage,
                            actionLabel = "Tentar novamente",
                            onAction = onRetry,
                        )
                    }
                }
                if (isUsingMockFallback) {
                    item("mock-fallback") {
                        SuitAlertBanner(
                            variant = SuitAlertVariant.Offline,
                            message = "A mostrar pedidos locais em modo demo.",
                        )
                    }
                }
                item("header") {
                    Text(
                        "${orders.size} pedido(s) registado(s)",
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Slate,
                    )
                }
                items(orders, key = { it.id }) { order ->
                    SuitOrderCard(order = order, onClick = { onOrderClick(order) })
                }
            }
        }
    }
}

/**
 * Card de pedido Stitch. Substitui o antigo PremiumOrderCard (dark
 * PremiumFoundation) apenas dentro deste ecrã — a definição antiga permanece
 * em CommerceComponents.kt sem consumidores restantes (Home já usa o seu
 * próprio RecentOrderCard), pelo que não foi tocada para minimizar risco.
 *
 * Estado (canónico): reutiliza [EstadoPedido.shortLabel]/[EstadoPedido.toBadgeKind]
 * (Fase 9.4) em vez de recriar um mapeador local — evita reintroduzir o bug de
 * mapeamento de estado corrigido nessa fase (Task 7).
 *
 * Imagem (continuidade, Task 3): usa exactamente a mesma cadeia
 * design.idModeloBase -> MockCatalogStore.getModeloFatoById(...)?.urlImagemPrevia
 * já usada pelo carrinho (MockOrderStore.toDesign) — nenhuma segunda mapeadora
 * de imagem é introduzida.
 */
@Composable
private fun SuitOrderCard(order: Pedido, onClick: () -> Unit) {
    val design = order.designsFato.firstOrNull()
    SuitCard(onClick = onClick, padding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SuitImageContainer(
                image = suitImageResource(design?.previewImageKey().orEmpty()),
                contentDescription = design?.nome,
                context = SuitImageContext.Thumbnail,
                modifier = Modifier.width(64.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Pedido #${order.numero}", style = SuitTextStyles.titleMedium, color = SuitColors.Ink)
                        Text(
                            design?.nome ?: "Fato personalizado",
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Slate,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    SuitStatusBadge(text = order.estado.shortLabel(), kind = order.estado.toBadgeKind())
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(formatMzn(order.total), style = SuitTextStyles.titleMedium, color = SuitColors.Ink)
                    if (order.criadoEm.isNotBlank()) {
                        Text(order.criadoEm, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
                    }
                }
            }
        }
    }
}

private fun DesignFato.previewImageKey(): String =
    MockCatalogStore.getModeloFatoById(idModeloBase)?.urlImagemPrevia.orEmpty()
