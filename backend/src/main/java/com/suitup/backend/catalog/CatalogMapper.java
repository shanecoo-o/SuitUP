package com.suitup.backend.catalog;

import com.suitup.backend.catalog.dto.CreateSuitModelRequest;
import com.suitup.backend.catalog.dto.SuitModelResponse;
import com.suitup.backend.catalog.dto.UpdateSuitModelRequest;
import com.suitup.backend.common.MoneyValidator;
import com.suitup.backend.upload.UploadedFileEntity;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {

    public SuitModelEntity toEntity(CreateSuitModelRequest request, UploadedFileEntity primaryImage) {
        SuitModelEntity entity = new SuitModelEntity();
        entity.setName(request.name().trim());
        entity.setCategory(request.category().trim());
        entity.setDescription(request.description().trim());
        entity.setPrice(MoneyValidator.requireNonNegative(request.price(), "price"));
        entity.setCurrency(MoneyValidator.normalizeCurrency(request.currency()));
        entity.setFabricType(request.fabricType().trim());
        entity.setColor(request.color().trim());
        entity.setImageKey(trimToNull(request.imageKey()));
        entity.setPrimaryImageFile(primaryImage);
        entity.setActive(request.active() == null || request.active());
        return entity;
    }

    public void updateEntity(
        SuitModelEntity entity,
        UpdateSuitModelRequest request,
        UploadedFileEntity primaryImage
    ) {
        entity.setName(request.name().trim());
        entity.setCategory(request.category().trim());
        entity.setDescription(request.description().trim());
        entity.setPrice(MoneyValidator.requireNonNegative(request.price(), "price"));
        entity.setCurrency(MoneyValidator.normalizeCurrency(request.currency()));
        entity.setFabricType(request.fabricType().trim());
        entity.setColor(request.color().trim());
        entity.setImageKey(trimToNull(request.imageKey()));
        entity.setPrimaryImageFile(primaryImage);
        entity.setActive(request.active());
    }

    public SuitModelResponse toResponse(SuitModelEntity entity) {
        return new SuitModelResponse(
            entity.getId(),
            entity.getName(),
            entity.getCategory(),
            entity.getDescription(),
            entity.getPrice(),
            entity.getCurrency(),
            entity.getFabricType(),
            entity.getColor(),
            entity.getImageKey(),
            entity.getPrimaryImageFile() == null ? null : entity.getPrimaryImageFile().getId(),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
