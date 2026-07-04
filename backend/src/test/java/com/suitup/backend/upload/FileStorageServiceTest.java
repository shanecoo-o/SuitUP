package com.suitup.backend.upload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.suitup.backend.common.BadRequestException;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.user.UserEntity;
import com.suitup.backend.user.UserRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class FileStorageServiceTest {

    private static final byte[] PNG = new byte[] {
        (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x01
    };

    @TempDir
    Path tempDirectory;

    private UploadedFileRepository uploadedFileRepository;
    private UserRepository userRepository;
    private FileStorageService service;
    private UserEntity owner;

    @BeforeEach
    void setUp() {
        StorageProperties properties = new StorageProperties();
        properties.setRoot(tempDirectory.resolve("uploads"));
        uploadedFileRepository = mock(UploadedFileRepository.class);
        userRepository = mock(UserRepository.class);
        service = new FileStorageService(properties, uploadedFileRepository, userRepository);
        owner = new UserEntity("Cliente", "cliente@example.com", null, "hash");
        owner.setId(UUID.randomUUID());
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(uploadedFileRepository.saveAndFlush(any(UploadedFileEntity.class))).thenAnswer(invocation -> {
            UploadedFileEntity entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });
    }

    @Test
    void storesAllowedFileAndPersistsSafeMetadata() throws Exception {
        UploadedFileEntity stored = storePng("catalog.png", UploadedFilePurpose.SUIT_IMAGE);

        Path physicalFile = tempDirectory.resolve("uploads").resolve(stored.getStoragePath());
        assertThat(physicalFile).isRegularFile();
        assertThat(Files.readAllBytes(physicalFile)).isEqualTo(PNG);
        assertThat(stored.getStoredName()).matches("[0-9a-f-]{36}\\.png");
        assertThat(stored.getStoragePath()).doesNotContain("catalog.png");
        assertThat(stored.getOwnerUser()).isSameAs(owner);
        verify(uploadedFileRepository).saveAndFlush(stored);
    }

    @Test
    void rejectsEmptyUnsupportedOversizeAndTraversalFiles() {
        assertThatThrownBy(() -> service.store(
            new MockMultipartFile("file", "empty.png", "image/png", new byte[0]),
            UploadedFilePurpose.SUIT_IMAGE,
            owner.getId()
        )).isInstanceOf(BadRequestException.class).hasMessageContaining("vazio");

        assertThatThrownBy(() -> service.store(
            new MockMultipartFile("file", "script.txt", "text/plain", "hello".getBytes()),
            UploadedFilePurpose.OTHER,
            owner.getId()
        )).isInstanceOf(BadRequestException.class).hasMessageContaining("nao suportado");

        assertThatThrownBy(() -> service.store(
            new MockMultipartFile(
                "file", "large.pdf", "application/pdf", new byte[(int) FileStorageService.MAX_FILE_SIZE + 1]
            ),
            UploadedFilePurpose.PAYMENT_PROOF,
            owner.getId()
        )).isInstanceOf(BadRequestException.class).hasMessageContaining("10 MB");

        assertThatThrownBy(() -> service.store(
            new MockMultipartFile("file", "../escape.png", "image/png", PNG),
            UploadedFilePurpose.SUIT_IMAGE,
            owner.getId()
        )).isInstanceOf(BadRequestException.class).hasMessageContaining("caminho invalido");
    }

    @Test
    void rejectsSpoofedMimeAndPdfForSuitImage() {
        assertThatThrownBy(() -> service.store(
            new MockMultipartFile("file", "fake.png", "image/png", "not-png".getBytes()),
            UploadedFilePurpose.SUIT_IMAGE,
            owner.getId()
        )).isInstanceOf(BadRequestException.class).hasMessageContaining("nao corresponde");

        assertThatThrownBy(() -> service.store(
            new MockMultipartFile("file", "catalog.pdf", "application/pdf", "%PDF-test".getBytes()),
            UploadedFilePurpose.SUIT_IMAGE,
            owner.getId()
        )).isInstanceOf(BadRequestException.class).hasMessageContaining("apenas imagens");
    }

    @Test
    void ownerAndAdminCanLoadButForeignUserGetsNotFound() {
        UploadedFileEntity stored = storePng("profile.png", UploadedFilePurpose.PROFILE);
        when(uploadedFileRepository.findById(stored.getId())).thenReturn(Optional.of(stored));

        StoredFileResource ownFile = service.load(stored.getId(), owner.getId(), false);
        StoredFileResource adminFile = service.load(stored.getId(), UUID.randomUUID(), true);

        assertThat(ownFile.resource().exists()).isTrue();
        assertThat(adminFile.metadata().getContentType()).isEqualTo("image/png");
        assertThatThrownBy(() -> service.load(stored.getId(), UUID.randomUUID(), false))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    private UploadedFileEntity storePng(String filename, UploadedFilePurpose purpose) {
        return service.store(
            new MockMultipartFile("file", filename, "image/png", PNG),
            purpose,
            owner.getId()
        );
    }
}
