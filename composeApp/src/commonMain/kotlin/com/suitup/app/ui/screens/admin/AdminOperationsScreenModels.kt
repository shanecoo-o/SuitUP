package com.suitup.app.ui.screens.admin

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.admin.AdminDataSource
import com.suitup.app.data.admin.AdminOperationsRepository
import com.suitup.app.data.admin.AdminOperationsRuntime
import com.suitup.app.data.admin.AdminOrderStatus
import com.suitup.app.data.admin.AdminTimelineEvent
import com.suitup.app.data.admin.AdminWriteResult
import com.suitup.app.domain.model.AdminDashboardSummary
import com.suitup.app.domain.model.AdminOrderSummary
import com.suitup.app.domain.model.PaymentRecord
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val stats: AdminDashboardStats = AdminDashboardStats(
        totalModels = 0,
        activeModels = 0,
        inactiveModels = 0,
        estimatedRevenueMt = 0,
        totalOrders = 0,
        pendingPayments = 0,
    ),
    val recentOrders: List<AdminOrderSummary> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUsingMockFallback: Boolean = false,
    val sessionExpired: Boolean = false,
)

class AdminDashboardScreenModel(
    private val repository: AdminOperationsRepository = AdminOperationsRuntime.repository,
) : ScreenModel {
    private val _state = MutableStateFlow(AdminDashboardUiState())
    val state: StateFlow<AdminDashboardUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getDashboard()
            val dashboard = result.value
            _state.update {
                it.copy(
                    stats = dashboard?.toUiStats() ?: it.stats,
                    recentOrders = dashboard?.recentOrders.orEmpty(),
                    isLoading = false,
                    errorMessage = result.errorMessage,
                    isUsingMockFallback = result.source == AdminDataSource.MOCK && result.errorMessage != null,
                    sessionExpired = result.sessionExpired,
                )
            }
        }
    }

    fun consumeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }
}

data class AdminOrdersUiState(
    val orders: List<Pedido> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUsingMockFallback: Boolean = false,
    val sessionExpired: Boolean = false,
)

class AdminOrdersScreenModel(
    private val repository: AdminOperationsRepository = AdminOperationsRuntime.repository,
) : ScreenModel {
    private val _state = MutableStateFlow(AdminOrdersUiState())
    val state: StateFlow<AdminOrdersUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getOrders()
            _state.update {
                it.copy(
                    orders = result.value.orEmpty(),
                    isLoading = false,
                    errorMessage = result.errorMessage,
                    isUsingMockFallback = result.source == AdminDataSource.MOCK && result.errorMessage != null,
                    sessionExpired = result.sessionExpired,
                )
            }
        }
    }

    fun consumeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }
}

data class AdminOrderDetailUiState(
    val order: Pedido? = null,
    val timeline: List<AdminTimelineEvent> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val pendingAction: Boolean = false,
    val rejectionReason: String = "",
    val isUsingMockFallback: Boolean = false,
    val sessionExpired: Boolean = false,
)

