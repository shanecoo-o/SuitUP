package com.suitup.backend.upload.dto;

import com.suitup.backend.upload.UploadedFileEntity;
import com.suitup.backend.upload.UploadedFilePurpose;
import java.time.OffsetDateTime;
import java.util.UUID;

public record StoredFileResponse(
    UUID fileId,
    String originalFilename,
    String contentType,
    long sizeBytes,
    UploadedFilePurpose purpose,
    OffsetDateTime createdAt,
    String url
) {
    public static StoredFileResponse from(UploadedFileEntity entity) {
        return new StoredFileResponse(
            entity.getId(),
            entity.getOriginalName(),
            entity.getContentType(),
            entity.getSizeBytes(),
            entity.getPurpose(),
            entity.getCreatedAt(),
            "/api/files/" + entity.getId()
        );
    }
}
