package com.suitup.backend.upload;

import com.suitup.backend.upload.dto.UploadedFileResponse;
import org.springframework.stereotype.Component;

@Component
public class UploadMapper {

    public UploadedFileResponse toResponse(UploadedFileEntity entity) {
        return new UploadedFileResponse(
            entity.getId(),
            entity.getOwnerUser() == null ? null : entity.getOwnerUser().getId(),
            entity.getPurpose(),
            entity.getOriginalName(),
            entity.getStoredName(),
            entity.getContentType(),
            entity.getSizeBytes(),
            entity.getStoragePath(),
            entity.getPublicUrl(),
            entity.getCreatedAt()
        );
    }
}
