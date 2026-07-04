package com.suitup.app.data.payment

import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.remote.http.ApiError
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.orders.OrderStatusDto
import com.suitup.app.data.remote.orders.OrderStatusHistoryDto
import com.suitup.app.data.remote.payments.PaymentMethodDto
import com.suitup.app.data.remote.payments.PaymentStatusHistoryDto
import com.suitup.app.data.remote.payments.SubmitPaymentRequestDto
import com.suitup.app.data.remote.upload.StoredFileDto
import com.suitup.app.data.remote.upload.UploadFilePayload
import com.suitup.app.data.repository.remote.RemoteFileRepository
import com.suitup.app.data.repository.remote.RemoteOrderRepository
import com.suitup.app.data.repository.remote.RemotePaymentRepository
import com.suitup.app.domain.model.PaymentRecord
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class PaymentTrackingDataSourceMode {
    MOCK,
    API,
    API_WITH_MOCK_FALLBACK,
}

enum class PaymentTrackingDataSource { API, MOCK }

object PaymentTrackingDataSourceConfig {
    val mode: PaymentTrackingDataSourceMode = PaymentTrackingDataSourceMode.API_WITH_MOCK_FALLBACK
}

enum class CustomerTrackingStatus {
    RECEIVED,
    IN_ANALYSIS,
    MEASUREMENTS_CONFIRMED,
    IN_PRODUCTION,
    READY_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
}

data class CustomerTrackingEvent(
    val status: CustomerTrackingStatus,
    val occurredAt: String,
    val isCurrent: Boolean,
)

sealed interface PaymentSubmitResult {
    data class Success(
        val payment: PaymentRecord?,
        val source: PaymentTrackingDataSource,
    ) : PaymentSubmitResult

    data class Failure(
        val message: String,
        val sessionExpired: Boolean = false,
        val canUseMockFallback: Boolean = false,
    ) : PaymentSubmitResult
}

sealed interface ProofUploadResult {
    data class Success(val file: StoredFileDto) : ProofUploadResult
    data class Failure(
        val message: String,
        val sessionExpired: Boolean = false,
    ) : ProofUploadResult
}

data class CustomerTimelineResult(
    val events: List<CustomerTrackingEvent>,
    val source: PaymentTrackingDataSource,
    val errorMessage: String? = null,
    val sessionExpired: Boolean = false,
)

