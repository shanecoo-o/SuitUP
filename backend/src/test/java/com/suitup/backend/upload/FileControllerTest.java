package com.suitup.backend.upload;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.suitup.backend.common.GlobalExceptionHandler;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.security.CustomUserDetails;
import com.suitup.backend.security.CustomUserDetailsService;
import com.suitup.backend.security.JwtAuthenticationFilter;
import com.suitup.backend.security.JwtService;
import com.suitup.backend.security.RestAccessDeniedHandler;
import com.suitup.backend.security.RestAuthenticationEntryPoint;
import com.suitup.backend.security.SecurityConfig;
import com.suitup.backend.user.RoleCode;
import com.suitup.backend.user.RoleEntity;
import com.suitup.backend.user.UserEntity;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileController.class)
@Import({
    SecurityConfig.class,
    JwtAuthenticationFilter.class,
    RestAuthenticationEntryPoint.class,
    RestAccessDeniedHandler.class,
    GlobalExceptionHandler.class
})
@TestPropertySource(properties = {
    "app.security.jwt.secret=suitup-test-secret-with-at-least-32-bytes",
    "app.security.cors.allowed-origins=http://localhost:3000"
})
class FileControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private FileStorageService fileStorageService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    void uploadRequiresAuthentication() throws Exception {
        mockMvc.perform(multipart("/api/files/upload")
                .file(new MockMultipartFile("file", "image.png", "image/png", new byte[] {1}))
                .param("purpose", "PROFILE"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanUploadAndReceivesSafeResponse() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);
        MockMultipartFile file = new MockMultipartFile(
            "file", "profile.png", "image/png", new byte[] {(byte) 0x89, 0x50}
        );
        UploadedFileEntity stored = metadata(customer.getId(), UploadedFilePurpose.PROFILE, file.getSize());
        when(fileStorageService.store(file, UploadedFilePurpose.PROFILE, customer.getId())).thenReturn(stored);

        mockMvc.perform(multipart("/api/files/upload")
                .file(file)
                .param("purpose", "PROFILE")
                .with(user(customer)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/files/" + stored.getId()))
            .andExpect(jsonPath("$.fileId").value(stored.getId().toString()))
            .andExpect(jsonPath("$.url").value("/api/files/" + stored.getId()))
            .andExpect(jsonPath("$.storagePath").doesNotExist())
            .andExpect(jsonPath("$.storedName").doesNotExist());
    }

    @Test
    void rejectsUnknownUploadPurpose() throws Exception {
        mockMvc.perform(multipart("/api/files/upload")
                .file(new MockMultipartFile("file", "image.png", "image/png", new byte[] {1}))
                .param("purpose", "UNKNOWN")
                .with(user(userDetails(RoleCode.CUSTOMER))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void retrievalReturnsStoredContentTypeAndDisposition() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);
        UploadedFileEntity stored = metadata(customer.getId(), UploadedFilePurpose.PAYMENT_PROOF, 9);
        stored.setOriginalName("proof.pdf");
        stored.setContentType("application/pdf");
        byte[] bytes = "%PDF-test".getBytes();
        stored.setSizeBytes(bytes.length);
        when(fileStorageService.load(stored.getId(), customer.getId(), false)).thenReturn(
            new StoredFileResource(stored, new ByteArrayResource(bytes))
        );

        mockMvc.perform(get("/api/files/{id}", stored.getId()).with(user(customer)))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/pdf"))
            .andExpect(content().bytes(bytes))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.startsWith("attachment")));

        verify(fileStorageService).load(stored.getId(), customer.getId(), false);
    }

    @Test
    void adminCanRetrievePrivateFile() throws Exception {
        CustomUserDetails admin = userDetails(RoleCode.ADMIN);
        UploadedFileEntity stored = metadata(UUID.randomUUID(), UploadedFilePurpose.PAYMENT_PROOF, 9);
        byte[] bytes = new byte[] {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x01
        };
        stored.setSizeBytes(bytes.length);
        when(fileStorageService.load(stored.getId(), admin.getId(), true)).thenReturn(
            new StoredFileResource(stored, new ByteArrayResource(bytes))
        );

        mockMvc.perform(get("/api/files/{id}", stored.getId()).with(user(admin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType("image/png"))
            .andExpect(content().bytes(bytes));

        verify(fileStorageService).load(stored.getId(), admin.getId(), true);
    }

    @Test
    void unavailableFileUsesControlledNotFoundContract() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);
        UUID fileId = UUID.randomUUID();
        when(fileStorageService.load(fileId, customer.getId(), false))
            .thenThrow(new ResourceNotFoundException("Ficheiro nao encontrado"));

        mockMvc.perform(get("/api/files/{id}", fileId).with(user(customer)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Ficheiro nao encontrado"))
            .andExpect(jsonPath("$.path").value("/api/files/" + fileId));
    }

    private UploadedFileEntity metadata(UUID ownerId, UploadedFilePurpose purpose, long size) {
        UserEntity owner = new UserEntity("Cliente", "cliente@example.com", null, "hash");
        owner.setId(ownerId);
        UploadedFileEntity entity = new UploadedFileEntity();
        entity.setId(UUID.randomUUID());
        entity.setOwnerUser(owner);
        entity.setPurpose(purpose);
        entity.setOriginalName("profile.png");
        entity.setStoredName(UUID.randomUUID() + ".png");
        entity.setContentType("image/png");
        entity.setSizeBytes(size);
        entity.setStoragePath("profile/file.png");
        entity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return entity;
    }

    private CustomUserDetails userDetails(RoleCode roleCode) {
        RoleEntity role = new RoleEntity(roleCode, roleCode.name());
        role.setId(UUID.randomUUID());
        UserEntity userEntity = new UserEntity(
            "Utilizador",
            roleCode.name().toLowerCase() + "@example.com",
            null,
            "hash"
        );
        userEntity.setId(UUID.randomUUID());
        userEntity.addRole(role);
        return CustomUserDetails.from(userEntity);
    }
}
