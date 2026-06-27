package com.suitup.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * CONFIGURAÇÃO DETALHADA DO SPRING SECURITY 6.x
 * Desativa proteção CSRF para chamadas REST sem estado, configura
 * o decodificador de palavra-passe BCrypt e expõe as rotas sem
 * necessidade de permissão imediata de tokens complexos em fase de ensaio.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Crucial para receber chamadas de APIs externas e Postman
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/orders/**", "/api/me").permitAll() // Expõe endpoints para testes
                .anyRequest().permitAll() // Garante flexibilidade metodológica para os testes da banca
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // Auxilia a vizualização em iframes de demonstração

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Retorna criptografia BCrypt para as senhas cadastradas
    }
}
