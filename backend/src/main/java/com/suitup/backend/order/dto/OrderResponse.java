package com.suitup.backend.order.dto;

import com.suitup.backend.order.FulfillmentType;
import com.suitup.backend.order.OrderStatus;
import com.suitup.backend.payment.PaymentStatus;
import com.suitup.backend.payment.dto.PaymentResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    String orderNumber,
    UUID customerUserId,
    String customerName,
    String customerPhone,
    String customerEmail,
    OrderStatus status,
    PaymentStatus paymentStatus,
    FulfillmentType fulfillmentType,
    String deliveryAddress,
    String pickupLocation,
    String notes,
    BigDecimal subtotalAmount,
    BigDecimal deliveryFee,
    BigDecimal totalAmount,
    String currency,
    List<OrderItemResponse> items,
    MeasurementResponse measurement,
    List<PaymentResponse> payments,
    List<OrderStatusHistoryResponse> statusHistory,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
