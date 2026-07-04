package com.suitup.backend.catalog;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuitModelRepository extends JpaRepository<SuitModelEntity, UUID> {
    List<SuitModelEntity> findByActiveTrueOrderByCreatedAtDesc();
    List<SuitModelEntity> findByActive(boolean active);
    List<SuitModelEntity> findByNameContainingIgnoreCase(String name);
    long countByActive(boolean active);
}
