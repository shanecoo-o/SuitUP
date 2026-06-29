package com.suitup.backend.payment.dto;

import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ConfirmPaymentRequest(
    UUID reviewedByUserId,
    @Size(max = 4000) String note
) {
}
