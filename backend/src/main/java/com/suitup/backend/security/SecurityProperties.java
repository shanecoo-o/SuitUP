package com.suitup.backend.security;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private final Jwt jwt = new Jwt();
    private final Cors cors = new Cors();

    public Jwt getJwt() {
        return jwt;
    }

    public Cors getCors() {
        return cors;
    }

    public static class Jwt {
        private String secret = "";
        private long accessTokenMinutes = 15;
        private long refreshTokenDays = 14;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getAccessTokenMinutes() { return accessTokenMinutes; }
        public void setAccessTokenMinutes(long accessTokenMinutes) { this.accessTokenMinutes = accessTokenMinutes; }
        public long getRefreshTokenDays() { return refreshTokenDays; }
        public void setRefreshTokenDays(long refreshTokenDays) { this.refreshTokenDays = refreshTokenDays; }
    }

    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>();

        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins == null ? new ArrayList<>() : allowedOrigins;
        }
    }
}