class PaymentTrackingRepository(
    private val paymentRepository: RemotePaymentRepository,
    private val fileRepository: RemoteFileRepository,
    private val orderRepository: RemoteOrderRepository,
    private val mode: PaymentTrackingDataSourceMode = PaymentTrackingDataSourceConfig.mode,
) {
    private val submitMutex = Mutex()

    suspend fun submitPayment(
        orderId: String,
        amountMzn: Int,
        reference: String,
    ): PaymentSubmitResult = submitMutex.withLock {
        if (mode == PaymentTrackingDataSourceMode.MOCK) {
            return@withLock createMockSubmission(orderId, reference, null)
        }
        val isMockOrder = MockOrderStore.getAllOrders().any { it.id == orderId }
        if (mode == PaymentTrackingDataSourceMode.API_WITH_MOCK_FALLBACK && isMockOrder) {
            return@withLock PaymentSubmitResult.Failure(
                message = "Este pedido existe apenas em modo demo.",
                canUseMockFallback = true,
            )
        }
        val request = SubmitPaymentRequestDto(
            method = PaymentMethodDto.MPESA,
            amount = amountMzn.toDouble(),
            transactionReference = reference.trim(),
            note = "Pagamento M-Pesa submetido pelo cliente SuitUP",
        )
        when (val result = paymentRepository.submit(orderId, request)) {
            is ApiResult.Success -> PaymentSubmitResult.Success(
                payment = result.value,
                source = PaymentTrackingDataSource.API,
            )
            is ApiResult.Failure -> PaymentSubmitResult.Failure(
                message = result.error.paymentMessage(),
                sessionExpired = result.error is ApiError.Unauthorized,
                canUseMockFallback = mode == PaymentTrackingDataSourceMode.API_WITH_MOCK_FALLBACK &&
                    isMockOrder && result.error.canUseMockFallback(),
            )
        }
    }

    suspend fun submitMockPayment(
        orderId: String,
        reference: String,
        proofName: String?,
    ): PaymentSubmitResult = submitMutex.withLock {
        if (mode == PaymentTrackingDataSourceMode.API) {
            return@withLock PaymentSubmitResult.Failure("Não foi possível submeter o pagamento.")
        }
        createMockSubmission(orderId, reference, proofName)
    }

    private fun createMockSubmission(
        orderId: String,
        reference: String,
        proofName: String?,
    ): PaymentSubmitResult {
        if (MockOrderStore.submitMockPayment(orderId, reference, proofName) == null) {
            return PaymentSubmitResult.Failure("Pedido não encontrado.")
        }
        return PaymentSubmitResult.Success(payment = null, source = PaymentTrackingDataSource.MOCK)
    }

    suspend fun uploadPaymentProof(
        orderId: String,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ): ProofUploadResult {
        val validationError = validateProof(filename, contentType, bytes)
        if (validationError != null) return ProofUploadResult.Failure(validationError)
        return when (val result = fileRepository.uploadPaymentProof(
            orderId,
            UploadFilePayload(filename, contentType, bytes),
        )) {
            is ApiResult.Success -> ProofUploadResult.Success(result.value)
            is ApiResult.Failure -> ProofUploadResult.Failure(
                message = result.error.proofMessage(),
                sessionExpired = result.error is ApiError.Unauthorized,
            )
        }
    }

    suspend fun loadTimeline(orderId: String): CustomerTimelineResult {
        val mockOrder = MockOrderStore.getAllOrders().firstOrNull { it.id == orderId }
        if (mode == PaymentTrackingDataSourceMode.MOCK) return mockTimeline(mockOrder)
        if (mode == PaymentTrackingDataSourceMode.API_WITH_MOCK_FALLBACK && mockOrder != null) {
            return mockTimeline(mockOrder)
        }
        return when (val result = orderRepository.getTimeline(orderId)) {
            is ApiResult.Success -> CustomerTimelineResult(
                events = result.value.mapIndexed { index, item ->
                    item.toTrackingEvent(index == result.value.lastIndex)
                },
                source = PaymentTrackingDataSource.API,
            )
            is ApiResult.Failure -> when {
                result.error is ApiError.Unauthorized -> CustomerTimelineResult(
                    events = emptyList(),
                    source = PaymentTrackingDataSource.API,
                    errorMessage = "Sessão expirada. Faça login novamente.",
                    sessionExpired = true,
                )
                mode == PaymentTrackingDataSourceMode.API_WITH_MOCK_FALLBACK && mockOrder != null ->
                    mockTimeline(mockOrder)
                else -> CustomerTimelineResult(
                    events = emptyList(),
                    source = PaymentTrackingDataSource.API,
                    errorMessage = result.error.timelineMessage(),
                )
            }
        }
    }

    suspend fun getPaymentTimeline(orderId: String): ApiResult<List<PaymentStatusHistoryDto>> =
        paymentRepository.getTimeline(orderId)

    private fun mockTimeline(
        order: com.suitup.app.domain.model.Pedido?,
        errorMessage: String? = null,
    ): CustomerTimelineResult = CustomerTimelineResult(
        events = order?.linhaTempo.orEmpty().mapIndexed { index, event ->
            CustomerTrackingEvent(
                status = event.estadoPedido.toTrackingStatus(),
                occurredAt = event.ocorridoEm.orEmpty(),
                isCurrent = index == order?.linhaTempo?.lastIndex,
            )
        },
        source = PaymentTrackingDataSource.MOCK,
        errorMessage = errorMessage,
    )
}

object PaymentTrackingRuntime {
    private var instance: PaymentTrackingRepository? = null

    val repository: PaymentTrackingRepository
        get() = checkNotNull(instance) { "PaymentTrackingRuntime deve ser inicializado antes do uso" }

    fun initialize(
        paymentRepository: RemotePaymentRepository,
        fileRepository: RemoteFileRepository,
        orderRepository: RemoteOrderRepository,
    ) {
        if (instance == null) {
            instance = PaymentTrackingRepository(paymentRepository, fileRepository, orderRepository)
        }
    }
}

private fun validateProof(filename: String, contentType: String, bytes: ByteArray): String? = when {
    filename.isBlank() -> "Seleccione um comprovativo válido."
    contentType !in ALLOWED_PROOF_TYPES -> "Use um ficheiro PNG, JPEG ou PDF."
    bytes.isEmpty() -> "O comprovativo está vazio."
    bytes.size > MAX_PROOF_BYTES -> "O comprovativo deve ter no máximo 10 MB."
    else -> null
}

