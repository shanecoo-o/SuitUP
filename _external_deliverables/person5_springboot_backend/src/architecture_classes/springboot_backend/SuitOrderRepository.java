package mz.ac.unizambeze.suitup.backend.repository;

import mz.ac.unizambeze.suitup.backend.entity.SuitOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * REPOSITÓRIO JPA (Spring Data)
 * Provê automaticamente as operações fundamentais de CRUD (Create, Read, Update, Delete)
 * contra o banco PostgreSQL sem necessidade de escrever SQL cru.
 */
@Repository
public interface SuitOrderRepository extends JpaRepository<SuitOrderEntity, String> {

    /**
     * Encontra uma encomenda baseada no ID da transação do M-Pesa.
     * Útil para Auditoria e Autenticação de Pagamentos no Back-office.
     */
    Optional<SuitOrderEntity> findByMpesaTransactionId(String mpesaTransactionId);
}
