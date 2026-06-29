package com.suitup.backend.order.dto;

import com.suitup.backend.order.FulfillmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    UUID customerUserId,
    @NotBlank @Size(max = 200) String customerName,
    @NotBlank @Size(max = 32) String customerPhone,
    @Email @Size(max = 320) String customerEmail,
    @NotNull FulfillmentType fulfillmentType,
    @Size(max = 4000) String deliveryAddress,
    @Size(max = 300) String pickupLocation,
    @Size(max = 4000) String notes,
    @Size(max = 150) String idempotencyKey,
    @NotEmpty List<@Valid CreateOrderItemRequest> items,
    @NotNull @Valid MeasurementRequest measurement
) {
}
