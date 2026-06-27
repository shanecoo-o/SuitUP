package com.suitup.app.ui.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.EstadoPedido
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.domain.model.Pedido
import com.suitup.app.domain.model.SuitModel
import com.suitup.app.ui.components.AdminActionCard
import com.suitup.app.ui.components.CheckoutSummaryCard
import com.suitup.app.ui.components.CheckoutSummaryLine
import com.suitup.app.ui.components.EmptyStateCard
import com.suitup.app.ui.components.MetricCard
import com.suitup.app.ui.components.PaymentStatusCard
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumDropdown
import com.suitup.app.ui.components.PremiumTextField
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.StatusChip
import com.suitup.app.ui.components.StatusChipType
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonSize
import com.suitup.app.ui.components.SuitButtonVariant
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitDropdown
import com.suitup.app.ui.components.SuitStatusBadge
import com.suitup.app.ui.components.SuitStatusKind
import com.suitup.app.ui.components.SuitTextField
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMetical
import com.suitup.app.ui.util.formatMzn
import com.suitup.app.ui.util.suitImageResource
import org.jetbrains.compose.resources.painterResource

data class AdminDashboardStats(
    val totalModels: Int,
    val activeModels: Int,
    val inactiveModels: Int,
    val estimatedRevenueMt: Int,
    val totalOrders: Int,
    val pendingOrders: Int = 0,
    val productionOrders: Int = 0,
    val deliveredOrders: Int = 0,
    val pendingPayments: Int,
    val confirmedPayments: Int = 0,
    val rejectedPayments: Int = 0,
    val confirmedRevenueMt: Int = 0,
)

data class AdminSuitFormState(
    val id: String = "",
    val name: String = "",
    val category: String = AdminCatalogOptions.categories.first(),
    val description: String = "",
    val basePrice: String = "",
    val fabricType: String = AdminCatalogOptions.fabrics.first(),
    val color: String = "",
    val available: Boolean = true,
    val imageKey: String = AdminCatalogOptions.imageKeys.first(),
)

object AdminCatalogOptions {
    val categories = listOf("Clássico", "Executivo", "Slim Fit", "Casual", "Premium", "Gala")
    val fabrics = listOf("Lã Premium", "Algodão", "Linho", "Cashmere", "Tweed", "Veludo")
    val availability = listOf(true, false)
    val imageKeys = listOf(
        "suit_classic_black",
        "suit_navy_business",
        "suit_grey_slim",
        "suit_casual_linen",
    )
}

@Composable
fun AdminDashboardScreen(
    stats: AdminDashboardStats,
    recentOrders: List<Pedido> = emptyList(),
    onBack: () -> Unit = {},
    onCatalogClick: () -> Unit = {},
    onAddSuitClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onPaymentsClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        PremiumTopBar(title = "Painel do Administrador", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                SectionHeader(
                    eyebrow = "ADMINISTRADOR",
                    title = "Gestão da loja e encomendas",
                    description = "Visão operacional do catálogo, pedidos e pagamentos.",
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item { MetricCard("Total de pedidos", stats.totalOrders.toString(), Modifier.width(148.dp)) }
                    item { MetricCard("Pendentes", stats.pendingOrders.toString(), Modifier.width(148.dp)) }
                    item { MetricCard("Em produção", stats.productionOrders.toString(), Modifier.width(148.dp)) }
                    item { MetricCard("Entregues", stats.deliveredOrders.toString(), Modifier.width(148.dp)) }
                    item { MetricCard("Pagamentos", stats.pendingPayments.toString(), Modifier.width(148.dp)) }
                    item { MetricCard("Confirmados", stats.confirmedPayments.toString(), Modifier.width(148.dp)) }
                    item { MetricCard("Receita", formatMzn(stats.confirmedRevenueMt), Modifier.width(174.dp)) }
                    item { MetricCard("Modelos activos", stats.activeModels.toString(), Modifier.width(148.dp)) }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader(title = "Acções rápidas")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            AdminActionCard(
                                "Gerir Catálogo",
                                "Modelos e disponibilidade.",
                                onCatalogClick,
                                Modifier.width(220.dp),
                            )
                        }
                        item {
                            AdminActionCard(
                                "Adicionar Fato",
                                "Criar novo modelo.",
                                onAddSuitClick,
                                Modifier.width(220.dp),
                            )
                        }
                        item {
                            AdminActionCard(
                                "Ver Pedidos",
                                "Produção e entrega.",
                                onOrdersClick,
                                Modifier.width(220.dp),
                            )
                        }
                        item {
                            AdminActionCard(
                                "Confirmar Pagamentos",
                                "Validar comprovativos.",
                                onPaymentsClick,
                                Modifier.width(220.dp),
                            )
                        }
                    }
                }
            }

            if (recentOrders.isNotEmpty()) {
                item {
                    SectionHeader(
                        eyebrow = "ACTIVIDADE",
                        title = "Pedidos recentes",
                        actionLabel = "Ver todos",
                        onAction = onOrdersClick,
                    )
                }
                items(recentOrders.take(2), key = { it.id }) { order ->
                    AdminOrderCard(order = order, onOpen = onOrdersClick)
                }
            }

        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    SuitCard(modifier = modifier, padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
            Text(value, style = SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = SuitColors.Gold)
        }
    }
}

