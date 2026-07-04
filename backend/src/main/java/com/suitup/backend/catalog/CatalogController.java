package com.suitup.backend.catalog;

import com.suitup.backend.catalog.dto.SuitModelResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suit-models")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<SuitModelResponse> listActive() {
        return catalogService.listActive();
    }

    @GetMapping("/{id}")
    public SuitModelResponse getActiveById(@PathVariable UUID id) {
        return catalogService.getActiveById(id);
    }
}
