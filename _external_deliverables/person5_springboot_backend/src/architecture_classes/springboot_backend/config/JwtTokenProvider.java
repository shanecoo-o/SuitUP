package mz.ac.unizambeze.suitup.backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SERVIÇO DE TOKEN JWT (Json Web Token)
 * Responsável por codificar, assinar com HMAC SHA-256 e decodificar tokens de acesso seguros.
 */
@Component
public class JwtTokenProvider {

    // Assinatura secreta forte simétrica (deve ser externa em production - dev.env)
    private final String jwtSecret = "ZambezeSuitUPBespokeTailorSecretKeySuperSafe2026NewVersionForMobileApps";
    
    // Tempo de validade do token em milissegundos (8 Horas)
    private final long jwtExpirationInMs = 28800000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Gera um token assinado digitalmente com dados do utilizador e suas roles
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userPrincipal.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai o e-mail (username) que está gravado no corpo criptográfico (claims/subject) do JWT
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Valida se a chave digital e a expiração do token encontram-se válidas na base
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Log do erro de verificação JWT
            System.err.println("Erro na validação do JWT: " + ex.getMessage());
        }
        return false;
    }
}
