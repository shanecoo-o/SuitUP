package com.suitup.backend.payment;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistoryEntity, UUID> {
    List<PaymentStatusHistoryEntity> findByPaymentIdOrderByCreatedAtAsc(UUID paymentId);
}
