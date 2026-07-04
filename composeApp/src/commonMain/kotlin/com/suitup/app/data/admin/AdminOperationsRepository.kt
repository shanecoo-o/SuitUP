package com.suitup.app.data.admin

import com.suitup.app.data.mock.MockCatalogStore
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.remote.http.ApiError
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.orders.OrderStatusDto
import com.suitup.app.data.remote.orders.OrderStatusHistoryDto
import com.suitup.app.data.remote.orders.UpdateOrderStatusRequestDto
import com.suitup.app.data.repository.remote.RemoteAdminRepository
import com.suitup.app.data.repository.remote.RemoteOrderRepository
import com.suitup.app.data.repository.remote.RemotePaymentRepository
import com.suitup.app.domain.model.AdminDashboardSummary
import com.suitup.app.domain.model.AdminOrderSummary
import com.suitup.app.domain.model.AdminPaymentSummary
import com.suitup.app.domain.model.EstadoPedido
import com.suitup.app.domain.model.PaymentRecord
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.domain.model.Pedido
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class AdminDataSourceMode { MOCK, API, API_WITH_MOCK_FALLBACK }
enum class AdminDataSource { API, MOCK }

object AdminDataSourceConfig {
    val mode: AdminDataSourceMode = AdminDataSourceMode.API_WITH_MOCK_FALLBACK
}

enum class AdminOrderStatus(val label: String) {
    RECEIVED("Recebido"),
    IN_ANALYSIS("Em análise"),
    MEASUREMENTS_CONFIRMED("Medidas confirmadas"),
    IN_PRODUCTION("Em produção"),
    READY_FOR_DELIVERY("Pronto para entrega"),
    DELIVERED("Entregue"),
    CANCELLED("Cancelado");

    fun allowedNext(): List<AdminOrderStatus> = when (this) {
        RECEIVED -> listOf(IN_ANALYSIS, CANCELLED)
        IN_ANALYSIS -> listOf(MEASUREMENTS_CONFIRMED, CANCELLED)
        MEASUREMENTS_CONFIRMED -> listOf(IN_PRODUCTION, CANCELLED)
        IN_PRODUCTION -> listOf(READY_FOR_DELIVERY, CANCELLED)
        READY_FOR_DELIVERY -> listOf(DELIVERED, CANCELLED)
        DELIVERED, CANCELLED -> emptyList()
    }
}

data class AdminTimelineEvent(
    val status: AdminOrderStatus,
    val createdAt: String,
    val note: String?,
)

data class AdminReadResult<T>(
    val value: T?,
    val source: AdminDataSource,
    val errorMessage: String? = null,
    val sessionExpired: Boolean = false,
)

sealed interface AdminWriteResult<out T> {
    data class Success<T>(val value: T) : AdminWriteResult<T>
    data class Failure(
        val message: String,
        val sessionExpired: Boolean = false,
    ) : AdminWriteResult<Nothing>
}

