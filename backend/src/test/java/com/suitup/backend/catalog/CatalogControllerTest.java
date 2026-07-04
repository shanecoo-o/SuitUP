package com.suitup.backend.catalog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suitup.backend.catalog.dto.CreateSuitModelRequest;
import com.suitup.backend.catalog.dto.SuitModelResponse;
import com.suitup.backend.common.GlobalExceptionHandler;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.security.CustomUserDetailsService;
import com.suitup.backend.security.JwtAuthenticationFilter;
import com.suitup.backend.security.JwtService;
import com.suitup.backend.security.RestAccessDeniedHandler;
import com.suitup.backend.security.RestAuthenticationEntryPoint;
import com.suitup.backend.security.SecurityConfig;
import com.suitup.backend.security.CustomUserDetails;
import com.suitup.backend.upload.UploadedFilePurpose;
import com.suitup.backend.upload.dto.StoredFileResponse;
import com.suitup.backend.user.RoleCode;
import com.suitup.backend.user.RoleEntity;
import com.suitup.backend.user.UserEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@WebMvcTest({CatalogController.class, AdminCatalogController.class})
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
class CatalogControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CatalogService catalogService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    void publicListReturnsOnlyServiceActiveModels() throws Exception {
        SuitModelResponse active = response(UUID.randomUUID(), true);
        when(catalogService.listActive()).thenReturn(List.of(active));

        mockMvc.perform(get("/api/suit-models"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(active.id().toString()))
            .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void publicLookupReturnsNotFoundForInactiveModel() throws Exception {
        UUID id = UUID.randomUUID();
        when(catalogService.getActiveById(id))
            .thenThrow(new ResourceNotFoundException("Modelo de fato nÃ£o encontrado: " + id));

        mockMvc.perform(get("/api/suit-models/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void invalidCatalogIdentifierReturnsStructuredBadRequest() throws Exception {
        mockMvc.perform(get("/api/suit-models/not-a-uuid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void adminCatalogRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/suit-models"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotAccessAdminCatalog() throws Exception {
        mockMvc.perform(get("/api/admin/suit-models"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateSuitModel() throws Exception {
        CreateSuitModelRequest request = validCreateRequest();
        SuitModelResponse created = response(UUID.randomUUID(), true);
        when(catalogService.create(any(CreateSuitModelRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/admin/suit-models")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/admin/suit-models/" + created.id()))
            .andExpect(jsonPath("$.id").value(created.id().toString()));

        verify(catalogService).create(any(CreateSuitModelRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createValidationRejectsBlankNameAndNegativePrice() throws Exception {
        CreateSuitModelRequest invalid = new CreateSuitModelRequest(
            "",
            "ClÃ¡ssico",
            "Corte formal",
            new BigDecimal("-1.00"),
            "MZN",
            "LÃ£ Premium",
            "Preto",
            null,
            null,
            true
        );

        mockMvc.perform(post("/api/admin/suit-models")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.fieldErrors.name").exists())
            .andExpect(jsonPath("$.fieldErrors.price").exists());

        verifyNoInteractions(catalogService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanActivateAndDeactivateSuitModel() throws Exception {
        UUID id = UUID.randomUUID();
        when(catalogService.activate(id)).thenReturn(response(id, true));
        when(catalogService.deactivate(id)).thenReturn(response(id, false));

        mockMvc.perform(patch("/api/admin/suit-models/{id}/activate", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(patch("/api/admin/suit-models/{id}/deactivate", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));

        verify(catalogService).activate(id);
        verify(catalogService).deactivate(id);
    }

    @Test
    void adminCanUploadSuitImage() throws Exception {
        CustomUserDetails admin = userDetails(RoleCode.ADMIN);
        UUID modelId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
            "file", "fato.png", "image/png", new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47}
        );
        when(catalogService.uploadImage(modelId, file, admin.getId())).thenReturn(
            new StoredFileResponse(
                fileId,
                "fato.png",
                "image/png",
                file.getSize(),
                UploadedFilePurpose.SUIT_IMAGE,
                OffsetDateTime.now(ZoneOffset.UTC),
                "/api/files/" + fileId
            )
        );

        mockMvc.perform(multipart("/api/admin/suit-models/{id}/image", modelId)
                .file(file)
                .with(user(admin)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.fileId").value(fileId.toString()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotUploadSuitImage() throws Exception {
        mockMvc.perform(multipart("/api/admin/suit-models/{id}/image", UUID.randomUUID())
                .file(new MockMultipartFile("file", "fato.png", "image/png", new byte[] {1})))
            .andExpect(status().isForbidden());

        verifyNoInteractions(catalogService);
    }

    private CreateSuitModelRequest validCreateRequest() {
        return new CreateSuitModelRequest(
            "Fato ClÃ¡ssico Preto",
            "ClÃ¡ssico",
            "Corte formal",
            new BigDecimal("8500.00"),
            null,
            "LÃ£ Premium",
            "Preto",
            "suit_classic_black",
            null,
            null
        );
    }

    private SuitModelResponse response(UUID id, boolean active) {
        return new SuitModelResponse(
            id,
            "Fato ClÃ¡ssico Preto",
            "ClÃ¡ssico",
            "Corte formal",
            new BigDecimal("8500.00"),
            "MZN",
            "LÃ£ Premium",
            "Preto",
            "suit_classic_black",
            null,
            active,
            null,
            null
        );
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
