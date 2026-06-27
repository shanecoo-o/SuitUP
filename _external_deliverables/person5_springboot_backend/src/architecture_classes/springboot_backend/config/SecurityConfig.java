package mz.ac.unizambeze.suitup.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * CONFIGURAÇÃO GLOBAL DE SEGURANÇA (Spring Security 6.x)
 * Configura o controle de acesso por rotas, define a política stateless do JWT
 * e ativa filtros de segurança para o ecossistema SuitUP.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF pois trabalhamos com JWT (Stateless)
            .csrf(csrf -> csrf.disable())
            
            // Gerenciamento de sessão sem estado (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Regras de autorização por endpoint
            .authorizeHttpRequests(auth -> auth
                // Rotas de Autenticação Aberta
                .requestMatchers("/api/auth/**").permitAll()
                
                // Obter Modelos Base é livre para os clientes explorarem o catálogo
                .requestMatchers(HttpMethod.GET, "/api/models/**").permitAll()
                
                // Acesso às imagens estáticas enviadas (comprovativos e fotos de produtos)
                .requestMatchers("/uploads/**").permitAll()
                
                // Encomendas e comprovativos necessitam de autenticação típica de utilizador
                .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/orders/payment-proof/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN")
                
                // Endpoints Administrativos (Administração/Alfaiate apenas - Validação de pagamento & Alterações de tracking físico)
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/validate-payment").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                
                // Qualquer outro pedido necessita de autenticação básica ativa
                .anyRequest().authenticated()
            );

        // Insere o filtro personalizado do JWT antes do tratamento de usuário/senha
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Encriptação de palavras-passe usando BCrypt com hash adaptativo
        return new BCryptPasswordEncoder();
    }
}