class AdminOperationsRepository(
    private val adminRepository: RemoteAdminRepository,
    private val orderRepository: RemoteOrderRepository,
    private val paymentRepository: RemotePaymentRepository,
    private val mode: AdminDataSourceMode = AdminDataSourceConfig.mode,
) {
    private val writeMutex = Mutex()

    suspend fun getDashboard(): AdminReadResult<AdminDashboardSummary> {
        if (mode == AdminDataSourceMode.MOCK) return AdminReadResult(mockDashboard(), AdminDataSource.MOCK)
        return when (val result = adminRepository.getDashboard()) {
            is ApiResult.Success -> AdminReadResult(result.value, AdminDataSource.API)
            is ApiResult.Failure -> result.error.readResult(
                fallback = ::mockDashboard,
                defaultMessage = "Não foi possível carregar o painel administrativo.",
            )
        }
    }

    suspend fun getOrders(): AdminReadResult<List<Pedido>> {
        if (mode == AdminDataSourceMode.MOCK) {
            return AdminReadResult(MockOrderStore.getAllOrders(), AdminDataSource.MOCK)
        }
        return when (val result = orderRepository.adminGetAll()) {
            is ApiResult.Success -> AdminReadResult(result.value, AdminDataSource.API)
            is ApiResult.Failure -> result.error.readResult(
                fallback = MockOrderStore::getAllOrders,
                defaultMessage = "Não foi possível carregar os pedidos.",
            )
        }
    }

    suspend fun getOrder(orderId: String): AdminReadResult<Pedido> {
        if (mode == AdminDataSourceMode.MOCK) {
            return AdminReadResult(
                MockOrderStore.getAllOrders().firstOrNull { it.id == orderId },
                AdminDataSource.MOCK,
            )
        }
        return when (val result = orderRepository.adminGetById(orderId)) {
            is ApiResult.Success -> AdminReadResult(result.value, AdminDataSource.API)
            is ApiResult.Failure -> result.error.readResult(
                fallback = { MockOrderStore.getAllOrders().firstOrNull { it.id == orderId } },
                defaultMessage = "Não foi possível carregar o pedido.",
            )
        }
    }

    suspend fun getOrderTimeline(orderId: String): AdminReadResult<List<AdminTimelineEvent>> {
        val mockOrder = MockOrderStore.getAllOrders().firstOrNull { it.id == orderId }
        if (mode == AdminDataSourceMode.MOCK) {
            return AdminReadResult(mockOrder.toAdminTimeline(), AdminDataSource.MOCK)
        }
        return when (val result = orderRepository.adminGetTimeline(orderId)) {
            is ApiResult.Success -> AdminReadResult(
                result.value.map(OrderStatusHistoryDto::toAdminTimeline),
                AdminDataSource.API,
            )
            is ApiResult.Failure -> result.error.readResult(
                fallback = { mockOrder.toAdminTimeline() },
                defaultMessage = "Não foi possível carregar o histórico do pedido.",
            )
        }
    }

    suspend fun getPayments(pendingOnly: Boolean = false): AdminReadResult<List<PaymentRecord>> {
        if (mode == AdminDataSourceMode.MOCK) {
            return AdminReadResult(mockPayments(pendingOnly), AdminDataSource.MOCK)
        }
        val result = if (pendingOnly) paymentRepository.adminGetPending() else paymentRepository.adminGetAll()
        return when (result) {
            is ApiResult.Success -> AdminReadResult(result.value, AdminDataSource.API)
            is ApiResult.Failure -> result.error.readResult(
                fallback = { mockPayments(pendingOnly) },
                defaultMessage = "Não foi possível carregar os pagamentos.",
            )
        }
    }

    suspend fun updateOrderStatus(
        orderId: String,
        status: AdminOrderStatus,
        note: String? = null,
    ): AdminWriteResult<Pedido> = writeMutex.withLock {
        if (mode == AdminDataSourceMode.MOCK) {
            MockOrderStore.updateOrderStatus(orderId, status.toMockStatus())
            return@withLock MockOrderStore.getAllOrders().firstOrNull { it.id == orderId }
                ?.let { AdminWriteResult.Success(it) }
                ?: AdminWriteResult.Failure("Registo não encontrado.")
        }
        when (val result = orderRepository.adminUpdateStatus(
            orderId,
            UpdateOrderStatusRequestDto(status.toDto(), note),
        )) {
            is ApiResult.Success -> AdminWriteResult.Success(result.value)
            is ApiResult.Failure -> result.error.writeFailure("Não foi possível alterar o estado do pedido.")
        }
    }

    suspend fun confirmPayment(paymentId: String): AdminWriteResult<PaymentRecord> = writeMutex.withLock {
        if (mode == AdminDataSourceMode.MOCK) {
            val orderId = mockOrderId(paymentId)
            MockOrderStore.confirmPayment(orderId)
            return@withLock mockPayments(false).firstOrNull { it.id == paymentId }
                ?.let { AdminWriteResult.Success(it) }
                ?: AdminWriteResult.Failure("Registo não encontrado.")
        }
        when (val result = paymentRepository.adminConfirm(paymentId, "Pagamento confirmado pelo administrador")) {
            is ApiResult.Success -> AdminWriteResult.Success(result.value)
            is ApiResult.Failure -> result.error.writeFailure("Não foi possível confirmar o pagamento.")
        }
    }

    suspend fun rejectPayment(
        paymentId: String,
        reason: String,
    ): AdminWriteResult<PaymentRecord> = writeMutex.withLock {
        if (reason.isBlank()) return@withLock AdminWriteResult.Failure("Indique o motivo da rejeição.")
        if (mode == AdminDataSourceMode.MOCK) {
            val orderId = mockOrderId(paymentId)
            MockOrderStore.rejectPayment(orderId)
            return@withLock mockPayments(false).firstOrNull { it.id == paymentId }
                ?.let { AdminWriteResult.Success(it) }
                ?: AdminWriteResult.Failure("Registo não encontrado.")
        }
        when (val result = paymentRepository.adminReject(
            paymentId = paymentId,
            reason = reason.trim(),
            note = "Pagamento rejeitado pelo administrador",
        )) {
            is ApiResult.Success -> AdminWriteResult.Success(result.value)
            is ApiResult.Failure -> result.error.writeFailure("Não foi possível rejeitar o pagamento.")
        }
    }

    private fun mockDashboard(): AdminDashboardSummary {
        val models = MockCatalogStore.getAllSuitModels()
        val orders = MockOrderStore.getAllOrders()
        return AdminDashboardSummary(
            totalOrders = orders.size,
            activeSuitModels = models.count { it.available },
            inactiveSuitModels = models.count { !it.available },
            pendingPayments = orders.count { it.pagamento.status == PaymentStatus.PENDING },
            confirmedPayments = orders.count { it.pagamento.status == PaymentStatus.CONFIRMED },
            rejectedPayments = orders.count { it.pagamento.status == PaymentStatus.REJECTED },
            confirmedRevenueMt = orders.filter { it.pagamento.status == PaymentStatus.CONFIRMED }.sumOf { it.total },
            ordersByStatus = orders.groupingBy { it.backendStatus ?: it.estado.name }.eachCount(),
            recentOrders = orders.take(5).map { order ->
                AdminOrderSummary(
                    id = order.id,
                    orderNumber = order.numero,
                    customerName = order.cliente?.nome ?: order.idUtilizador,
                    customerPhone = order.cliente?.telefone.orEmpty(),
                    status = order.backendStatus ?: order.estado.name,
                    paymentStatus = order.pagamento.status,
                    totalMt = order.total,
                    createdAt = order.criadoEm,
                )
            },
            recentPendingPayments = mockPayments(true).take(5).map { payment ->
                val order = orders.firstOrNull { it.id == payment.orderId }
                AdminPaymentSummary(
                    paymentId = payment.id,
                    orderId = payment.orderId,
                    orderNumber = order?.numero ?: payment.orderId,
                    customerName = order?.cliente?.nome ?: order?.idUtilizador.orEmpty(),
                    method = payment.method,
                    status = payment.status,
                    amountMt = payment.amountMt,
                    transactionReference = payment.transactionReference,
                    submittedAt = payment.submittedAt,
                )
            },
        )
    }

    private fun mockPayments(pendingOnly: Boolean): List<PaymentRecord> = MockOrderStore.getAllOrders()
        .map { order ->
            PaymentRecord(
                id = order.pagamento.idPagamento ?: "mock-payment-${order.id}",
                orderId = order.id,
                method = order.pagamento.metodo.name,
                status = order.pagamento.status,
                amountMt = order.total,
                currency = "MZN",
                transactionReference = order.pagamento.referenciaTransaccao,
                proofFileId = order.pagamento.caminhoImagemComprovativo,
                submittedAt = order.actualizadoEm,
                confirmedAt = null,
                rejectedAt = null,
                reviewedByUserId = null,
                rejectionReason = null,
            )
        }
        .filter { !pendingOnly || it.status == PaymentStatus.PENDING }

    private fun mockOrderId(paymentId: String): String =
        paymentId.removePrefix("mock-payment-")

    private fun <T> ApiError.readResult(
        fallback: () -> T?,
        defaultMessage: String,
    ): AdminReadResult<T> {
        val canFallback = mode == AdminDataSourceMode.API_WITH_MOCK_FALLBACK && canUseMockFallback()
        return AdminReadResult(
            value = if (canFallback) fallback() else null,
            source = if (canFallback) AdminDataSource.MOCK else AdminDataSource.API,
            errorMessage = adminMessage(defaultMessage),
            sessionExpired = this is ApiError.Unauthorized,
        )
    }
}

