package com.suitup.backend.catalog;

import com.suitup.backend.catalog.dto.CreateSuitModelRequest;
import com.suitup.backend.catalog.dto.SuitModelResponse;
import com.suitup.backend.catalog.dto.UpdateSuitModelRequest;
import com.suitup.backend.common.BadRequestException;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.upload.UploadedFileEntity;
import com.suitup.backend.upload.UploadedFilePurpose;
import com.suitup.backend.upload.UploadedFileRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

    private final SuitModelRepository suitModelRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final CatalogMapper catalogMapper;

    public CatalogService(
        SuitModelRepository suitModelRepository,
        UploadedFileRepository uploadedFileRepository,
        CatalogMapper catalogMapper
    ) {
        this.suitModelRepository = suitModelRepository;
        this.uploadedFileRepository = uploadedFileRepository;
        this.catalogMapper = catalogMapper;
    }

    @Transactional(readOnly = true)
    public List<SuitModelResponse> listActive() {
        return suitModelRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
            .map(catalogMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<SuitModelResponse> listAllForAdmin() {
        return suitModelRepository.findAll().stream().map(catalogMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SuitModelResponse getById(UUID id) {
        return catalogMapper.toResponse(requireById(id));
    }

    @Transactional(readOnly = true)
    public SuitModelResponse getActiveById(UUID id) {
        SuitModelEntity entity = requireById(id);
        if (!entity.isActive()) {
            throw new ResourceNotFoundException("Modelo de fato nÃ£o encontrado: " + id);
        }
        return catalogMapper.toResponse(entity);
    }

    @Transactional
    public SuitModelResponse create(CreateSuitModelRequest request) {
        UploadedFileEntity image = resolveSuitImage(request.primaryImageFileId());
        return catalogMapper.toResponse(suitModelRepository.save(catalogMapper.toEntity(request, image)));
    }

    @Transactional
    public SuitModelResponse update(UUID id, UpdateSuitModelRequest request) {
        SuitModelEntity entity = requireById(id);
        catalogMapper.updateEntity(entity, request, resolveSuitImage(request.primaryImageFileId()));
        return catalogMapper.toResponse(suitModelRepository.save(entity));
    }

    @Transactional
    public SuitModelResponse activate(UUID id) {
        SuitModelEntity entity = requireById(id);
        entity.setActive(true);
        return catalogMapper.toResponse(suitModelRepository.save(entity));
    }

    @Transactional
    public SuitModelResponse deactivate(UUID id) {
        SuitModelEntity entity = requireById(id);
        entity.setActive(false);
        return catalogMapper.toResponse(suitModelRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public SuitModelEntity requireActiveEntity(UUID id) {
        SuitModelEntity model = requireById(id);
        if (!model.isActive()) {
            throw new BadRequestException("O modelo seleccionado está inactivo");
        }
        return model;
    }

    private SuitModelEntity requireById(UUID id) {
        return suitModelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Modelo de fato não encontrado: " + id));
    }

    private UploadedFileEntity resolveSuitImage(UUID fileId) {
        if (fileId == null) {
            return null;
        }
        UploadedFileEntity file = uploadedFileRepository.findById(fileId)
            .orElseThrow(() -> new ResourceNotFoundException("Imagem não encontrada: " + fileId));
        if (file.getPurpose() != UploadedFilePurpose.SUIT_IMAGE) {
            throw new BadRequestException("O ficheiro seleccionado não é uma imagem de fato");
        }
        return file;
    }
}