private fun OrderStatusHistoryDto.toTrackingEvent(isCurrent: Boolean) = CustomerTrackingEvent(
    status = when (newStatus) {
        OrderStatusDto.RECEIVED -> CustomerTrackingStatus.RECEIVED
        OrderStatusDto.IN_ANALYSIS -> CustomerTrackingStatus.IN_ANALYSIS
        OrderStatusDto.MEASUREMENTS_CONFIRMED -> CustomerTrackingStatus.MEASUREMENTS_CONFIRMED
        OrderStatusDto.IN_PRODUCTION -> CustomerTrackingStatus.IN_PRODUCTION
        OrderStatusDto.READY_FOR_DELIVERY -> CustomerTrackingStatus.READY_FOR_DELIVERY
        OrderStatusDto.DELIVERED -> CustomerTrackingStatus.DELIVERED
        OrderStatusDto.CANCELLED -> CustomerTrackingStatus.CANCELLED
    },
    occurredAt = createdAt,
    isCurrent = isCurrent,
)

private fun com.suitup.app.domain.model.EstadoPedido.toTrackingStatus(): CustomerTrackingStatus = when (this) {
    com.suitup.app.domain.model.EstadoPedido.AguardandoPagamento -> CustomerTrackingStatus.RECEIVED
    com.suitup.app.domain.model.EstadoPedido.PagamentoValidado -> CustomerTrackingStatus.MEASUREMENTS_CONFIRMED
    com.suitup.app.domain.model.EstadoPedido.PagamentoRejeitado -> CustomerTrackingStatus.RECEIVED
    com.suitup.app.domain.model.EstadoPedido.EmProducao -> CustomerTrackingStatus.IN_PRODUCTION
    com.suitup.app.domain.model.EstadoPedido.ProntoParaEntrega -> CustomerTrackingStatus.READY_FOR_DELIVERY
    com.suitup.app.domain.model.EstadoPedido.Entregue -> CustomerTrackingStatus.DELIVERED
    com.suitup.app.domain.model.EstadoPedido.Cancelado -> CustomerTrackingStatus.CANCELLED
}

private fun ApiError.paymentMessage(): String = when (this) {
    is ApiError.Unauthorized -> "Sessão expirada. Faça login novamente."
    is ApiError.Forbidden -> "Sem permissão para acessar este pedido."
    is ApiError.NotFound -> "Pedido não encontrado."
    is ApiError.Conflict -> "Esta referência de pagamento já foi utilizada."
    is ApiError.ValidationError -> "Dados de pagamento inválidos. Verifique as informações."
    is ApiError.NetworkUnavailable -> "Não foi possível ligar ao servidor."
    is ApiError.ServerError,
    is ApiError.Unknown -> "Erro inesperado. Tente novamente."
}

private fun ApiError.proofMessage(): String = when (this) {
    is ApiError.Unauthorized -> "Sessão expirada. Faça login novamente."
    is ApiError.Forbidden -> "Sem permissão para acessar este pedido."
    is ApiError.NotFound -> "Pedido não encontrado."
    is ApiError.ValidationError -> "Comprovativo inválido. Use PNG, JPEG ou PDF até 10 MB."
    is ApiError.NetworkUnavailable -> "Não foi possível ligar ao servidor."
    else -> "Não foi possível enviar o comprovativo."
}

private fun ApiError.timelineMessage(): String = when (this) {
    is ApiError.Unauthorized -> "Sessão expirada. Faça login novamente."
    is ApiError.Forbidden -> "Sem permissão para acessar este pedido."
    is ApiError.NotFound -> "Pedido não encontrado."
    is ApiError.NetworkUnavailable -> "Não foi possível ligar ao servidor."
    else -> "Não foi possível carregar o acompanhamento."
}

private fun ApiError.canUseMockFallback(): Boolean =
    this is ApiError.NetworkUnavailable || this is ApiError.ServerError || this is ApiError.Unknown

private val ALLOWED_PROOF_TYPES = setOf("image/png", "image/jpeg", "application/pdf")
private const val MAX_PROOF_BYTES = 10 * 1024 * 1024
