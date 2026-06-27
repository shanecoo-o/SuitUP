package mz.ac.unizambeze.suitup.backend.controller;

import mz.ac.unizambeze.suitup.backend.entity.SuitOrderEntity;
import mz.ac.unizambeze.suitup.backend.service.SuitOrderService;
import mz.ac.unizambeze.suitup.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLADOR REST GERAL DE ENCOMENDAS (Suit Orders)
 * Expõeendpoints seguros e avançados para criação, consulta, upload de comprovativo
 * e administração (auditoria de transações M-Pesa e tracking físico do alfaiate).
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class SuitOrderController {

    private final SuitOrderService orderService;
    private final FileStorageService fileStorageService;

    @Autowired
    public SuitOrderController(SuitOrderService orderService, FileStorageService fileStorageService) {
        this.orderService = orderService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * GET /api/orders
     * Lista todos os pedidos efetuados (Apenas administradores ou operadores de sistema).
     */
    @GetMapping
    public ResponseEntity<List<SuitOrderEntity>> listAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * GET /api/orders/{id}
     * Procura os dados de uma encomenda de forma exata pelo seu ID único de rastreio (Ex: SUIT-785)
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuitOrderEntity> getById(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/orders
     * Recebe e valida os dados de design e medidas corporais digitados pelo cliente.
     */
    @PostMapping
    public ResponseEntity<?> createNew(@RequestBody SuitOrderEntity order) {
        try {
            SuitOrderEntity saved = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * POST /api/orders/payment-proof
     * Faz o Multipart upload do comprovativo (captura de tela com mensagem do M-Pesa) e associa a uma Encomenda.
     */
    @PostMapping(value = "/payment-proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPaymentProof(
            @RequestParam("orderId") String orderId,
            @RequestParam("file") MultipartFile file) {
        
        try {
            // 1. Faz o upload físico seguro utilizando o FileStorageService
            String relativeUrlPath = fileStorageService.storeFile(file);

            // 2. Associa o caminho e URL do comprovativo físico ao registo correspondente da encomenda
            SuitOrderEntity updatedOrder = orderService.savePaymentProofUrl(orderId, relativeUrlPath);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Comprovativo M-Pesa gravado com êxito! Equipa irá validar a transação.");
            response.put("proofUrl", relativeUrlPath);
            response.put("order", updatedOrder);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível processar o upload do arquivo de transação: " + e.getMessage());
        }
    }

    /**
     * PUT /api/orders/{id}/validate-payment (ADMIN ONLY)
     * Endpoint administrativo chamado após o alfaiate cruzar o código da transação com o sistema do banco/M-Pesa.
     */
    @PutMapping("/{id}/validate-payment")
    public ResponseEntity<?> validateMpesaPayment(
            @PathVariable String id, 
            @RequestParam("isValid") boolean isValid,
            @RequestParam(value = "notes", required = false) String notes) {
        try {
            SuitOrderEntity updated = orderService.validatePaymentAndProgress(id, isValid, notes);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * PUT /api/orders/{id}/status (ADMIN ONLY)
     * Atualização manual do fluxo industrial da fábrica (Corte, Costura, Confecção, Entrega).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestParam("newStatus") String newStatus) {
        try {
            SuitOrderEntity updated = orderService.updateOrderStatus(id, newStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * POST /api/orders/sync-batch
     * Processamento em lote de sincronização para resiliência de cache offline do KMP (Store and Forward).
     */
    @PostMapping("/sync-batch")
    public ResponseEntity<String> syncBatch(@RequestBody List<SuitOrderEntity> offlineBatch) {
        try {
            orderService.syncOfflineOrders(offlineBatch);
            return ResponseEntity.ok("Dispositivo sincronizado com sucesso! Encomendas atualizadas no servidor.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar lote de sincronização: " + e.getMessage());
        }
    }
}