class AdminOrderDetailScreenModel(
    private val orderId: String,
    private val repository: AdminOperationsRepository = AdminOperationsRuntime.repository,
) : ScreenModel {
    private val _state = MutableStateFlow(AdminOrderDetailUiState())
    val state: StateFlow<AdminOrderDetailUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val orderResult = repository.getOrder(orderId)
            val timelineResult = if (orderResult.value != null) repository.getOrderTimeline(orderId) else null
            _state.update {
                it.copy(
                    order = orderResult.value,
                    timeline = timelineResult?.value.orEmpty(),
                    isLoading = false,
                    errorMessage = orderResult.errorMessage ?: timelineResult?.errorMessage,
                    isUsingMockFallback = orderResult.source == AdminDataSource.MOCK ||
                        timelineResult?.source == AdminDataSource.MOCK,
                    sessionExpired = orderResult.sessionExpired || timelineResult?.sessionExpired == true,
                )
            }
        }
    }

    fun updateStatus(status: AdminOrderStatus) {
        if (_state.value.pendingAction || _state.value.isUsingMockFallback) return
        _state.update { it.copy(pendingAction = true, errorMessage = null, successMessage = null) }
        screenModelScope.launch {
            when (val result = repository.updateOrderStatus(orderId, status)) {
                is AdminWriteResult.Success -> {
                    _state.update {
                        it.copy(
                            order = result.value,
                            pendingAction = false,
                            successMessage = "Estado do pedido actualizado.",
                        )
                    }
                    refreshTimelineOnly()
                }
                is AdminWriteResult.Failure -> applyFailure(result)
            }
        }
    }

    fun confirmPayment() {
        val paymentId = _state.value.order?.pagamento?.idPagamento
        if (paymentId == null) {
            _state.update { it.copy(errorMessage = "Registo de pagamento não encontrado.") }
            return
        }
        runPaymentWrite(paymentId, confirming = true)
    }

    fun rejectPayment() {
        val paymentId = _state.value.order?.pagamento?.idPagamento
        if (paymentId == null) {
            _state.update { it.copy(errorMessage = "Registo de pagamento não encontrado.") }
            return
        }
        if (_state.value.rejectionReason.isBlank()) {
            _state.update { it.copy(errorMessage = "Indique o motivo da rejeição.") }
            return
        }
        runPaymentWrite(paymentId, confirming = false)
    }

    fun setRejectionReason(value: String) {
        _state.update { it.copy(rejectionReason = value, errorMessage = null) }
    }

    fun consumeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }

    private fun runPaymentWrite(paymentId: String, confirming: Boolean) {
        if (_state.value.pendingAction || _state.value.isUsingMockFallback) return
        _state.update { it.copy(pendingAction = true, errorMessage = null, successMessage = null) }
        screenModelScope.launch {
            val result = if (confirming) {
                repository.confirmPayment(paymentId)
            } else {
                repository.rejectPayment(paymentId, _state.value.rejectionReason)
            }
            when (result) {
                is AdminWriteResult.Success -> {
                    val message = if (confirming) {
                        "Pagamento confirmado com sucesso."
                    } else {
                        "Pagamento rejeitado."
                    }
                    _state.update { it.copy(pendingAction = false, successMessage = message) }
                    refreshPreservingMessage()
                }
                is AdminWriteResult.Failure -> applyFailure(result)
            }
        }
    }

    private suspend fun refreshPreservingMessage() {
        val message = _state.value.successMessage
        val result = repository.getOrder(orderId)
        _state.update {
            it.copy(
                order = result.value ?: it.order,
                errorMessage = result.errorMessage,
                successMessage = message,
                sessionExpired = result.sessionExpired,
            )
        }
    }

    private suspend fun refreshTimelineOnly() {
        val result = repository.getOrderTimeline(orderId)
        _state.update {
            it.copy(
                timeline = result.value ?: it.timeline,
                errorMessage = result.errorMessage,
                sessionExpired = result.sessionExpired,
            )
        }
    }

    private fun applyFailure(result: AdminWriteResult.Failure) {
        _state.update {
            it.copy(
                pendingAction = false,
                errorMessage = result.message,
                sessionExpired = result.sessionExpired,
            )
        }
    }
}

data class AdminPaymentListItem(
    val payment: PaymentRecord,
    val order: Pedido?,
)

data class AdminPaymentsUiState(
    val items: List<AdminPaymentListItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val rejectionReason: String = "",
    val pendingPaymentId: String? = null,
    val isUsingMockFallback: Boolean = false,
    val sessionExpired: Boolean = false,
)

