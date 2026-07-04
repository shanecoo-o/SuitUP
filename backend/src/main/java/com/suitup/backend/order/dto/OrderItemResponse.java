package com.suitup.backend.order.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
    UUID id,
    UUID suitModelId,
    String suitName,
    String category,
    String fabric,
    String color,
    JsonNode designSnapshot,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal lineTotal
) {
}
