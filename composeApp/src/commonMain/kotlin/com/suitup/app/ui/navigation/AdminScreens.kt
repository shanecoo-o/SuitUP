package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.suitup.app.data.mock.MockCatalogStore
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.domain.model.EstadoPedido
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.ui.screens.admin.AdminCatalogScreen
import com.suitup.app.ui.screens.admin.AdminDashboardScreen
import com.suitup.app.ui.screens.admin.AdminDashboardStats
import com.suitup.app.ui.screens.admin.AdminOrderDetailsScreen
import com.suitup.app.ui.screens.admin.AdminOrdersScreen
import com.suitup.app.ui.screens.admin.AdminPaymentsScreen
import com.suitup.app.ui.screens.admin.AdminSuitFormScreen
import com.suitup.app.ui.screens.admin.AdminSuitFormState
import com.suitup.app.ui.screens.admin.toAdminFormState
import com.suitup.app.ui.screens.admin.toSuitModel

class AdminDashboardVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val models by MockCatalogStore.suitModels.collectAsState()
        val orders by MockOrderStore.orders.collectAsState()

        val stats = AdminDashboardStats(
            totalModels = models.size,
            activeModels = models.count { it.available },
            inactiveModels = models.count { !it.available },
            estimatedRevenueMt = models.filter { it.available }.sumOf { it.basePrice },
            totalOrders = orders.size,
            pendingOrders = orders.count { it.estado == EstadoPedido.AguardandoPagamento },
            productionOrders = orders.count { it.estado == EstadoPedido.EmProducao },
            deliveredOrders = orders.count { it.estado == EstadoPedido.Entregue },
            pendingPayments = orders.count { it.pagamento.status == PaymentStatus.PENDING },
            confirmedPayments = orders.count { it.pagamento.status == PaymentStatus.CONFIRMED },
            rejectedPayments = orders.count { it.pagamento.status == PaymentStatus.REJECTED },
            confirmedRevenueMt = orders.filter { it.pagamento.status == PaymentStatus.CONFIRMED }.sumOf { it.total },
        )

        AdminDashboardScreen(
            stats = stats,
            onBack = { navigator.pop() },
            onCatalogClick = { navigator.push(AdminCatalogVoyagerScreen()) },
            onAddSuitClick = { navigator.push(AdminSuitFormVoyagerScreen()) },
            onOrdersClick = { navigator.push(AdminOrdersVoyagerScreen()) },
            onPaymentsClick = { navigator.push(AdminPaymentsVoyagerScreen()) },
        )
    }
}

class AdminOrdersVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val orders by MockOrderStore.orders.collectAsState()

        AdminOrdersScreen(
            orders = orders,
            onBack = { navigator.pop() },
            onOpenDetails = { id -> navigator.push(AdminOrderDetailsVoyagerScreen(id)) },
        )
    }
}

class AdminPaymentsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val orders by MockOrderStore.orders.collectAsState()
        val paymentOrders = orders.sortedBy { order ->
            when (order.pagamento.status) {
                PaymentStatus.PENDING -> 0
                PaymentStatus.REJECTED -> 1
                PaymentStatus.CONFIRMED -> 2
            }
        }

        AdminPaymentsScreen(
            orders = paymentOrders,
            onBack = { navigator.pop() },
            onOpenDetails = { id -> navigator.push(AdminOrderDetailsVoyagerScreen(id)) },
            onConfirm = { MockOrderStore.confirmPayment(it) },
            onReject = { MockOrderStore.rejectPayment(it) },
        )
    }
}

class AdminOrderDetailsVoyagerScreen(
    private val orderId: String,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val orders by MockOrderStore.orders.collectAsState()
        val order = orders.firstOrNull { it.id == orderId }

        if (order == null) {
            AdminOrdersScreen(
                orders = orders,
                onBack = { navigator.pop() },
                onOpenDetails = { id -> navigator.push(AdminOrderDetailsVoyagerScreen(id)) },
            )
            return
        }

        AdminOrderDetailsScreen(
            order = order,
            onBack = { navigator.pop() },
            onConfirmPayment = { MockOrderStore.confirmPayment(it) },
            onRejectPayment = { MockOrderStore.rejectPayment(it) },
            onUpdateStatus = { status -> MockOrderStore.updateOrderStatus(order.id, status) },
        )
    }
}

class AdminCatalogVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val models by MockCatalogStore.suitModels.collectAsState()

        AdminCatalogScreen(
            models = models,
            onBack = { navigator.pop() },
            onAddSuit = { navigator.push(AdminSuitFormVoyagerScreen()) },
            onEditSuit = { id -> navigator.push(AdminSuitFormVoyagerScreen(id)) },
            onDeactivate = { MockCatalogStore.deactivateSuitModel(it) },
            onReactivate = { MockCatalogStore.reactivateSuitModel(it) },
        )
    }
}

class AdminSuitFormVoyagerScreen(
    private val modelId: String? = null,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val original = modelId?.let { MockCatalogStore.getSuitModelById(it) }
        val isEditMode = original != null
        var formState by remember(modelId) {
            mutableStateOf(original?.toAdminFormState() ?: AdminSuitFormState())
        }

        AdminSuitFormScreen(
            title = if (isEditMode) "Editar Fato" else "Adicionar Fato",
            state = formState,
            isEditMode = isEditMode,
            onStateChange = { formState = it },
            onCancel = { navigator.pop() },
            onSave = {
                if (isEditMode) {
                    MockCatalogStore.updateSuitModel(formState.toSuitModel())
                } else {
                    val id = "admin-${MockCatalogStore.getAllSuitModels().size + 1}"
                    MockCatalogStore.addSuitModel(formState.toSuitModel(generatedId = id))
                }
                navigator.pop()
            },
        )
    }
}
