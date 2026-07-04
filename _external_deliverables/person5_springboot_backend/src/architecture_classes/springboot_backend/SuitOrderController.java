package mz.ac.unizambeze.suitup.backend.controller;

import mz.ac.unizambeze.suitup.backend.entity.SuitOrderEntity;
import mz.ac.unizambeze.suitup.backend.service.SuitOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLADOR REST (Spring Boot API Gateway Endpoints)
 * Permite a comunicação direta do cliente KMP através de requisições HTTP JSON.
 * Configura CORS por padrão para as conexões locais ou na nuvem Cloud Run.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*") // Habilita acesso cross-origin para o aplicativo móvel ou simulador web em Vite
public class SuitOrderController {

    private final SuitOrderService orderService;

    @Autowired
    public SuitOrderController(SuitOrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Obter lista de todas as encomendas do ateliê
     */
    @GetMapping
    public ResponseEntity<List<SuitOrderEntity>> listAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Obter detalhes de tracking de um fato específico pelo ID (Ex: SUIT-785)
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuitOrderEntity> getById(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gravação Online direta de nova encomenda personalizada pelo utilizador
     */
    @PostMapping
    public ResponseEntity<?> createNew(@RequestBody SuitOrderEntity order) {
        try {
            SuitOrderEntity saved = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint de Sincronização em Lote (Store and Forward)
     * Recebe um lote de pedidos acumulados offline pelo cliente KMP.
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

    /**
     * Atualização Administrativa do Tracking
     * Chamado pelo back-office do alfaiate para mudar o estado para "Medidas & Corte", "Confeção Manual", etc.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestParam String newStatus) {
        try {
            SuitOrderEntity updated = orderService.updateOrderStatus(id, newStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
