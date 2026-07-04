package com.suitup.backend.upload;

import com.suitup.backend.auth.InvalidCredentialsException;
import com.suitup.backend.security.CustomUserDetails;
import com.suitup.backend.upload.dto.StoredFileResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoredFileResponse> upload(
        @RequestParam("file") MultipartFile file,
        @RequestParam("purpose") UploadedFilePurpose purpose,
        Authentication authentication
    ) {
        CustomUserDetails user = currentUser(authentication);
        StoredFileResponse response = StoredFileResponse.from(
            fileStorageService.store(file, purpose, user.getId())
        );
        return ResponseEntity.created(URI.create(response.url())).body(response);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> download(
        @PathVariable UUID fileId,
        Authentication authentication
    ) {
        CustomUserDetails user = currentUser(authentication);
        StoredFileResource storedFile = fileStorageService.load(fileId, user.getId(), isAdmin(user));
        UploadedFileEntity metadata = storedFile.metadata();
        ContentDisposition disposition = metadata.getContentType().startsWith("image/")
            ? ContentDisposition.inline().filename(metadata.getOriginalName(), StandardCharsets.UTF_8).build()
            : ContentDisposition.attachment().filename(metadata.getOriginalName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(metadata.getContentType()))
            .contentLength(metadata.getSizeBytes())
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .body(storedFile.resource());
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new InvalidCredentialsException("Sessao invalida");
        }
        return user;
    }

    private boolean isAdmin(CustomUserDetails user) {
        return user.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
