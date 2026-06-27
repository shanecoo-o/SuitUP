package com.suitup.backend.auth.dto;

import java.util.Set;

/**
 * RECONHECIMENTO DE TOKEN COM METADADOS
 * Resposta de sucesso ao cliente contendo o token de portador Bearer JWT e o escopo.
 */
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String email;
    private String name;
    private String role;

    public AuthResponse() {}

    public AuthResponse(String token, String email, String name, String role) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    // Getters & Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
