package com.suitup.backend.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    List<PaymentEntity> findByOrderId(UUID orderId);
    List<PaymentEntity> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
    List<PaymentEntity> findByStatus(PaymentStatus status);
    List<PaymentEntity> findByStatusOrderByCreatedAtAsc(PaymentStatus status);
    List<PaymentEntity> findTop5ByStatusOrderBySubmittedAtDesc(PaymentStatus status);
    long countByStatus(PaymentStatus status);
    boolean existsByMethodAndTransactionReference(PaymentMethod method, String transactionReference);

    @Query("select sum(p.amount) from PaymentEntity p where p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);
}
