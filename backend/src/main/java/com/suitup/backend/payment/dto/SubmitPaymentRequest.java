package com.suitup.backend.payment.dto;

import com.suitup.backend.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record SubmitPaymentRequest(
    @NotNull PaymentMethod method,
    @NotNull @Positive BigDecimal amount,
    @Size(max = 150) String transactionReference,
    UUID proofFileId,
    @Size(max = 4000) String note
) {
}
