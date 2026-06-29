package com.suitup.backend.upload;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFileEntity, UUID> {
    List<UploadedFileEntity> findByOwnerUserId(UUID ownerUserId);
    List<UploadedFileEntity> findByPurpose(UploadedFilePurpose purpose);
    boolean existsByStoredName(String storedName);
}
