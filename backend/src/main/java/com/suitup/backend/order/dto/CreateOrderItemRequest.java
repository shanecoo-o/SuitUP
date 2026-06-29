package com.suitup.backend.order.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateOrderItemRequest(
    @NotNull UUID suitModelId,
    @Size(max = 100) String fabric,
    @Size(max = 100) String color,
    @NotNull JsonNode designSnapshot,
    @Min(1) int quantity
) {
}
