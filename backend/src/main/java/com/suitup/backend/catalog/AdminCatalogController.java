package com.suitup.backend.catalog;

import com.suitup.backend.catalog.dto.CreateSuitModelRequest;
import com.suitup.backend.catalog.dto.SuitModelResponse;
import com.suitup.backend.catalog.dto.UpdateSuitModelRequest;
import com.suitup.backend.auth.InvalidCredentialsException;
import com.suitup.backend.security.CustomUserDetails;
import com.suitup.backend.upload.dto.StoredFileResponse;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

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

    @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoredFileResponse> uploadImage(
        @PathVariable UUID id,
        @RequestPart("file") MultipartFile file,
        Authentication authentication
    ) {
        CustomUserDetails admin = currentUser(authentication);
        return ResponseEntity.status(201).body(catalogService.uploadImage(id, file, admin.getId()));
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new InvalidCredentialsException("Sessao invalida");
        }
        return user;
    }
}
