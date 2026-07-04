package com.suitup.backend.order;

import com.suitup.backend.order.dto.*;
import com.suitup.backend.storage.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST CONTROLLER DE ENCOMENDAS (Orders Bespoke API)
 * Centraliza ações do cliente e fluxos b2b/b2c relacionados ao tracking de fatos de alfaiataria.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final FileStorageService fileStorageService;

    @Autowired
    public OrderController(OrderService orderService, FileStorageService fileStorageService) {
        this.orderService = orderService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * POST /api/orders
     * Cria um pedido novo a partir do terno montado ou modelado no KMP móvel.
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderResponse response = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * GET /api/orders
     * Lista todos os fatos sob medida e canais de sincronização inseridos no sistema.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * GET /api/orders/{id}
     * Detalhes de um pedido individual (conferência mestre externa).
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable("id") String id) {
        try {
            OrderResponse response = orderService.getOrderById(id);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }
    }

    /**
     * POST /api/orders/sync-batch
     * Envia em lote múltiplos pedidos recolhidos localmente em modo offline.
     */
    @PostMapping("/sync-batch")
    public ResponseEntity<?> syncOfflineBatch(@Valid @RequestBody SyncBatchRequest request) {
        try {
            SyncBatchResponse response = orderService.syncBatch(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Falha de processamento no lote: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    /**
     * PUT /api/orders/{id}/status
     * Alfaiates/Ateliê: Alteram o andamento físico (Ex: IN_PRODUCTION, READY_FOR_PICKUP).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") String id, 
                                          @Valid @RequestBody UpdateOrderStatusRequest request) {
        try {
            OrderResponse response = orderService.updateOrderStatus(id, request);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * PUT /api/orders/{id}/validate-payment
     * Contabilidade: Confirma ou rejeita o comprovante de depósito M-Pesa.
     */
    @PutMapping("/{id}/validate-payment")
    public ResponseEntity<?> validatePayment(@PathVariable("id") String id, 
                                             @Valid @RequestBody ValidatePaymentRequest request) {
        try {
            OrderResponse response = orderService.validatePayment(id, request);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * POST /api/orders/{id}/payment-proof
     * Faz upload via multipart do comprovativo físico de M-Pesa.
     */
    @PostMapping(value = "/{id}/payment-proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPaymentProof(@PathVariable("id") String id, 
                                                @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Arquivo enviado está vazio ou nulo.");
                return ResponseEntity.badRequest().body(err);
            }

            // Grava documento
            String fileUrl = fileStorageService.storeFile(file);
            orderService.updateProofUrl(id, fileUrl);

            Map<String, Object> body = new HashMap<>();
            body.put("message", "Comprovativo M-Pesa carregado com sucesso!");
            body.put("paymentProofUrl", fileUrl);
            body.put("orderId", id);
            
            return ResponseEntity.ok(body);
        } catch (NoSuchElementException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Erro ao gravar comprovativo multipart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    /**
     * GET /api/orders/{id}/payment-proof
     * Devolve os metadados do comprovativo anexado àquela transação.
     */
    @GetMapping("/{id}/payment-proof")
    public ResponseEntity<?> getPaymentProofUrl(@PathVariable("id") String id) {
        try {
            OrderResponse order = orderService.getOrderById(id);
            Map<String, String> body = new HashMap<>();
            body.put("id", id);
            body.put("paymentProofUrl", order.getPaymentProofUrl());
            body.put("paymentStatus", order.getPaymentStatus());
            return ResponseEntity.ok(body);
        } catch (NoSuchElementException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }
    }
}
