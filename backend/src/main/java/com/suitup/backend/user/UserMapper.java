package com.suitup.backend.user;

import com.suitup.backend.user.dto.CurrentUserResponse;
import com.suitup.backend.user.dto.UserSummaryResponse;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserSummaryResponse toSummary(UserEntity entity) {
        return new UserSummaryResponse(
            entity.getId(),
            entity.getFullName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.isEnabled(),
            roleCodes(entity)
        );
    }

    public CurrentUserResponse toCurrentUser(UserEntity entity) {
        return new CurrentUserResponse(
            entity.getId(),
            entity.getFullName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.isEnabled(),
            roleCodes(entity),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    private Set<RoleCode> roleCodes(UserEntity entity) {
        return entity.getRoles().stream()
            .map(RoleEntity::getCode)
            .collect(Collectors.toUnmodifiableSet());
    }
}
