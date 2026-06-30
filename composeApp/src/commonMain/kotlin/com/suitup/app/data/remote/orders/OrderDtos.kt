package com.suitup.app.data.remote.orders

import com.suitup.app.data.remote.payments.PaymentDto
import com.suitup.app.data.remote.payments.PaymentStatusDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
enum class FulfillmentTypeDto { DELIVERY, PICKUP }

@Serializable
enum class OrderStatusDto {
    RECEIVED,
    IN_ANALYSIS,
    MEASUREMENTS_CONFIRMED,
    IN_PRODUCTION,
    READY_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
}

@Serializable
data class MeasurementDto(
    val id: String? = null,
    val heightCm: Double,
    val chestCm: Double,
    val waistCm: Double,
    val shouldersCm: Double,
    val sleeveCm: Double,
    val trouserLengthCm: Double,
    val neckCm: Double? = null,
    val hipCm: Double? = null,
    val notes: String? = null,
)

@Serializable
data class CreateOrderItemRequestDto(
    val suitModelId: String,
    val fabric: String? = null,
    val color: String? = null,
    val designSnapshot: JsonElement,
    val quantity: Int,
)

@Serializable
data class CreateOrderRequestDto(
    val customerUserId: String? = null,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String? = null,
    val fulfillmentType: FulfillmentTypeDto,
    val deliveryAddress: String? = null,
    val pickupLocation: String? = null,
    val notes: String? = null,
    val idempotencyKey: String? = null,
    val items: List<CreateOrderItemRequestDto>,
    val measurement: MeasurementDto,
)

@Serializable
data class OrderItemDto(
    val id: String,
    val suitModelId: String? = null,
    val suitName: String,
    val category: String,
    val fabric: String,
    val color: String,
    val designSnapshot: JsonElement,
    val unitPrice: Double,
    val quantity: Int,
    val lineTotal: Double,
)

@Serializable
data class OrderStatusHistoryDto(
    val id: String,
    val oldStatus: OrderStatusDto? = null,
    val newStatus: OrderStatusDto,
    val changedByUserId: String? = null,
    val note: String? = null,
    val createdAt: String,
)

@Serializable
data class OrderDto(
    val id: String,
    val orderNumber: String,
    val customerUserId: String? = null,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String? = null,
    val status: OrderStatusDto,
    val paymentStatus: PaymentStatusDto,
    val fulfillmentType: FulfillmentTypeDto,
    val deliveryAddress: String? = null,
    val pickupLocation: String? = null,
    val notes: String? = null,
    val subtotalAmount: Double,
    val deliveryFee: Double,
    val totalAmount: Double,
    val currency: String,
    val items: List<OrderItemDto> = emptyList(),
    val measurement: MeasurementDto? = null,
    val payments: List<PaymentDto> = emptyList(),
    val statusHistory: List<OrderStatusHistoryDto> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class UpdateOrderStatusRequestDto(
    val status: OrderStatusDto,
    val note: String? = null,
)
