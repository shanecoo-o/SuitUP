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
import com.suitup.app.ui.screens.admin.AdminCatalogScreen
import com.suitup.app.ui.screens.admin.AdminDashboardScreen
import com.suitup.app.ui.screens.admin.AdminDashboardStats
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
            pendingPayments = orders.count { it.estado == EstadoPedido.AguardandoPagamento },
        )

        AdminDashboardScreen(
            stats = stats,
            onBack = { navigator.pop() },
            onCatalogClick = { navigator.push(AdminCatalogVoyagerScreen()) },
            onAddSuitClick = { navigator.push(AdminSuitFormVoyagerScreen()) },
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
