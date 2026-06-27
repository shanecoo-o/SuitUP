package com.suitup.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REQUISITO DE REGISTO DE UTILIZADOR
 * Modelo contendo as informações iniciais para criação de conta.
 */
public class RegisterRequest {

    @NotBlank(message = "O nome é de preenchimento obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve conter entre 3 e 100 letras")
    private String name;

    @NotBlank(message = "Endereço de correio eletrônico obrigatório")
    @Email(message = "Por favor, indique um e-mail com formato válido")
    private String email;

    @NotBlank(message = "Contacto telefónico obrigatório")
    private String phone;

    @NotBlank(message = "Indique uma palavra-passe robusta")
    @Size(min = 6, message = "A palavra-passe deve conter pelo menos 6 caracteres")
    private String password;

    private String role; // CLIENT, ADMIN ou TAILOR (opcional, padrão CLIENT)

    public RegisterRequest() {}

    public RegisterRequest(String name, String email, String phone, String password, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