object AdminOperationsRuntime {
    private var instance: AdminOperationsRepository? = null

    val repository: AdminOperationsRepository
        get() = checkNotNull(instance) { "AdminOperationsRuntime deve ser inicializado antes do uso" }

    fun initialize(
        adminRepository: RemoteAdminRepository,
        orderRepository: RemoteOrderRepository,
        paymentRepository: RemotePaymentRepository,
    ) {
        if (instance == null) {
            instance = AdminOperationsRepository(adminRepository, orderRepository, paymentRepository)
        }
    }
}

private fun OrderStatusHistoryDto.toAdminTimeline() = AdminTimelineEvent(
    status = AdminOrderStatus.valueOf(newStatus.name),
    createdAt = createdAt,
    note = note,
)

private fun Pedido?.toAdminTimeline(): List<AdminTimelineEvent> = this?.linhaTempo.orEmpty().map { event ->
    AdminTimelineEvent(
        status = when (event.estadoPedido) {
            EstadoPedido.AguardandoPagamento -> AdminOrderStatus.RECEIVED
            EstadoPedido.PagamentoValidado -> AdminOrderStatus.MEASUREMENTS_CONFIRMED
            EstadoPedido.PagamentoRejeitado -> AdminOrderStatus.RECEIVED
            EstadoPedido.EmProducao -> AdminOrderStatus.IN_PRODUCTION
            EstadoPedido.ProntoParaEntrega -> AdminOrderStatus.READY_FOR_DELIVERY
            EstadoPedido.Entregue -> AdminOrderStatus.DELIVERED
            EstadoPedido.Cancelado -> AdminOrderStatus.CANCELLED
        },
        createdAt = event.ocorridoEm.orEmpty(),
        note = null,
    )
}

