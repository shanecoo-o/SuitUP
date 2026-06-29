package com.suitup.backend.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateSuitModelRequest(
    @NotBlank @Size(max = 200) String name,
    @NotBlank @Size(max = 100) String category,
    @NotNull @Size(max = 4000) String description,
    @NotNull @DecimalMin("0.00") BigDecimal price,
    @Pattern(regexp = "MZN") String currency,
    @NotBlank @Size(max = 100) String fabricType,
    @NotBlank @Size(max = 100) String color,
    @Size(max = 150) String imageKey,
    UUID primaryImageFileId,
    @NotNull Boolean active
) {
}
