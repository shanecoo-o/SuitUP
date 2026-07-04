package mz.ac.unizambeze.suitup.backend.controller;

import mz.ac.unizambeze.suitup.backend.config.JwtTokenProvider;
import mz.ac.unizambeze.suitup.backend.dto.AuthRequest;
import mz.ac.unizambeze.suitup.backend.dto.AuthResponse;
import mz.ac.unizambeze.suitup.backend.dto.RegisterRequest;
import mz.ac.unizambeze.suitup.backend.entity.UserEntity;
import mz.ac.unizambeze.suitup.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

/**
 * CONTROLADOR REST DE AUTENTICAÇÃO
 * Expõe as rotas `/api/auth/register` e `/api/auth/login` para obtenção dos Tokens JWT de segurança.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, 
                          UserRepository userRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * POST /api/auth/login
     * Efetua login das credenciais do utilizador, gerando o Token JWT de acesso.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest loginRequest) {
        try {
            // Efetua a checagem no Spring Security contra a BD
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Gera o JWT criptografado assinado
            String jwt = tokenProvider.generateToken(authentication);
            
            // Busca dados complementares do utilizador cadastrado
            Optional<UserEntity> userOpt = userRepository.findByEmail(loginRequest.getEmail());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno carregando utilizador");
            }
            UserEntity user = userOpt.get();

            return ResponseEntity.ok(new AuthResponse(
                    jwt, 
                    user.getEmail(), 
                    user.getFullName(), 
                    user.getRoles()
            ));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciais inválidas: Por favor, reveja o seu e-mail e palavra-passe.");
        }
    }

    /**
     * POST /api/auth/register
     * Efetua o cadastro de um novo utilizador de forma segura com encriptação SHA-256 de password.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Verifica se o e-mail já existe na base para não duplicar
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("O endereço de e-mail indicado já está registado num utilizador ateliê.");
        }

        // Determina as roles: por padrão é ROLE_CLIENTE, a não ser que seja configurado de outra forma
        HashSet<String> roles = new HashSet<>();
        if (registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()) {
            roles.addAll(registerRequest.getRoles());
        } else {
            roles.add("ROLE_CLIENTE"); // Role comum para app mobile cliente
        }

        // Cria a entidade encriptando a senha fornecida pelo cliente
        UserEntity user = new UserEntity(
                registerRequest.getFullName(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                roles
        );

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Utilizador registado com sucesso sob medida! Pode efetuar login agora.");
    }
}
