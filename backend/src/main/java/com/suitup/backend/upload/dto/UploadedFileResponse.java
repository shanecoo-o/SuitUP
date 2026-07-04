package com.suitup.backend.upload.dto;

import com.suitup.backend.upload.UploadedFilePurpose;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UploadedFileResponse(
    UUID id,
    UUID ownerUserId,
    UploadedFilePurpose purpose,
    String originalName,
    String storedName,
    String contentType,
    long sizeBytes,
    String storagePath,
    String publicUrl,
    OffsetDateTime createdAt
) {
}