@Composable
fun AdminCatalogScreen(
    models: List<SuitModel>,
    onBack: () -> Unit = {},
    onAddSuit: () -> Unit = {},
    onEditSuit: (String) -> Unit = {},
    onDeactivate: (String) -> Unit = {},
    onReactivate: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        PremiumTopBar(title = "Gestão do Catálogo", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                SectionHeader(
                    eyebrow = "CATÁLOGO",
                    title = "${models.count { it.available }} activos, ${models.count { !it.available }} inactivos",
                    actionLabel = "Adicionar Fato",
                    onAction = onAddSuit,
                )
            }

            if (models.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Nenhum modelo encontrado",
                        description = "Adicione o primeiro fato para iniciar o catálogo.",
                        actionLabel = "Adicionar primeiro fato",
                        onAction = onAddSuit,
                    )
                }
            } else {
                items(models, key = { it.id }) { model ->
                    AdminSuitCard(
                        model = model,
                        onEdit = { onEditSuit(model.id) },
                        onToggleAvailability = {
                            if (model.available) onDeactivate(model.id) else onReactivate(model.id)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminSuitCard(
    model: SuitModel,
    onEdit: () -> Unit,
    onToggleAvailability: () -> Unit,
) {
    PremiumCard(padding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            CatalogImage(model.imageKey, model.name)

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(model.name, style = SuitTextStyles.titleMedium, color = SuitColors.Ink, modifier = Modifier.weight(1f))
                    StatusChip(
                        status = if (model.available) StatusChipType.Active else StatusChipType.Inactive,
                    )
                }

                Text(model.category, style = SuitTextStyles.bodySmall, color = SuitColors.Gold)
                Text(formatMzn(model.basePrice), style = SuitTextStyles.titleMedium, color = SuitColors.GoldChampagne)
                Text("Tecido: ${model.fabricType}", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                Text("Cor: ${model.color}", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SecondaryDarkButton(
                        text = "Editar",
                        onClick = onEdit,
                        fullWidth = false,
                        modifier = Modifier.weight(1f),
                    )
                    if (model.available) SecondaryDarkButton(
                        text = if (model.available) "Desactivar" else "Reactivar",
                        onClick = onToggleAvailability,
                        fullWidth = false,
                        modifier = Modifier.weight(1f),
                    ) else PrimaryGoldButton(
                        text = "Reactivar",
                        onClick = onToggleAvailability,
                        fullWidth = false,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
fun AdminSuitFormScreen(
    title: String,
    state: AdminSuitFormState,
    isEditMode: Boolean,
    onStateChange: (AdminSuitFormState) -> Unit,
    onCancel: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        PremiumTopBar(title = title, onBack = onCancel)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionHeader(
                eyebrow = if (isEditMode) "EDIÇÃO" else "NOVO MODELO",
                title = title,
                description = "Configure os dados apresentados no catálogo.",
            )
            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Identificação", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                    PremiumTextField(
                        value = state.name,
                        onValueChange = { onStateChange(state.copy(name = it)) },
                        label = "Nome do fato",
                        placeholder = "Ex: Fato Azul Executivo",
                    )
                    PremiumDropdown(
                        options = AdminCatalogOptions.categories,
                        selectedOption = state.category,
                        onSelect = { onStateChange(state.copy(category = it)) },
                        optionLabel = { it },
                    )
                    PremiumTextField(
                        value = state.description,
                        onValueChange = { onStateChange(state.copy(description = it)) },
                        label = "Descrição",
                        placeholder = "Descrição breve para o catálogo",
                    )
                    PremiumTextField(
                        value = state.basePrice,
                        onValueChange = { onStateChange(state.copy(basePrice = it.filter(Char::isDigit))) },
                        label = "Preço base",
                        placeholder = "8500",
                        keyboardType = KeyboardType.Number,
                    )
                    PremiumDropdown(
                        options = AdminCatalogOptions.fabrics,
                        selectedOption = state.fabricType,
                        onSelect = { onStateChange(state.copy(fabricType = it)) },
                        optionLabel = { it },
                    )
                    PremiumTextField(
                        value = state.color,
                        onValueChange = { onStateChange(state.copy(color = it)) },
                        label = "Cor",
                        placeholder = "Preto",
                    )
                    PremiumDropdown(
                        options = AdminCatalogOptions.availability,
                        selectedOption = state.available,
                        onSelect = { onStateChange(state.copy(available = it)) },
                        optionLabel = { if (it) "Disponível: Sim" else "Disponível: Não" },
                    )
                }
            }

            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Imagem", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                    StatusChip(status = StatusChipType.Analysis, label = "Upload simulado")
                    CatalogImage(state.imageKey, state.name.ifBlank { "Foto seleccionada" }, size = 132)
                    Text("Foto seleccionada: ${state.imageKey}", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                    Text(
                        "A imagem será ligada ao catálogo local de demonstração.",
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Smoke,
                    )
                    PremiumDropdown(
                        options = AdminCatalogOptions.imageKeys,
                        selectedOption = state.imageKey,
                        onSelect = { onStateChange(state.copy(imageKey = it)) },
                        optionLabel = { it },
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SecondaryDarkButton(
                text = "Cancelar",
                onClick = onCancel,
                fullWidth = false,
                modifier = Modifier.weight(1f),
            )
            PrimaryGoldButton(
                text = if (isEditMode) "Actualizar fato" else "Guardar fato",
                onClick = onSave,
                enabled = state.name.isNotBlank() &&
                    state.description.isNotBlank() &&
                    state.basePrice.toIntOrNull()?.let { it > 0 } == true &&
                    state.color.isNotBlank(),
                fullWidth = false,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun AdminOrdersScreen(
    orders: List<Pedido>,
    onBack: () -> Unit = {},
    onOpenDetails: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        PremiumTopBar(title = "Gestão de Pedidos", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader(
                        eyebrow = "PEDIDOS",
                        title = "Gestão de Pedidos",
                        description = "Acompanhe pagamento, produção e entrega.",
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item { StatusChip(StatusChipType.Analysis, label = "Todos: ${orders.size}") }
                        item {
                            StatusChip(
                                StatusChipType.Pending,
                                label = "Pendentes: ${orders.count { it.pagamento.status == PaymentStatus.PENDING }}",
                            )
                        }
                        item {
                            StatusChip(
                                StatusChipType.Production,
                                label = "Produção: ${orders.count { it.estado == EstadoPedido.EmProducao }}",
                            )
                        }
                        item {
                            StatusChip(
                                StatusChipType.Delivered,
                                label = "Entregues: ${orders.count { it.estado == EstadoPedido.Entregue }}",
                            )
                        }
                    }
                }
            }

            if (orders.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Ainda não existem pedidos",
                        description = "Os novos pedidos dos clientes aparecerão aqui.",
                    )
                }
            } else {
                items(orders, key = { it.id }) { order ->
                    AdminOrderCard(order = order, onOpen = { onOpenDetails(order.id) })
                }
            }
        }
    }
}

@Composable
fun AdminPaymentsScreen(
    orders: List<Pedido>,
    onBack: () -> Unit = {},
    onOpenDetails: (String) -> Unit = {},
    onConfirm: (String) -> Unit = {},
    onReject: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        PremiumTopBar(title = "Confirmação de Pagamentos", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader(
                        eyebrow = "PAGAMENTOS",
                        title = "Validação manual M-Pesa",
                        description = "Confirme ou rejeite comprovativos antes da produção.",
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        MetricCard(
                            "Pendentes",
                            orders.count { it.pagamento.status == PaymentStatus.PENDING }.toString(),
                            Modifier.weight(1f),
                        )
                        MetricCard(
                            "Confirmados",
                            orders.count { it.pagamento.status == PaymentStatus.CONFIRMED }.toString(),
                            Modifier.weight(1f),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        MetricCard(
                            "Rejeitados",
                            orders.count { it.pagamento.status == PaymentStatus.REJECTED }.toString(),
                            Modifier.weight(1f),
                        )
                        MetricCard(
                            "Receita confirmada",
                            formatMzn(
                                orders.filter { it.pagamento.status == PaymentStatus.CONFIRMED }
                                    .sumOf { it.total }
                            ),
                            Modifier.weight(1f),
                        )
                    }
                }
            }

            if (orders.isNotEmpty() && orders.none { it.pagamento.status == PaymentStatus.PENDING }) {
                item {
                    EmptyStateCard(
                        title = "Não existem pagamentos pendentes",
                        description = "Os comprovativos que aguardam validação aparecerão em primeiro lugar.",
                    )
                }
            }

            if (orders.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Ainda não existem pagamentos",
                        description = "Os pagamentos submetidos pelos clientes aparecerão aqui.",
                    )
                }
            } else {
                items(orders, key = { it.id }) { order ->
                    AdminPaymentCard(
                        order = order,
                        onOpen = { onOpenDetails(order.id) },
                        onConfirm = { onConfirm(order.id) },
                        onReject = { onReject(order.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun AdminOrderDetailsScreen(
    order: Pedido,
    onBack: () -> Unit = {},
    onConfirmPayment: (String) -> Unit = {},
    onRejectPayment: (String) -> Unit = {},
    onUpdateStatus: (EstadoPedido) -> Unit = {},
) {
    val statusOptions = listOf(
        EstadoPedido.PagamentoValidado,
        EstadoPedido.EmProducao,
        EstadoPedido.ProntoParaEntrega,
        EstadoPedido.Entregue,
        EstadoPedido.Cancelado,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        PremiumTopBar(title = "Detalhes do Pedido", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(
                eyebrow = "PEDIDO #${order.numero}",
                title = order.estado.label,
                description = "Criado em ${order.criadoEm}",
            )
            AdminOrderSummary(order = order)

            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Cliente e entrega", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
                    Text(
                        text = order.cliente?.nome ?: order.idUtilizador,
                        style = SuitTextStyles.bodyMedium,
                        color = SuitColors.Ink,
                    )
                    order.cliente?.let { cliente ->
                        Text(
                            text = "${cliente.telefone} · ${cliente.email}",
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Slate,
                        )
                    }
                    Text(
                        text = deliverySummary(order),
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Slate,
                    )
                    order.medidas?.let { medidas ->
                        Text(
                            text = "Medidas: altura ${medidas.alturaCm} cm, peito ${medidas.peitoCm} cm, cintura ${medidas.cinturaCm} cm, ombros ${medidas.ombrosCm} cm, manga ${medidas.mangaCm} cm, calça ${medidas.calcaCm} cm",
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Slate,
                        )
                        if (medidas.observacoes.isNotBlank()) {
                            Text(
                                text = "Observações: ${medidas.observacoes}",
                                style = SuitTextStyles.bodySmall,
                                color = SuitColors.Slate,
                            )
                        }
                    }
                }
            }

            PaymentStatusCard(status = order.pagamento.status)

            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Pagamento", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
                    Text("Método: M-Pesa manual", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                    Text("Número: ${order.pagamento.numeroMpesa}", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                    Text("Titular: ${order.pagamento.titular}", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                    Text(
                        text = "Referência: ${order.pagamento.referenciaTransaccao ?: order.pagamento.caminhoImagemComprovativo ?: "Sem referência"}",
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Slate,
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrimaryGoldButton(
                            text = "Confirmar pagamento",
                            onClick = { onConfirmPayment(order.id) },
                            enabled = order.pagamento.status == PaymentStatus.PENDING,
                        )
                        SecondaryDarkButton(
                            text = "Rejeitar pagamento",
                            onClick = { onRejectPayment(order.id) },
                            enabled = order.pagamento.status == PaymentStatus.PENDING,
                        )
                    }
                }
            }

            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Actualizar estado", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
                    if (order.pagamento.status == PaymentStatus.CONFIRMED) {
                        PremiumDropdown(
                            options = statusOptions,
                            selectedOption = order.estado.takeIf { it in statusOptions } ?: EstadoPedido.PagamentoValidado,
                            onSelect = onUpdateStatus,
                            optionLabel = { it.label },
                        )
                    } else {
                        Text(
                            text = if (order.pagamento.status == PaymentStatus.REJECTED) {
                                "O pagamento foi rejeitado. O pedido não pode avançar até ser corrigido."
                            } else {
                                "Confirme o pagamento antes de avançar o pedido para produção."
                            },
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Slate,
                        )
                    }
                }
            }

            CheckoutSummaryCard(
                lines = listOf(
                    CheckoutSummaryLine("Subtotal", order.subtotal),
                    CheckoutSummaryLine("Entrega", order.taxaEntrega),
                ),
                totalMzn = order.total,
            )
        }
    }
}

@Composable
private fun AdminOrderCard(order: Pedido, onOpen: () -> Unit) {
    PremiumCard(padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminOrderSummary(order = order)
            SecondaryDarkButton(
                text = "Ver detalhes",
                onClick = onOpen,
            )
        }
    }
}

@Composable
private fun AdminPaymentCard(
    order: Pedido,
    onOpen: () -> Unit,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
) {
    PremiumCard(padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminOrderSummary(order = order)
            PaymentStatusBadge(order.pagamento.status)
            if (order.pagamento.status == PaymentStatus.PENDING) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryGoldButton(
                        text = "Confirmar",
                        onClick = onConfirm,
                        fullWidth = false,
                        modifier = Modifier.weight(1f),
                    )
                    SecondaryDarkButton(
                        text = "Rejeitar",
                        onClick = onReject,
                        fullWidth = false,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                Text(
                    text = if (order.pagamento.status == PaymentStatus.CONFIRMED) {
                        "Pagamento revisto e confirmado."
                    } else {
                        "Pagamento revisto e rejeitado."
                    },
                    style = SuitTextStyles.bodySmall,
                    color = SuitColors.Slate,
                )
            }
            SecondaryDarkButton(
                text = "Abrir pedido",
                onClick = onOpen,
            )
        }
    }
}

@Composable
private fun AdminOrderSummary(order: Pedido) {
    val suitName = order.designsFato.firstOrNull()?.nome ?: "Sem fato"
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text("Pedido #${order.numero}", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
            SuitStatusBadge(text = order.estado.label, kind = order.toAdminBadgeKind())
        }
        Text(
            "${order.cliente?.nome ?: order.idUtilizador} · $suitName",
            style = SuitTextStyles.bodySmall,
            color = SuitColors.Slate,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(formatMzn(order.total), style = SuitTextStyles.titleMedium, color = SuitColors.Gold)
            Text(order.criadoEm, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
        }
    }
}

private fun deliverySummary(order: Pedido): String = when (order.tipoEntrega) {
    com.suitup.app.domain.model.TipoEntrega.Entrega -> {
        val address = order.enderecoEntrega
        if (address == null) {
            "Entrega: morada não informada"
        } else {
            listOfNotNull(
                address.rua,
                address.bairro,
                address.cidade,
                address.referencia?.takeIf { it.isNotBlank() },
            ).joinToString(", ", prefix = "Entrega: ")
        }
    }
    com.suitup.app.domain.model.TipoEntrega.Levantamento -> {
        val point = order.pontoLevantamento
        if (point == null) "Levantamento: ponto não informado"
        else "Levantamento: ${point.nome}, ${point.endereco}"
    }
}

@Composable
private fun PaymentStatusBadge(status: PaymentStatus) {
    SuitStatusBadge(
        text = status.label,
        kind = when (status) {
            PaymentStatus.PENDING -> SuitStatusKind.Pendente
            PaymentStatus.CONFIRMED -> SuitStatusKind.Success
            PaymentStatus.REJECTED -> SuitStatusKind.Error
        },
    )
}

@Composable
private fun PaymentCountBadge(label: String, count: Int, kind: SuitStatusKind) {
    SuitStatusBadge(text = "$label: $count", kind = kind)
}

private fun Pedido.toAdminBadgeKind(): SuitStatusKind = when (estado) {
    EstadoPedido.AguardandoPagamento -> SuitStatusKind.Pendente
    EstadoPedido.PagamentoValidado -> SuitStatusKind.Info
    EstadoPedido.PagamentoRejeitado -> SuitStatusKind.Error
    EstadoPedido.EmProducao -> SuitStatusKind.Info
    EstadoPedido.ProntoParaEntrega -> SuitStatusKind.Info
    EstadoPedido.Entregue -> SuitStatusKind.Success
    EstadoPedido.Cancelado -> SuitStatusKind.Error
}

@Composable
private fun CatalogImage(imageKey: String, description: String, size: Int = 88) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(SuitTheme.shapes.md)
            .background(SuitColors.SurfaceLow),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(suitImageResource(imageKey)),
            contentDescription = description,
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

fun SuitModel.toAdminFormState(): AdminSuitFormState = AdminSuitFormState(
    id = id,
    name = name,
    category = category,
    description = description,
    basePrice = basePrice.toString(),
    fabricType = fabricType,
    color = color,
    available = available,
    imageKey = imageKey,
)

fun AdminSuitFormState.toSuitModel(generatedId: String = id): SuitModel = SuitModel(
    id = generatedId,
    name = name.trim(),
    category = category,
    description = description.trim(),
    basePrice = basePrice.toIntOrNull() ?: 0,
    imageKey = imageKey,
    fabricType = fabricType,
    color = color.trim(),
    available = available,
)
