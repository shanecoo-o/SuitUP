package com.suitup.backend.dashboard.dto;

import com.suitup.backend.payment.PaymentMethod;
import com.suitup.backend.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DashboardPaymentSummaryResponse(
    UUID paymentId,
    UUID orderId,
    String orderNumber,
    String customerName,
    PaymentMethod method,
    PaymentStatus status,
    BigDecimal amount,
    String currency,
    String transactionReference,
    OffsetDateTime submittedAt
) {
}
