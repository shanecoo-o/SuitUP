package com.suitup.app.data.order

import com.suitup.app.data.mock.CheckoutOrderItemDraft
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.remote.http.ApiError
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.orders.CreateOrderItemRequestDto
import com.suitup.app.data.remote.orders.CreateOrderRequestDto
import com.suitup.app.data.remote.orders.FulfillmentTypeDto
import com.suitup.app.data.remote.orders.MeasurementDto
import com.suitup.app.data.repository.remote.RemoteOrderRepository
import com.suitup.app.domain.model.DesignFato
import com.suitup.app.domain.model.Pedido
import com.suitup.app.domain.model.TipoEntrega
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.random.Random

enum class OrderDataSourceMode {
    MOCK,
    API,
    API_WITH_MOCK_FALLBACK,
}

enum class OrderDataSource {
    API,
    MOCK,
}

object OrderDataSourceConfig {
    val mode: OrderDataSourceMode = OrderDataSourceMode.API_WITH_MOCK_FALLBACK
}

data class CustomerOrdersState(
    val orders: List<Pedido> = emptyList(),
    val isLoading: Boolean = false,
    val source: OrderDataSource? = null,
    val errorMessage: String? = null,
    val sessionExpired: Boolean = false,
) {
    val isUsingMockFallback: Boolean
        get() = source == OrderDataSource.MOCK && errorMessage != null
}

sealed interface CreateCustomerOrderResult {
    data class Success(val order: Pedido) : CreateCustomerOrderResult
    data class Failure(
        val message: String,
        val sessionExpired: Boolean = false,
        val canUseMockFallback: Boolean = false,
    ) : CreateCustomerOrderResult
}

data class CustomerOrderDetailResult(
    val order: Pedido?,
    val errorMessage: String? = null,
    val isUsingMockFallback: Boolean = false,
    val sessionExpired: Boolean = false,
)

