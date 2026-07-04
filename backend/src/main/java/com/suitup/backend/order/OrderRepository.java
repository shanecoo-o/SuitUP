package com.suitup.backend.order;

import com.suitup.backend.payment.PaymentStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByCustomerUserIdOrderByCreatedAtDesc(UUID customerUserId);
    List<OrderEntity> findByCustomerPhoneOrderByCreatedAtDesc(String phone);
    List<OrderEntity> findByStatus(OrderStatus status);
    List<OrderEntity> findByPaymentStatus(PaymentStatus paymentStatus);
    List<OrderEntity> findTop5ByOrderByCreatedAtDesc();
    Optional<OrderEntity> findByOrderNumber(String orderNumber);
    Optional<OrderEntity> findByIdempotencyKey(String idempotencyKey);
    boolean existsByIdempotencyKey(String idempotencyKey);
    long countByStatus(OrderStatus status);
    long countByPaymentStatus(PaymentStatus paymentStatus);
}
