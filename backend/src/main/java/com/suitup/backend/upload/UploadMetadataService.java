package com.suitup.backend.upload;

import com.suitup.backend.common.DuplicateResourceException;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.upload.dto.CreateUploadedFileMetadataRequest;
import com.suitup.backend.upload.dto.UploadedFileResponse;
import com.suitup.backend.user.UserEntity;
import com.suitup.backend.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UploadMetadataService {

    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;
    private final UploadMapper uploadMapper;

    public UploadMetadataService(
        UploadedFileRepository uploadedFileRepository,
        UserRepository userRepository,
        UploadMapper uploadMapper
    ) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.userRepository = userRepository;
        this.uploadMapper = uploadMapper;
    }

    @Transactional
    public UploadedFileResponse createMetadata(CreateUploadedFileMetadataRequest request) {
        if (uploadedFileRepository.existsByStoredName(request.storedName())) {
            throw new DuplicateResourceException("Já existe metadata para este nome armazenado");
        }

        UserEntity owner = request.ownerUserId() == null ? null : userRepository.findById(request.ownerUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Proprietário do ficheiro não encontrado"));

        UploadedFileEntity entity = new UploadedFileEntity();
        entity.setOwnerUser(owner);
        entity.setPurpose(request.purpose());
        entity.setOriginalName(request.originalName().trim());
        entity.setStoredName(request.storedName().trim());
        entity.setContentType(request.contentType().trim());
        entity.setSizeBytes(request.sizeBytes());
        entity.setStoragePath(request.storagePath().trim());
        entity.setPublicUrl(trimToNull(request.publicUrl()));
        return uploadMapper.toResponse(uploadedFileRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public UploadedFileEntity requireById(java.util.UUID id) {
        return uploadedFileRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ficheiro não encontrado: " + id));
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
