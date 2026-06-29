package com.suitup.backend.catalog;

import com.suitup.backend.catalog.dto.CreateSuitModelRequest;
import com.suitup.backend.catalog.dto.SuitModelResponse;
import com.suitup.backend.catalog.dto.UpdateSuitModelRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/suit-models")
public class AdminCatalogController {

    private final CatalogService catalogService;

    public AdminCatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<SuitModelResponse> listAll() {
        return catalogService.listAllForAdmin();
    }

    @GetMapping("/{id}")
    public SuitModelResponse getById(@PathVariable UUID id) {
        return catalogService.getById(id);
    }

    @PostMapping
    public ResponseEntity<SuitModelResponse> create(
        @Valid @RequestBody CreateSuitModelRequest request
    ) {
        SuitModelResponse response = catalogService.create(request);
        return ResponseEntity.created(URI.create("/api/admin/suit-models/" + response.id()))
            .body(response);
    }

    @PutMapping("/{id}")
    public SuitModelResponse update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateSuitModelRequest request
    ) {
        return catalogService.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public SuitModelResponse activate(@PathVariable UUID id) {
        return catalogService.activate(id);
    }

    @PatchMapping("/{id}/deactivate")
    public SuitModelResponse deactivate(@PathVariable UUID id) {
        return catalogService.deactivate(id);
    }
}
