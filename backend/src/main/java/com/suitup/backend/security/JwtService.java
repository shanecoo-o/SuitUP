package com.suitup.backend.security;

import com.suitup.backend.user.RoleEntity;
import com.suitup.backend.user.UserEntity;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final String ISSUER = "suitup-backend";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final SecurityProperties properties;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, SecurityProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.properties = properties;
    }

    public String generateAccessToken(UserEntity user) {
        return generateToken(user, ACCESS_TOKEN_TYPE, accessTokenDuration());
    }

    public String generateRefreshToken(UserEntity user) {
        return generateToken(user, REFRESH_TOKEN_TYPE, refreshTokenDuration());
    }

    public Jwt parseAccessToken(String token) {
        return parseToken(token, ACCESS_TOKEN_TYPE);
    }

    public Jwt parseRefreshToken(String token) {
        return parseToken(token, REFRESH_TOKEN_TYPE);
    }

    public UUID extractUserId(Jwt jwt) {
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException exception) {
            throw new BadJwtException("JWT subject inválido");
        }
    }

    public long accessTokenExpiresInSeconds() {
        return accessTokenDuration().toSeconds();
    }

    private String generateToken(UserEntity user, String tokenType, Duration duration) {
        Instant now = Instant.now();
        List<String> roles = user.getRoles().stream()
            .map(RoleEntity::getCode)
            .map(Enum::name)
            .sorted()
            .toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(ISSUER)
            .issuedAt(now)
            .expiresAt(now.plus(duration))
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("roles", roles)
            .claim("token_type", tokenType)
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private Jwt parseToken(String token, String expectedType) {
        Jwt jwt = jwtDecoder.decode(token);
        String actualType = jwt.getClaimAsString("token_type");
        if (!expectedType.equals(actualType)) {
            throw new BadJwtException("Tipo de token inválido");
        }
        return jwt;
    }

    private Duration accessTokenDuration() {
        return Duration.ofMinutes(properties.getJwt().getAccessTokenMinutes());
    }

    private Duration refreshTokenDuration() {
        return Duration.ofDays(properties.getJwt().getRefreshTokenDays());
    }
}
