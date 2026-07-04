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
import com.suitup.app.data.session.AuthRuntime
import com.suitup.app.ui.screens.admin.AdminCatalogScreen
import com.suitup.app.ui.screens.admin.AdminCatalogScreenModel
import com.suitup.app.ui.screens.admin.AdminDashboardScreen
import com.suitup.app.ui.screens.admin.AdminDashboardScreenModel
import com.suitup.app.ui.screens.admin.AdminOrderDetailScreenModel
import com.suitup.app.ui.screens.admin.AdminOrderDetailsScreen
import com.suitup.app.ui.screens.admin.AdminOrdersScreen
import com.suitup.app.ui.screens.admin.AdminOrdersScreenModel
import com.suitup.app.ui.screens.admin.AdminPaymentsScreen
import com.suitup.app.ui.screens.admin.AdminPaymentsScreenModel
import com.suitup.app.ui.screens.admin.AdminSuitFormScreen
import com.suitup.app.ui.screens.admin.AdminSuitFormScreenModel
import kotlinx.coroutines.launch

class AdminDashboardVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        val screenModel = rememberScreenModel { AdminDashboardScreenModel() }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.sessionExpired) {
            if (state.sessionExpired) {
                screenModel.consumeSessionExpired()
                AuthRuntime.sessionManager.logout()
                navigator.replaceAll(LoginVoyagerScreen())
            }
        }

        AdminDashboardScreen(
            stats = state.stats,
            recentOrders = state.recentOrders,
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            isUsingMockFallback = state.isUsingMockFallback,
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
            onRetry = screenModel::refresh,
        )
    }
}

class AdminOrdersVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AdminOrdersScreenModel() }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.sessionExpired) {
            if (state.sessionExpired) {
                screenModel.consumeSessionExpired()
                AuthRuntime.sessionManager.logout()
                navigator.replaceAll(LoginVoyagerScreen())
            }
        }

        AdminOrdersScreen(
            orders = state.orders,
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            isUsingMockFallback = state.isUsingMockFallback,
            onBack = { navigator.pop() },
            onOpenDetails = { id -> navigator.push(AdminOrderDetailsVoyagerScreen(id)) },
            onRetry = screenModel::refresh,
        )
    }
}

class AdminPaymentsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AdminPaymentsScreenModel() }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.sessionExpired) {
            if (state.sessionExpired) {
                screenModel.consumeSessionExpired()
                AuthRuntime.sessionManager.logout()
                navigator.replaceAll(LoginVoyagerScreen())
            }
        }

        AdminPaymentsScreen(
            items = state.items,
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            successMessage = state.successMessage,
            rejectionReason = state.rejectionReason,
            pendingPaymentId = state.pendingPaymentId,
            isUsingMockFallback = state.isUsingMockFallback,
            onBack = { navigator.pop() },
            onOpenDetails = { id -> navigator.push(AdminOrderDetailsVoyagerScreen(id)) },
            onConfirm = screenModel::confirm,
            onReject = screenModel::reject,
            onRejectionReasonChange = screenModel::setRejectionReason,
            onRetry = screenModel::refresh,
        )
    }
}

class AdminOrderDetailsVoyagerScreen(
    private val orderId: String,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AdminOrderDetailScreenModel(orderId) }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.sessionExpired) {
            if (state.sessionExpired) {
                screenModel.consumeSessionExpired()
                AuthRuntime.sessionManager.logout()
                navigator.replaceAll(LoginVoyagerScreen())
            }
        }

        val order = state.order
        if (order == null) {
            AdminOrdersScreen(
                orders = emptyList(),
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                isUsingMockFallback = state.isUsingMockFallback,
                onBack = { navigator.pop() },
                onOpenDetails = { id -> navigator.push(AdminOrderDetailsVoyagerScreen(id)) },
                onRetry = screenModel::refresh,
            )
            return
        }

        AdminOrderDetailsScreen(
            order = order,
            timeline = state.timeline,
            errorMessage = state.errorMessage,
            successMessage = state.successMessage,
            rejectionReason = state.rejectionReason,
            pendingAction = state.pendingAction,
            isUsingMockFallback = state.isUsingMockFallback,
            onBack = { navigator.pop() },
            onConfirmPayment = screenModel::confirmPayment,
            onRejectPayment = screenModel::rejectPayment,
            onRejectionReasonChange = screenModel::setRejectionReason,
            onUpdateStatus = screenModel::updateStatus,
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
