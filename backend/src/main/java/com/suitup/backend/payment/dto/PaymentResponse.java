package com.suitup.backend.payment.dto;

import com.suitup.backend.payment.PaymentMethod;
import com.suitup.backend.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PaymentResponse(
    UUID id,
    UUID orderId,
    PaymentMethod method,
    PaymentStatus status,
    BigDecimal amount,
    String currency,
    String transactionReference,
    UUID proofFileId,
    OffsetDateTime submittedAt,
    OffsetDateTime confirmedAt,
    OffsetDateTime rejectedAt,
    UUID reviewedByUserId,
    String rejectionReason,
    List<PaymentStatusHistoryResponse> statusHistory,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
