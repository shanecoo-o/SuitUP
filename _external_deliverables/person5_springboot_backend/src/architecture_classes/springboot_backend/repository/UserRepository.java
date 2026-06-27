package mz.ac.unizambeze.suitup.backend.repository;

import mz.ac.unizambeze.suitup.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITÓRIO JPA de Usuários
 * Provê buscas na tabela 'suit_users' para fins de validação de credenciais de login.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Encontra o utilizador registado através do endereço de correio eletrónico único
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Verifica a existência de um e-mail já registado
     */
    boolean existsByEmail(String email);
}
