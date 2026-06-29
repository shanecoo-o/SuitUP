package com.suitup.backend.catalog.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SuitModelResponse(
    UUID id,
    String name,
    String category,
    String description,
    BigDecimal price,
    String currency,
    String fabricType,
    String color,
    String imageKey,
    UUID primaryImageFileId,
    boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