class CustomerOrderRepository(
    private val remoteRepository: RemoteOrderRepository,
    private val mode: OrderDataSourceMode = OrderDataSourceConfig.mode,
) {
    private val operationMutex = Mutex()
    private val _state = MutableStateFlow(CustomerOrdersState())
    val state: StateFlow<CustomerOrdersState> = _state.asStateFlow()

    private val _lastCreatedOrder = MutableStateFlow<Pedido?>(null)
    val lastCreatedOrder: StateFlow<Pedido?> = _lastCreatedOrder.asStateFlow()

    suspend fun createCurrentCheckout(idempotencyKey: String): CreateCustomerOrderResult =
        operationMutex.withLock {
            if (mode == OrderDataSourceMode.MOCK) {
                val order = MockOrderStore.createOrder()
                rememberCreatedOrder(order, OrderDataSource.MOCK)
                return@withLock CreateCustomerOrderResult.Success(order)
            }

            val request = runCatching { buildCreateRequest() }.getOrElse {
                return@withLock CreateCustomerOrderResult.Failure(
                    "Dados inválidos. Verifique as informações do pedido.",
                )
            }
            when (val result = remoteRepository.create(request, idempotencyKey)) {
                is ApiResult.Success -> {
                    MockOrderStore.completeRemoteCheckout()
                    rememberCreatedOrder(result.value, OrderDataSource.API)
                    CreateCustomerOrderResult.Success(result.value)
                }
                is ApiResult.Failure -> result.error.toCreateFailure()
            }
        }

    suspend fun createMockCheckout(): CreateCustomerOrderResult = operationMutex.withLock {
        if (mode != OrderDataSourceMode.API_WITH_MOCK_FALLBACK && mode != OrderDataSourceMode.MOCK) {
            return@withLock CreateCustomerOrderResult.Failure("Não foi possível criar o pedido.")
        }
        val order = MockOrderStore.createOrder()
        rememberCreatedOrder(order, OrderDataSource.MOCK)
        CreateCustomerOrderResult.Success(order)
    }

    suspend fun refreshOrders() {
        operationMutex.withLock {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                sessionExpired = false,
            )
            _state.value = when (mode) {
                OrderDataSourceMode.MOCK -> mockOrdersState()
                OrderDataSourceMode.API,
                OrderDataSourceMode.API_WITH_MOCK_FALLBACK -> loadRemoteOrders()
            }
        }
    }

    suspend fun getOrder(id: String): CustomerOrderDetailResult {
        if (mode == OrderDataSourceMode.MOCK) {
            return CustomerOrderDetailResult(
                order = cachedOrder(id) ?: MockOrderStore.getAllOrders().firstOrNull { it.id == id },
            )
        }
        return when (val result = remoteRepository.getById(id)) {
            is ApiResult.Success -> {
                mergeOrder(result.value, OrderDataSource.API)
                CustomerOrderDetailResult(order = result.value)
            }
            is ApiResult.Failure -> when (result.error) {
                is ApiError.Unauthorized -> CustomerOrderDetailResult(
                    order = null,
                    errorMessage = "Sessão expirada. Faça login novamente.",
                    sessionExpired = true,
                )
                is ApiError.Forbidden -> CustomerOrderDetailResult(
                    order = null,
                    errorMessage = "Sem permissão para consultar pedido.",
                )
                else -> {
                    val fallback = if (mode == OrderDataSourceMode.API_WITH_MOCK_FALLBACK) {
                        MockOrderStore.getAllOrders().firstOrNull { it.id == id }
                    } else null
                    CustomerOrderDetailResult(
                        order = fallback,
                        errorMessage = orderErrorMessage(result.error, loading = true),
                        isUsingMockFallback = fallback != null,
                    )
                }
            }
        }
    }

    fun cachedOrder(id: String): Pedido? =
        _lastCreatedOrder.value?.takeIf { it.id == id }
            ?: _state.value.orders.firstOrNull { it.id == id }

    fun consumeSessionExpired() {
        _state.value = _state.value.copy(sessionExpired = false)
    }

    private suspend fun loadRemoteOrders(): CustomerOrdersState = when (val result = remoteRepository.getMine()) {
        is ApiResult.Success -> CustomerOrdersState(
            orders = result.value,
            source = OrderDataSource.API,
        )
        is ApiResult.Failure -> when (result.error) {
            is ApiError.Unauthorized -> CustomerOrdersState(
                errorMessage = "Sessão expirada. Faça login novamente.",
                sessionExpired = true,
            )
            is ApiError.Forbidden -> CustomerOrdersState(
                errorMessage = "Sem permissão para consultar pedidos.",
            )
            else -> if (mode == OrderDataSourceMode.API_WITH_MOCK_FALLBACK) {
                mockOrdersState(errorMessage = "Não foi possível carregar os pedidos.")
            } else {
                CustomerOrdersState(errorMessage = "Não foi possível carregar os pedidos.")
            }
        }
    }

    private fun mockOrdersState(errorMessage: String? = null): CustomerOrdersState = CustomerOrdersState(
        orders = MockOrderStore.getAllOrders(),
        source = OrderDataSource.MOCK,
        errorMessage = errorMessage,
    )

    private fun rememberCreatedOrder(order: Pedido, source: OrderDataSource) {
        _lastCreatedOrder.value = order
        _state.value = CustomerOrdersState(
            orders = listOf(order) + _state.value.orders.filterNot { it.id == order.id },
            source = source,
        )
    }

    private fun mergeOrder(order: Pedido, source: OrderDataSource) {
        _state.value = _state.value.copy(
            orders = listOf(order) + _state.value.orders.filterNot { it.id == order.id },
            source = source,
        )
    }

    private fun buildCreateRequest(): CreateOrderRequestDto {
        val checkout = MockOrderStore.checkoutDraft.value
        val items = MockOrderStore.getCheckoutOrderItems()
        require(items.isNotEmpty())
        val measurement = checkout.medidas
        return CreateOrderRequestDto(
            customerName = checkout.cliente.nome,
            customerPhone = checkout.cliente.telefone,
            customerEmail = checkout.cliente.email.takeIf(String::isNotBlank),
            fulfillmentType = if (checkout.tipoEntrega == TipoEntrega.Entrega) {
                FulfillmentTypeDto.DELIVERY
            } else {
                FulfillmentTypeDto.PICKUP
            },
            deliveryAddress = checkout.enderecoEntrega?.let { address ->
                listOfNotNull(
                    address.rua,
                    address.bairro,
                    address.cidade,
                    address.referencia?.takeIf(String::isNotBlank),
                ).joinToString(", ")
            },
            pickupLocation = checkout.pontoLevantamento?.let { "${it.nome}, ${it.endereco}" },
            notes = measurement.observacoes.takeIf(String::isNotBlank),
            items = items.map(CheckoutOrderItemDraft::toRequest),
            measurement = MeasurementDto(
                heightCm = measurement.alturaCm.requiredMeasurement(),
                chestCm = measurement.peitoCm.requiredMeasurement(),
                waistCm = measurement.cinturaCm.requiredMeasurement(),
                shouldersCm = measurement.ombrosCm.requiredMeasurement(),
                sleeveCm = measurement.mangaCm.requiredMeasurement(),
                trouserLengthCm = measurement.calcaCm.requiredMeasurement(),
                neckCm = measurement.pescocoCm.optionalMeasurement(),
                hipCm = measurement.quadrilCm.optionalMeasurement(),
                notes = measurement.observacoes.takeIf(String::isNotBlank),
            ),
        )
    }
}

