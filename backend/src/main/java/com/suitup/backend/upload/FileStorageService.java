package com.suitup.backend.upload;

import com.suitup.backend.common.BadRequestException;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.user.UserEntity;
import com.suitup.backend.user.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);
    public static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "image/png",
        "image/jpeg",
        "application/pdf"
    );
    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of("image/png", "image/jpeg");
    private static final Map<String, String> EXTENSIONS = Map.of(
        "image/png", ".png",
        "image/jpeg", ".jpg",
        "application/pdf", ".pdf"
    );

    private final Path storageRoot;
    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;

    public FileStorageService(
        StorageProperties properties,
        UploadedFileRepository uploadedFileRepository,
        UserRepository userRepository
    ) {
        if (properties.getRoot() == null) {
            throw new IllegalStateException("FILE_STORAGE_ROOT nao pode ser vazio");
        }
        this.storageRoot = properties.getRoot().toAbsolutePath().normalize();
        this.uploadedFileRepository = uploadedFileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UploadedFileEntity store(MultipartFile file, UploadedFilePurpose purpose, UUID ownerUserId) {
        validate(file, purpose);
        UserEntity owner = userRepository.findById(ownerUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Proprietario do ficheiro nao encontrado"));
        String contentType = file.getContentType();
        String storedName = UUID.randomUUID() + EXTENSIONS.get(contentType);
        Path purposeDirectory = resolveUnderRoot(purpose.name().toLowerCase(Locale.ROOT));
        Path target = purposeDirectory.resolve(storedName).normalize();
        requireUnderRoot(target);

        try {
            Files.createDirectories(purposeDirectory);
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, target);
            }

            UploadedFileEntity entity = new UploadedFileEntity();
            entity.setOwnerUser(owner);
            entity.setPurpose(purpose);
            entity.setOriginalName(file.getOriginalFilename().trim());
            entity.setStoredName(storedName);
            entity.setContentType(contentType);
            entity.setSizeBytes(file.getSize());
            entity.setStoragePath(storageRoot.relativize(target).toString());
            entity.setPublicUrl(null);
            entity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            try {
                UploadedFileEntity saved = uploadedFileRepository.saveAndFlush(entity);
                deleteOnTransactionRollback(target);
                return saved;
            } catch (RuntimeException exception) {
                Files.deleteIfExists(target);
                throw exception;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel armazenar o ficheiro", exception);
        }
    }

    @Transactional(readOnly = true)
    public StoredFileResource load(UUID fileId, UUID currentUserId, boolean admin) {
        UploadedFileEntity metadata = uploadedFileRepository.findById(fileId)
            .orElseThrow(() -> new ResourceNotFoundException("Ficheiro nao encontrado"));
        UUID ownerId = metadata.getOwnerUser() == null ? null : metadata.getOwnerUser().getId();
        if (!admin && (ownerId == null || !ownerId.equals(currentUserId))) {
            throw new ResourceNotFoundException("Ficheiro nao encontrado");
        }

        try {
            Path relativePath = Path.of(metadata.getStoragePath());
            if (relativePath.isAbsolute()) {
                throw new ResourceNotFoundException("Ficheiro nao encontrado");
            }
            Path filePath = storageRoot.resolve(relativePath).normalize();
            requireUnderRoot(filePath);
            if (!Files.isRegularFile(filePath) || !Files.isReadable(filePath)) {
                throw new ResourceNotFoundException("Ficheiro nao encontrado");
            }
            Resource resource = new UrlResource(filePath.toUri());
            return new StoredFileResource(metadata, resource);
        } catch (InvalidPathException | IOException exception) {
            throw new ResourceNotFoundException("Ficheiro nao encontrado");
        }
    }

    private void validate(MultipartFile file, UploadedFilePurpose purpose) {
        if (file == null || file.isEmpty() || file.getSize() <= 0) {
            throw new BadRequestException("O ficheiro nao pode estar vazio");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("O ficheiro excede o limite de 10 MB");
        }
        if (purpose == null) {
            throw new BadRequestException("A finalidade do ficheiro e obrigatoria");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank() || originalName.length() > 255) {
            throw new BadRequestException("O nome original do ficheiro e invalido");
        }
        if (originalName.contains("/") || originalName.contains("\\")
            || originalName.equals(".") || originalName.equals("..")
            || originalName.indexOf('\0') >= 0) {
            throw new BadRequestException("O nome original do ficheiro contem um caminho invalido");
        }
        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Tipo de ficheiro nao suportado");
        }
        if ((purpose == UploadedFilePurpose.SUIT_IMAGE || purpose == UploadedFilePurpose.PROFILE)
            && !IMAGE_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Esta finalidade aceita apenas imagens PNG ou JPEG");
        }
        validateSignature(file, contentType);
    }

    private void deleteOnTransactionRollback(Path target) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    try {
                        Files.deleteIfExists(target);
                    } catch (IOException exception) {
                        LOGGER.error("Failed to remove rolled-back stored file {}", target, exception);
                    }
                }
            }
        });
    }

    private void validateSignature(MultipartFile file, String contentType) {
        try (InputStream input = file.getInputStream()) {
            byte[] signature = input.readNBytes(8);
            boolean valid = switch (contentType) {
                case "image/png" -> startsWith(signature, new byte[] {
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
                });
                case "image/jpeg" -> startsWith(signature, new byte[] {
                    (byte) 0xFF, (byte) 0xD8, (byte) 0xFF
                });
                case "application/pdf" -> startsWith(signature, new byte[] {
                    0x25, 0x50, 0x44, 0x46, 0x2D
                });
                default -> false;
            };
            if (!valid) {
                throw new BadRequestException("O conteudo do ficheiro nao corresponde ao tipo informado");
            }
        } catch (IOException exception) {
            throw new BadRequestException("Nao foi possivel validar o ficheiro");
        }
    }

    private boolean startsWith(byte[] source, byte[] prefix) {
        return source.length >= prefix.length
            && Arrays.equals(Arrays.copyOf(source, prefix.length), prefix);
    }

    private Path resolveUnderRoot(String relativePath) {
        Path resolved = storageRoot.resolve(relativePath).normalize();
        requireUnderRoot(resolved);
        return resolved;
    }

    private void requireUnderRoot(Path path) {
        if (!path.startsWith(storageRoot)) {
            throw new BadRequestException("Caminho de armazenamento invalido");
        }
    }
}
