package com.suitup.backend.order.dto;

import com.suitup.backend.order.OrderStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderStatusHistoryResponse(
    UUID id,
    OrderStatus oldStatus,
    OrderStatus newStatus,
    UUID changedByUserId,
    String note,
    OffsetDateTime createdAt
) {
}
