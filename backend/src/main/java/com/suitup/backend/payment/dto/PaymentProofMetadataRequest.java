package com.suitup.backend.payment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PaymentProofMetadataRequest(
    @NotBlank @Size(max = 255) String originalName,
    @Size(max = 255) String storedName,
    @NotBlank
    @Pattern(regexp = "image/png|image/jpeg|application/pdf")
    String contentType,
    @Positive @Max(10_485_760) long sizeBytes,
    @Size(max = 1000) String storagePath,
    @Size(max = 1000) String publicUrl
) {
}
