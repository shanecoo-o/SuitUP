package com.suitup.backend.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MeasurementResponse(
    UUID id,
    BigDecimal heightCm,
    BigDecimal chestCm,
    BigDecimal waistCm,
    BigDecimal shouldersCm,
    BigDecimal sleeveCm,
    BigDecimal trouserLengthCm,
    BigDecimal neckCm,
    BigDecimal hipCm,
    String notes
) {
}
