package com.suitup.backend.payment.dto;

import com.suitup.backend.payment.PaymentStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentStatusHistoryResponse(
    UUID id,
    PaymentStatus oldStatus,
    PaymentStatus newStatus,
    UUID changedByUserId,
    String note,
    OffsetDateTime createdAt
) {
}