object OrderRuntime {
    private var customerOrderRepository: CustomerOrderRepository? = null

    val repository: CustomerOrderRepository
        get() = checkNotNull(customerOrderRepository) {
            "OrderRuntime deve ser inicializado antes de usar pedidos"
        }

    fun initialize(remoteRepository: RemoteOrderRepository) {
        if (customerOrderRepository == null) {
            customerOrderRepository = CustomerOrderRepository(remoteRepository)
        }
    }
}

fun generateOrderIdempotencyKey(): String = buildString {
    append("suitup-mobile-")
    repeat(32) { append(IDEMPOTENCY_ALPHABET[Random.nextInt(IDEMPOTENCY_ALPHABET.length)]) }
}

private fun CheckoutOrderItemDraft.toRequest(): CreateOrderItemRequestDto = CreateOrderItemRequestDto(
    suitModelId = design.idModeloBase,
    fabric = design.tecido.nome,
    color = design.cor.nome,
    designSnapshot = design.toSnapshot(),
    quantity = quantity,
)

private fun DesignFato.toSnapshot() = buildJsonObject {
    put("modelId", idModeloBase)
    put("modelName", nome)
    put("fabricId", tecido.id)
    put("fabric", tecido.nome)
    put("colorId", cor.id)
    put("color", cor.nome)
    put("collar", partes.gola.name)
    put("lapel", partes.lapela.name)
    put("pocket", partes.bolso.name)
    put("buttons", partes.botoes.name)
    put("sleeves", partes.mangas.name)
    put("lining", partes.forro.name)
    put("back", partes.costas.name)
    put("widthAdjustment", partes.ajusteLargura)
}

private fun String.requiredMeasurement(): Double =
    replace(',', '.').toDoubleOrNull()?.takeIf { it > 0.0 } ?: error("Invalid measurement")

private fun String.optionalMeasurement(): Double? =
    takeIf(String::isNotBlank)?.replace(',', '.')?.toDoubleOrNull()?.takeIf { it > 0.0 }

private fun ApiError.toCreateFailure(): CreateCustomerOrderResult.Failure =
    CreateCustomerOrderResult.Failure(
        message = orderErrorMessage(this, loading = false),
        sessionExpired = this is ApiError.Unauthorized,
        canUseMockFallback = this is ApiError.NetworkUnavailable ||
            this is ApiError.ServerError ||
            this is ApiError.Unknown,
    )

private fun orderErrorMessage(error: ApiError, loading: Boolean): String = when (error) {
    is ApiError.Unauthorized -> "Sessão expirada. Faça login novamente."
    is ApiError.Forbidden -> if (loading) "Sem permissão para consultar pedido." else "Sem permissão para criar pedido."
    is ApiError.ValidationError -> if (loading) "Pedido inválido." else "Dados inválidos. Verifique as informações do pedido."
    is ApiError.NetworkUnavailable -> "Não foi possível ligar ao servidor."
    is ApiError.NotFound -> if (loading) "Pedido não encontrado." else "Não foi possível criar o pedido."
    is ApiError.Conflict -> "Já existe um pedido para esta tentativa. Tente novamente."
    is ApiError.ServerError,
    is ApiError.Unknown -> if (loading) "Não foi possível carregar o pedido." else "Erro inesperado. Tente novamente."
}

private const val IDEMPOTENCY_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"
