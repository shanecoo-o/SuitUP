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
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogServiceTest {

    private SuitModelRepository repository;
    private CatalogService service;

    @BeforeEach
    void setUp() {
        repository = mock(SuitModelRepository.class);
        service = new CatalogService(repository, mock(UploadedFileRepository.class), new CatalogMapper());
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
}