private fun AdminOrderStatus.toDto(): OrderStatusDto = OrderStatusDto.valueOf(name)

private fun AdminOrderStatus.toMockStatus(): EstadoPedido = when (this) {
    AdminOrderStatus.RECEIVED -> EstadoPedido.AguardandoPagamento
    AdminOrderStatus.IN_ANALYSIS -> EstadoPedido.AguardandoPagamento
    AdminOrderStatus.MEASUREMENTS_CONFIRMED -> EstadoPedido.PagamentoValidado
    AdminOrderStatus.IN_PRODUCTION -> EstadoPedido.EmProducao
    AdminOrderStatus.READY_FOR_DELIVERY -> EstadoPedido.ProntoParaEntrega
    AdminOrderStatus.DELIVERED -> EstadoPedido.Entregue
    AdminOrderStatus.CANCELLED -> EstadoPedido.Cancelado
}

private fun ApiError.writeFailure(defaultMessage: String): AdminWriteResult.Failure =
    AdminWriteResult.Failure(adminMessage(defaultMessage), this is ApiError.Unauthorized)

private fun ApiError.adminMessage(defaultMessage: String): String = when (this) {
    is ApiError.ValidationError -> "Dados inválidos. Verifique as informações."
    is ApiError.Unauthorized -> "Sessão expirada. Faça login novamente."
    is ApiError.Forbidden -> "Sem permissão para executar esta acção."
    is ApiError.NotFound -> "Registo não encontrado."
    is ApiError.Conflict -> "Esta acção não é permitida no estado actual."
    is ApiError.NetworkUnavailable -> "Não foi possível ligar ao servidor."
    is ApiError.ServerError -> defaultMessage
    is ApiError.Unknown -> "Erro inesperado. Tente novamente."
}

private fun ApiError.canUseMockFallback(): Boolean =
    this is ApiError.NetworkUnavailable || this is ApiError.ServerError || this is ApiError.Unknown
