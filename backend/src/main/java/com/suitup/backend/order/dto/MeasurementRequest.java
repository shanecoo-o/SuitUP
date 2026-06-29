package com.suitup.backend.order.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record MeasurementRequest(
    @Positive BigDecimal heightCm,
    @Positive BigDecimal chestCm,
    @Positive BigDecimal waistCm,
    @Positive BigDecimal shouldersCm,
    @Positive BigDecimal sleeveCm,
    @Positive BigDecimal trouserLengthCm,
    @Positive BigDecimal neckCm,
    @Positive BigDecimal hipCm,
    @Size(max = 4000) String notes
) {
}
