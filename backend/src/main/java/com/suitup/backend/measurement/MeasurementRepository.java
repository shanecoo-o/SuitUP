package com.suitup.backend.measurement;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<MeasurementEntity, UUID> {
    Optional<MeasurementEntity> findByOrderId(UUID orderId);
}
