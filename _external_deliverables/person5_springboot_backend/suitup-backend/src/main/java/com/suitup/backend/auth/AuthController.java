package com.suitup.backend.auth;

import com.suitup.backend.auth.dto.AuthRequest;
import com.suitup.backend.auth.dto.AuthResponse;
import com.suitup.backend.auth.dto.RegisterRequest;
import com.suitup.backend.user.UserEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * CONTROLADOR INDUSTRIAL DE AUTENTICAÇÃO
 * Centraliza os endpoints de acesso da API do cliente móvel e testes do Postman.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     * Efetua o cadastro seguro de novos clientes ou administradores no PostgreSQL.
     */
    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            UserEntity user = authService.register(request);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Utilizador registado com sucesso no SuitUP!");
            body.put("userId", user.getId());
            body.put("name", user.getName());
            body.put("email", user.getEmail());
            body.put("role", user.getRole().name());
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * POST /api/auth/login
     * Valida credenciais e retorna o token de autenticação JWT simulado.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
    }

    /**
     * GET /api/me
     * Retorna os detalhes de perfil do portador da chave ativa atual.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            UserEntity user = authService.getMe(authHeader);
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("name", user.getName());
            profile.put("email", user.getEmail());
            profile.put("phone", user.getPhone());
            profile.put("role", user.getRole().name());
            profile.put("createdAt", user.getCreatedAt());
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
    }
}
