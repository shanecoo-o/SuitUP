package com.suitup.backend.auth.dto;

import com.suitup.backend.user.dto.CurrentUserResponse;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInSeconds,
    CurrentUserResponse user
) {
}