class AdminPaymentsScreenModel(
    private val repository: AdminOperationsRepository = AdminOperationsRuntime.repository,
) : ScreenModel {
    private val _state = MutableStateFlow(AdminPaymentsUiState())
    val state: StateFlow<AdminPaymentsUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val payments = repository.getPayments()
            val orders = repository.getOrders()
            val ordersById = orders.value.orEmpty().associateBy { it.id }
            _state.update {
                it.copy(
                    items = payments.value.orEmpty()
                        .sortedBy { payment -> payment.status.sortOrder() }
                        .map { payment -> AdminPaymentListItem(payment, ordersById[payment.orderId]) },
                    isLoading = false,
                    errorMessage = payments.errorMessage ?: orders.errorMessage,
                    isUsingMockFallback = payments.source == AdminDataSource.MOCK || orders.source == AdminDataSource.MOCK,
                    sessionExpired = payments.sessionExpired || orders.sessionExpired,
                )
            }
        }
    }

    fun confirm(paymentId: String) = write(paymentId, confirming = true)

    fun reject(paymentId: String) {
        if (_state.value.rejectionReason.isBlank()) {
            _state.update { it.copy(errorMessage = "Indique o motivo da rejeição.") }
            return
        }
        write(paymentId, confirming = false)
    }

    fun setRejectionReason(value: String) {
        _state.update { it.copy(rejectionReason = value, errorMessage = null) }
    }

    fun consumeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }

    private fun write(paymentId: String, confirming: Boolean) {
        if (_state.value.pendingPaymentId != null || _state.value.isUsingMockFallback) return
        _state.update { it.copy(pendingPaymentId = paymentId, errorMessage = null, successMessage = null) }
        screenModelScope.launch {
            val result = if (confirming) {
                repository.confirmPayment(paymentId)
            } else {
                repository.rejectPayment(paymentId, _state.value.rejectionReason)
            }
            when (result) {
                is AdminWriteResult.Success -> {
                    val message = if (confirming) {
                        "Pagamento confirmado com sucesso."
                    } else {
                        "Pagamento rejeitado."
                    }
                    _state.update { it.copy(pendingPaymentId = null, successMessage = message) }
                    refreshPreservingMessage(message)
                }
                is AdminWriteResult.Failure -> _state.update {
                    it.copy(
                        pendingPaymentId = null,
                        errorMessage = result.message,
                        sessionExpired = result.sessionExpired,
                    )
                }
            }
        }
    }

    private suspend fun refreshPreservingMessage(message: String) {
        val payments = repository.getPayments()
        val orders = repository.getOrders()
        val ordersById = orders.value.orEmpty().associateBy { it.id }
        _state.update {
            it.copy(
                items = payments.value.orEmpty().map { payment ->
                    AdminPaymentListItem(payment, ordersById[payment.orderId])
                },
                errorMessage = payments.errorMessage ?: orders.errorMessage,
                successMessage = message,
                isUsingMockFallback = payments.source == AdminDataSource.MOCK || orders.source == AdminDataSource.MOCK,
                sessionExpired = payments.sessionExpired || orders.sessionExpired,
            )
        }
    }
}

private fun AdminDashboardSummary.toUiStats(): AdminDashboardStats = AdminDashboardStats(
    totalModels = activeSuitModels + inactiveSuitModels,
    activeModels = activeSuitModels,
    inactiveModels = inactiveSuitModels,
    estimatedRevenueMt = confirmedRevenueMt,
    totalOrders = totalOrders,
    pendingOrders = ordersByStatus["RECEIVED"].orZero() + ordersByStatus["IN_ANALYSIS"].orZero(),
    productionOrders = ordersByStatus["IN_PRODUCTION"].orZero(),
    deliveredOrders = ordersByStatus["DELIVERED"].orZero(),
    pendingPayments = pendingPayments,
    confirmedPayments = confirmedPayments,
    rejectedPayments = rejectedPayments,
    confirmedRevenueMt = confirmedRevenueMt,
)

private fun Int?.orZero(): Int = this ?: 0

private fun PaymentStatus.sortOrder(): Int = when (this) {
    PaymentStatus.PENDING -> 0
    PaymentStatus.REJECTED -> 1
    PaymentStatus.CONFIRMED -> 2
}
