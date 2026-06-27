package com.suitup.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * REQUISITOS DE BANCO DE DADOS PARA USUÁRIOS
 * Abstrai as buscas PostgreSQL para a tabela dos perfis cadastrados no ateliê.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Retorna o cadastro se localizado por e-mail (usado pelo Spring Security)
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Valida de antemão se o e-mail inserido já pertence a outro usuário
     */
    boolean existsByEmail(String email);
}
