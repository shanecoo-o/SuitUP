package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.suitup.app.data.mock.MockCatalogStore
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.session.AuthRuntime
import com.suitup.app.domain.model.EstadoPedido
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.ui.screens.admin.AdminCatalogScreen
import com.suitup.app.ui.screens.admin.AdminCatalogScreenModel
import com.suitup.app.ui.screens.admin.AdminDashboardScreen
import com.suitup.app.ui.screens.admin.AdminDashboardStats
import com.suitup.app.ui.screens.admin.AdminOrderDetailsScreen
import com.suitup.app.ui.screens.admin.AdminOrdersScreen
import com.suitup.app.ui.screens.admin.AdminPaymentsScreen
import com.suitup.app.ui.screens.admin.AdminSuitFormScreen
import com.suitup.app.ui.screens.admin.AdminSuitFormScreenModel
import kotlinx.coroutines.launch

class AdminDashboardVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
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
            recentOrders = orders,
            onBack = {
                coroutineScope.launch {
                    AuthRuntime.sessionManager.logout()
                    navigator.replaceAll(LoginVoyagerScreen())
                }
            },
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
        val screenModel = rememberScreenModel { AdminCatalogScreenModel() }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.sessionExpired) {
            if (state.sessionExpired) {
                screenModel.consumeSessionExpired()
                AuthRuntime.sessionManager.logout()
                navigator.replaceAll(LoginVoyagerScreen())
            }
        }

        AdminCatalogScreen(
            models = state.models,
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            successMessage = state.successMessage,
            isUsingMockFallback = state.isUsingMockFallback,
            pendingModelId = state.pendingModelId,
            onBack = { navigator.pop() },
            onAddSuit = { navigator.push(AdminSuitFormVoyagerScreen()) },
            onEditSuit = { id -> navigator.push(AdminSuitFormVoyagerScreen(id)) },
            onDeactivate = { screenModel.setAvailability(it, active = false) },
            onReactivate = { screenModel.setAvailability(it, active = true) },
            onRetry = screenModel::refresh,
        )
    }
}

class AdminSuitFormVoyagerScreen(
    private val modelId: String? = null,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AdminSuitFormScreenModel(modelId) }
        val state by screenModel.state.collectAsState()
        val navigateBack by screenModel.navigateBack.collectAsState()

        LaunchedEffect(navigateBack) {
            if (navigateBack) {
                screenModel.navigationConsumed()
                navigator.pop()
            }
        }

        LaunchedEffect(state.sessionExpired) {
            if (state.sessionExpired) {
                screenModel.consumeSessionExpired()
                AuthRuntime.sessionManager.logout()
                navigator.replaceAll(LoginVoyagerScreen())
            }
        }

        AdminSuitFormScreen(
            title = if (state.isEditMode) "Editar Fato" else "Adicionar Fato",
            state = state.form,
            isEditMode = state.isEditMode,
            isSaving = state.isSaving,
            errorMessage = state.errorMessage,
            onStateChange = screenModel::updateForm,
            onCancel = { navigator.pop() },
            onSave = screenModel::save,
        )
    }
}
