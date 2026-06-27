package mz.ac.unizambeze.suitup.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * SERVIÇO DE ARMAZENAMENTO DE ARQUIVOS (LOCAL/CLOUD SECURE)
 * Responsável por gerir os uploads de ficheiros em multipart no servidor Spring Boot,
 * ideal para salvar de forma fidedigna os prints de ecrã/telas com comprovativos do M-Pesa.
 */
@Service
public class FileStorageService {

    // Pasta local no servidor onde as faturas e comprovativos serão guardados fisicamente
    private final Path fileStorageLocation;

    public FileStorageService() {
        // Define o caminho base de persistência local (na raiz do contentor ou diretório local)
        this.fileStorageLocation = Paths.get("./uploads/payment_proofs")
                .toAbsolutePath().normalize();

        try {
            // Garante a existência do diretório físico para não causar IOExceptions em novas execuções
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível inicializar a pasta física para faturas M-Pesa localmente.", ex);
        }
    }

    /**
     * Guarda um ficheiro enviado por Multipart no disco rígido do servidor,
     * adicionando um prefixo aleatório UUID para evitar colisões de ficheiros com o mesmo nome.
     * @return O URL ou caminho estático público de acesso ao ficheiro salvo.
     */
    public String storeFile(MultipartFile file) {
        // Limpa o nome original do ficheiro para evitar riscos de Paths Traversal (Ex: "../../etc/file.txt")
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Verifica extensão para evitar upload de scripts executáveis perigosos (.sh, .exe, .jsp)
            if (originalFileName.contains("..")) {
                throw new IllegalArgumentException("Nome de ficheiro possui caminho inválido: " + originalFileName);
            }

            // Atribui nome único seguro com UUID para fins de auditoria inequívoca de transação
            String fileExtension = "";
            int i = originalFileName.lastIndexOf('.');
            if (i > 0) {
                fileExtension = originalFileName.substring(i);
            }
            
            String uniqueFileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + fileExtension;

            // Resolve e grava o ficheiro em disco físico
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Retorna o endereço acessível do ficheiro para ser guardado no banco de dados
            return "/uploads/payment_proofs/" + uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível salvar o arquivo multipart " + originalFileName + ". Tente de novo!", ex);
        }
    }
}
