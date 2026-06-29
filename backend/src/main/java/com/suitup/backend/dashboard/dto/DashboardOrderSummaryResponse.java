package com.suitup.backend.dashboard.dto;

import com.suitup.backend.order.OrderStatus;
import com.suitup.backend.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DashboardOrderSummaryResponse(
    UUID id,
    String orderNumber,
    String customerName,
    String customerPhone,
    OrderStatus status,
    PaymentStatus paymentStatus,
    BigDecimal totalAmount,
    String currency,
    OffsetDateTime createdAt
) {
}
