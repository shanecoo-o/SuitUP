package com.suitup.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO DE LOGIN REQUISITADO
 * Modelo de entrada de dados do formulário de autenticação.
 */
public class AuthRequest {

    @NotBlank(message = "E-mail não pode estar em branco")
    @Email(message = "Endereço de e-mail inválido")
    private String email;

    @NotBlank(message = "A palavra-passe é obrigatória para autenticar")
    @Size(min = 6, message = "A palavra-passe precisa ter no mínimo 6 caracteres")
    private String password;

    public AuthRequest() {}

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
