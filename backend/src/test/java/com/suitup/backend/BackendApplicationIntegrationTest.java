package com.suitup.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import com.suitup.backend.upload.FileStorageService;
import com.suitup.backend.upload.UploadedFileEntity;
import com.suitup.backend.upload.UploadedFilePurpose;
import com.suitup.backend.upload.UploadedFileRepository;
import com.suitup.backend.user.RoleCode;
import com.suitup.backend.user.RoleRepository;
import com.suitup.backend.user.UserEntity;
import com.suitup.backend.user.UserRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class BackendApplicationIntegrationTest {

    private static final int EXPECTED_TABLE_COUNT = 12;

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("suitup_test")
            .withUsername("suitup_test")
            .withPassword("suitup_test");

    @TempDir
    static Path storageRoot;

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("app.storage.root", () -> storageRoot.toString());
    }

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void contextLoadsWithPostgres() {
        assertThat(flyway.info().current()).isNotNull();
    }

    @Test
    void appliesAllMigrationsAndCreatesCoreTables() {
        assertThat(flyway.info().current().getVersion().toString()).isEqualTo("5");

        Integer tableCount = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM information_schema.tables
            WHERE table_schema = 'public'
              AND table_name IN (
                  'roles',
                  'users',
                  'user_roles',
                  'uploaded_files',
                  'suit_models',
                  'orders',
                  'order_items',
                  'measurements',
                  'payments',
                  'order_status_history',
                  'payment_status_history',
                  'idempotency_keys'
              )
            """,
            Integer.class
        );

        assertThat(tableCount).isEqualTo(EXPECTED_TABLE_COUNT);
    }

    @Test
    void persistsPhysicalFileMetadataAndBytes() {
        UserEntity owner = new UserEntity(
            "Upload Integration",
            "upload-" + UUID.randomUUID() + "@example.com",
            null,
            "test-hash"
        );
        owner.addRole(roleRepository.findByCode(RoleCode.CUSTOMER).orElseThrow());
        owner = userRepository.saveAndFlush(owner);
        byte[] png = new byte[] {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x01
        };

        UploadedFileEntity stored = fileStorageService.store(
            new MockMultipartFile("file", "profile.png", "image/png", png),
            UploadedFilePurpose.PROFILE,
            owner.getId()
        );

        UploadedFileEntity persisted = uploadedFileRepository.findById(stored.getId()).orElseThrow();
        assertThat(persisted.getOriginalName()).isEqualTo("profile.png");
        assertThat(persisted.getStoragePath()).doesNotContain("profile.png");
        assertThat(storageRoot.resolve(persisted.getStoragePath())).isRegularFile();
        assertThat(Files.exists(storageRoot.resolve(persisted.getStoragePath()))).isTrue();
    }
}
