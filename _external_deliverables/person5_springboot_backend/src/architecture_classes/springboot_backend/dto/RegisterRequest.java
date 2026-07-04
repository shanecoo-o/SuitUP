package mz.ac.unizambeze.suitup.backend.dto;

import java.util.Set;

/**
 * DTO para dados de requisição de Registo de Novo Cliente
 */
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private Set<String> roles; // Permite escolher se cadastra cliente ou staff (em teste)

    public RegisterRequest() {}

    public RegisterRequest(String fullName, String email, String password, Set<String> roles) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
