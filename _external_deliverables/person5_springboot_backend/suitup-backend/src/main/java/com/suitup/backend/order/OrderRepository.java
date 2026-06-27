package com.suitup.backend.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * REPOSITÓRIO JPA PARA ENCOMENDAS
 * Resgata e manipula informações de pedidos salvos na nuvem PostgreSQL.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    /**
     * Encontra uma encomenda baseada no ID da transação única d M-Pesa.
     * Crucial para evitar dupla submissão ou fraudes de fatura.
     */
    Optional<OrderEntity> findByMpesaTransactionId(String mpesaTransactionId);

    /**
     * Evita duplicação de pacotes em lote (sync-batch) validando o UUID provisório do cliente offline.
     */
    Optional<OrderEntity> findByClientGeneratedId(String clientGeneratedId);
}
