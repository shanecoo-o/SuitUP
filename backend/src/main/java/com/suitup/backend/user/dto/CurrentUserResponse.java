package com.suitup.backend.user.dto;

import com.suitup.backend.user.RoleCode;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record CurrentUserResponse(
    UUID id,
    String fullName,
    String email,
    String phone,
    boolean enabled,
    Set<RoleCode> roles,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
