package com.suitup.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 200) String fullName,
    @NotBlank @Email @Size(max = 320) String email,
    @Size(max = 32) String phone,
    @NotBlank @Size(min = 8, max = 128) String password
) {
}
