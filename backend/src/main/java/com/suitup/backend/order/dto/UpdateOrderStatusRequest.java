package com.suitup.backend.order.dto;

import com.suitup.backend.order.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateOrderStatusRequest(
    @NotNull OrderStatus status,
    @Size(max = 4000) String note
) {
}
