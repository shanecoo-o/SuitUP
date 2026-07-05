package com.suitup.app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.EstadoPedido
import com.suitup.app.domain.model.ModeloFato
import com.suitup.app.domain.model.Pedido
import com.suitup.app.ui.components.EmptyStateCard
import com.suitup.app.ui.components.MetricCard
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.SuitImageCard
import com.suitup.app.ui.components.SuitPrimaryTopBar
import com.suitup.app.ui.components.SuitStatusBadge
import com.suitup.app.ui.components.rememberSuitNavDensity
import com.suitup.app.ui.icons.ForwardChevronIcon
import com.suitup.app.ui.navigation.LocalSuitNavDensity
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn
import com.suitup.app.ui.util.suitImageResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    pedidosRecentes: List<Pedido>,
    cartItemCount: Int,
    featuredModels: List<ModeloFato> = emptyList(),
    userName: String = "João",
    onCreateNewSuit: () -> Unit = {},
    onFeaturedModelClick: (ModeloFato) -> Unit = {},
    onOrderClick: (Pedido) -> Unit = {},
    onSeeAllOrders: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val pendingPayments = pedidosRecentes.count { it.estado == EstadoPedido.AguardandoPagamento }
    val horizontalPadding = SuitTheme.responsive.horizontalContentPadding

    val listState = rememberLazyListState()
    val navDensity = rememberSuitNavDensity(listState)
    val sharedNavDensity = LocalSuitNavDensity.current
    LaunchedEffect(navDensity) { sharedNavDensity.value = navDensity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.InkBlack),
    ) {
        SuitPrimaryTopBar(
            title = "SuitUP",
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item("greeting") {
                Column(
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Olá, ${userName.substringBefore(' ')}",
                        style = SuitTextStyles.headlineLarge,
                        color = SuitColors.Pearl,
                    )
                    Text(
                        text = "O próximo fato começa nos detalhes.",
                        style = SuitTextStyles.bodyMedium,
                        color = SuitColors.Slate,
                    )
                }
            }

            item("hero") {
                HomeHero(
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    onCreateNewSuit = onCreateNewSuit,
                )
            }

            item("quick-actions") {
                HomeQuickActions(
                    onCatalog = onCreateNewSuit,
                    onCart = onCartClick,
                    onOrders = onSeeAllOrders,
                    onProfile = onProfileClick,
                )
            }

            item("metrics") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        MetricCard(
                            label = "Pedidos",
                            value = pedidosRecentes.size.toString(),
                            supportingText = "recentes",
                            modifier = Modifier.width(136.dp),
                        )
                    }
                    item {
                        MetricCard(
                            label = "Carrinho",
                            value = cartItemCount.toString(),
                            supportingText = "itens",
                            modifier = Modifier.width(136.dp),
                        )
                    }
                    item {
                        MetricCard(
                            label = "Pagamentos",
                            value = pendingPayments.toString(),
                            supportingText = "pendentes",
                            modifier = Modifier.width(136.dp),
                            accent = if (pendingPayments > 0) SuitColors.Warning else SuitColors.Success,
                        )
                    }
                }
            }

            if (featuredModels.isNotEmpty()) {
                item("catalog-header") {
                    SectionHeader(
                        title = "Modelos em destaque",
                        eyebrow = "Catálogo",
                        description = "Escolha uma base e personalize cada detalhe.",
                        actionLabel = "Ver catálogo",
                        onAction = onCreateNewSuit,
                        modifier = Modifier.padding(horizontal = horizontalPadding),
                    )
                }

                item("catalog-models") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = horizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(featuredModels.take(3), key = { it.id }) { model ->
                            SuitImageCard(
                                image = suitImageResource(model.urlImagemPrevia),
                                title = model.nome,
                                subtitle = model.categoria.label,
                                priceMzn = model.precoBase,
                                modifier = Modifier.width(220.dp),
                                onClick = { onFeaturedModelClick(model) },
                            )
                        }
                    }
                }
            }

            item("orders-header") {
                SectionHeader(
                    title = "Pedidos recentes",
                    description = "Acompanhe produção, pagamento e entrega.",
                    actionLabel = "Ver todos",
                    onAction = onSeeAllOrders,
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }

            if (pedidosRecentes.isEmpty()) {
                item("empty-orders") {
                    EmptyStateCard(
                        title = "Ainda não tem pedidos",
                        description = "Personalize o seu primeiro fato e acompanhe tudo por aqui.",
                        actionLabel = "Ver catálogo",
                        onAction = onCreateNewSuit,
                        modifier = Modifier.padding(horizontal = horizontalPadding),
                    )
                }
            } else {
                items(pedidosRecentes.take(2), key = { it.id }) { order ->
                    RecentOrderCard(
                        order = order,
                        onClick = { onOrderClick(order) },
                        modifier = Modifier.padding(horizontal = horizontalPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeQuickActions(
    onCatalog: () -> Unit,
    onCart: () -> Unit,
    onOrders: () -> Unit,
    onProfile: () -> Unit,
) {
    val actions = listOf(
        "Catálogo" to onCatalog,
        "Carrinho" to onCart,
        "Pedidos" to onOrders,
        "Perfil" to onProfile,
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = SuitTheme.responsive.horizontalContentPadding),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(actions, key = { it.first }) { (label, action) ->
            PremiumCard(
                modifier = Modifier
                    .width(116.dp)
                    .clickable(onClick = action),
                padding = 14.dp,
            ) {
                Text(label, style = SuitTextStyles.titleMedium, color = SuitColors.Pearl)
            }
        }
    }
}

@Composable
private fun HomeHero(
    onCreateNewSuit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PremiumCard(modifier = modifier, onClick = onCreateNewSuit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(SuitColors.SlateSurface, SuitColors.WarmBlack)
                    )
                )
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Crie o seu fato à medida",
                    style = SuitTextStyles.headlineLarge,
                    color = SuitColors.Pearl,
                )
                Text(
                    text = "Personalize modelo, tecido e detalhes em poucos passos.",
                    style = SuitTextStyles.bodyMedium,
                    color = SuitColors.Slate,
                )
                PrimaryGoldButton(
                    text = "Criar novo fato",
                    onClick = onCreateNewSuit,
                    fullWidth = false,
                )
            }

            Box(
                modifier = Modifier
                    .width(84.dp)
                    .clip(SuitTheme.shapes.md)
                    .background(SuitColors.WarmBlack),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(suitImageResource("suit_navy_executive")),
                    contentDescription = "Fato azul executivo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
private fun RecentOrderCard(
    order: Pedido,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PremiumCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = "Pedido #${order.numero}",
                    style = SuitTextStyles.titleLarge,
                    color = SuitColors.Pearl,
                )
                Text(
                    text = order.designsFato.firstOrNull()?.nome ?: "Fato personalizado",
                    style = SuitTextStyles.bodySmall,
                    color = SuitColors.Slate,
                )
                Text(
                    text = formatMzn(order.total),
                    style = SuitTextStyles.titleMedium,
                    color = SuitColors.GoldChampagne,
                )
                SuitStatusBadge(
                    text = order.estado.shortLabel(),
                    kind = order.estado.toBadgeKind(),
                )
            }
            ForwardChevronIcon(tint = SuitColors.GoldChampagne, size = 20.dp)
        }
    }
}
