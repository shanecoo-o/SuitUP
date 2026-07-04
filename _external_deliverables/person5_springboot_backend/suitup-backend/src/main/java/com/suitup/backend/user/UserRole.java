package com.suitup.backend.user;

/**
 * ENUM DE NÍVEIS DE ACESSO (User Roles)
 * Define os privilégios dentro do sistema SuitUP.
 */
public enum UserRole {
    CLIENT,  // Cliente que configura o terno e submete o pedido
    ADMIN,   // Administrador com acesso completo a validações de M-Pesa e Back-office
    TAILOR   // Alfaiate profissional que atualiza os passos de costura e corte das peças
}
