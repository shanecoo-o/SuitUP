package com.suitup.backend.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * SERVIÇO DE ARMAZENAMENTO DE COMPROVATIVOS DE PAGAMENTO
 * Salva imagens das transações bancárias convertidas em multipart localmente.
 */
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${suitup.upload-dir:uploads/payment-proofs}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            System.out.println("Diretório de comprovativos M-Pesa persistido em: " + this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório onde os ficheiros serão guardados.", ex);
        }
    }

    /**
     * Guarda o arquivo temporário de forma física e blindada contra fraudes de extensão de arquivos.
     */
    public String storeFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.contains("..")) {
            throw new IllegalArgumentException("Nome de arquivo contém sequências de caracteres inválidas.");
        }

        // Tipo Mime ou Extensão
        String suffix = ".jpg";
        if (originalFileName.toLowerCase().endsWith(".png")) {
            suffix = ".png";
        } else if (originalFileName.toLowerCase().endsWith(".pdf")) {
            suffix = ".pdf";
        }

        String cleanedFileName = UUID.randomUUID().toString() + suffix;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(cleanedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/payment-proofs/" + cleanedFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Falha ao registar o arquivador de comprovativos. Tente novamente.", ex);
        }
    }
}
