package com.suitup.backend.upload.dto;

import com.suitup.backend.upload.UploadedFilePurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateUploadedFileMetadataRequest(
    UUID ownerUserId,
    @NotNull UploadedFilePurpose purpose,
    @NotBlank @Size(max = 255) String originalName,
    @NotBlank @Size(max = 255) String storedName,
    @NotBlank @Size(max = 150) String contentType,
    @PositiveOrZero long sizeBytes,
    @NotBlank @Size(max = 1000) String storagePath,
    @Size(max = 1000) String publicUrl
) {
}
