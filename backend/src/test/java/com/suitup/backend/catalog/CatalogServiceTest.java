package com.suitup.backend.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.suitup.backend.catalog.dto.CreateSuitModelRequest;
import com.suitup.backend.catalog.dto.SuitModelResponse;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.upload.UploadedFileRepository;
import com.suitup.backend.upload.FileStorageService;
import com.suitup.backend.upload.UploadedFileEntity;
import com.suitup.backend.upload.UploadedFilePurpose;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class CatalogServiceTest {

    private SuitModelRepository repository;
    private CatalogService service;
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        repository = mock(SuitModelRepository.class);
        fileStorageService = mock(FileStorageService.class);
        service = new CatalogService(
            repository,
            mock(UploadedFileRepository.class),
            new CatalogMapper(),
            fileStorageService
        );
        when(repository.save(any(SuitModelEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createsActiveModelWithMznDefaults() {
        CreateSuitModelRequest request = new CreateSuitModelRequest(
            "Fato Clássico Preto",
            "Clássico",
            "Corte formal",
            new BigDecimal("8500.00"),
            null,
            "Lã Premium",
            "Preto",
            "suit_classic_black",
            null,
            null
        );

        SuitModelResponse response = service.create(request);

        assertThat(response.currency()).isEqualTo("MZN");
        assertThat(response.active()).isTrue();
        assertThat(response.price()).isEqualByComparingTo("8500.00");
        verify(repository).save(any(SuitModelEntity.class));
    }

    @Test
    void deactivatesExistingModel() {
        UUID id = UUID.randomUUID();
        SuitModelEntity model = new SuitModelEntity();
        model.setActive(true);
        when(repository.findById(id)).thenReturn(Optional.of(model));

        SuitModelResponse response = service.deactivate(id);

        assertThat(response.active()).isFalse();
    }

    @Test
    void publicLookupHidesInactiveModelAsNotFound() {
        UUID id = UUID.randomUUID();
        SuitModelEntity model = new SuitModelEntity();
        model.setActive(false);
        when(repository.findById(id)).thenReturn(Optional.of(model));

        assertThatThrownBy(() -> service.getActiveById(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void adminUploadLinksStoredImageToSuitModel() {
        UUID modelId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        SuitModelEntity model = new SuitModelEntity();
        UploadedFileEntity image = new UploadedFileEntity();
        image.setId(UUID.randomUUID());
        image.setPurpose(UploadedFilePurpose.SUIT_IMAGE);
        image.setOriginalName("fato.png");
        image.setContentType("image/png");
        image.setSizeBytes(8);
        MockMultipartFile file = new MockMultipartFile(
            "file", "fato.png", "image/png", new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47}
        );
        when(repository.findById(modelId)).thenReturn(Optional.of(model));
        when(fileStorageService.store(file, UploadedFilePurpose.SUIT_IMAGE, adminId)).thenReturn(image);

        service.uploadImage(modelId, file, adminId);

        assertThat(model.getPrimaryImageFile()).isSameAs(image);
        verify(repository).save(model);
    }
}
