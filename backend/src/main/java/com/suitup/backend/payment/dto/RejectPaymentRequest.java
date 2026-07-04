package com.suitup.backend.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record RejectPaymentRequest(
    UUID reviewedByUserId,
    @NotBlank @Size(max = 4000) String rejectionReason,
    @Size(max = 4000) String note
) {
}
