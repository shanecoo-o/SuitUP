package com.suitup.backend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.suitup.backend.user.RoleCode;
import com.suitup.backend.user.RoleEntity;
import com.suitup.backend.user.UserEntity;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;

class JwtServiceTest {

    private JwtService jwtService;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        SecurityProperties properties = new SecurityProperties();
        properties.getJwt().setSecret("suitup-test-secret-with-at-least-32-bytes");
        properties.getJwt().setAccessTokenMinutes(15);
        properties.getJwt().setRefreshTokenDays(14);

        SecurityConfig config = new SecurityConfig(null, null, null);
        jwtService = new JwtService(
            config.jwtEncoder(properties),
            config.jwtDecoder(properties),
            properties
        );

        RoleEntity customer = new RoleEntity(RoleCode.CUSTOMER, "Cliente");
        customer.setId(UUID.randomUUID());
        user = new UserEntity("Joao Cliente", "joao@example.com", null, "hash");
        user.setId(UUID.randomUUID());
        user.addRole(customer);
    }

    @Test
    void accessTokenContainsIdentityRolesAndExpectedLifetime() {
        Jwt jwt = jwtService.parseAccessToken(jwtService.generateAccessToken(user));

        assertThat(jwtService.extractUserId(jwt)).isEqualTo(user.getId());
        assertThat(jwt.getClaimAsString("email")).isEqualTo(user.getEmail());
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("CUSTOMER");
        assertThat(jwt.getClaimAsString("token_type")).isEqualTo("access");
        assertThat(jwtService.accessTokenExpiresInSeconds()).isEqualTo(900);
    }

    @Test
    void rejectsRefreshTokenWhenAccessTokenIsRequired() {
        String refreshToken = jwtService.generateRefreshToken(user);

        assertThatThrownBy(() -> jwtService.parseAccessToken(refreshToken))
            .isInstanceOf(BadJwtException.class)
            .hasMessageContaining("Tipo de token");
    }
}
