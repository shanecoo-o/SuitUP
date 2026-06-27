package mz.ac.unizambeze.suitup.backend.dto;

import java.util.Set;

/**
 * DTO para retorno de Login com sucesso contendo o Token JWT gerado e metadados
 */
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String email;
    private String fullName;
    private Set<String> roles;

    public AuthResponse(String token, String email, String fullName, Set<String> roles) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
