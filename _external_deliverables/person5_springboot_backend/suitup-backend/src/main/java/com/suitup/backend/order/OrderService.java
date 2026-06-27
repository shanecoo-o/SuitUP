package com.suitup.backend.order;

import com.suitup.backend.order.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SERVIÇO INDUSTRIAL DE PEDIDOS (Order Bespoke Core Services)
 * Governa as transações de fabrico de fatos no PostgreSQL.
 * Implementa as regras rígidas contra duplicidades de M-Pesa.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Regra de Negócio: Criar Encomenda Única
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Validação anti-reentrada: verificar se transação M-Pesa já foi usada
        Optional<OrderEntity> existingMpesa = orderRepository.findByMpesaTransactionId(request.getMpesaTransactionId().trim());
        if (existingMpesa.isPresent()) {
            throw new IllegalArgumentException(
                "O ID de transação M-Pesa '" + request.getMpesaTransactionId() + "' já foi utilizado em outra encomenda."
            );
        }

        // Caso o aplicativo offline possua ID local, assegura que não há reaproveitamento errôneo
        if (request.getClientGeneratedId() != null && !request.getClientGeneratedId().trim().isEmpty()) {
            Optional<OrderEntity> existingLocalId = orderRepository.findByClientGeneratedId(request.getClientGeneratedId().trim());
            if (existingLocalId.isPresent()) {
                throw new IllegalArgumentException(
                    "O ID local temporário '" + request.getClientGeneratedId() + "' já foi sincronizado."
                );
            }
        }

        // Mapeamento de Entity
        OrderEntity entity = mapRequestToEntity(request);
        entity.setId("SUIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        OrderEntity saved = orderRepository.save(entity);
        return mapEntityToResponse(saved);
    }

    /**
     * Regra de Negócio: Sincronização em Lote (Store and Forward)
     * Processa múltiplos pedidos cadastrados offline. Pula itens duplicados em vez de derrubar a transação em lote.
     */
    @Transactional
    public SyncBatchResponse syncBatch(SyncBatchRequest request) {
        int totalProcessed = 0;
        int totalSynced = 0;
        int totalIgnored = 0;

        List<String> syncedIds = new ArrayList<>();
        List<String> ignoredIds = new ArrayList<>();

        for (CreateOrderRequest ord : request.getOrders()) {
            totalProcessed++;
            String mpesaId = ord.getMpesaTransactionId().trim();
            String clientGenId = ord.getClientGeneratedId() != null ? ord.getClientGeneratedId().trim() : "";

            // Verifica duplicação idêntica
            boolean isMpesaDuplicated = orderRepository.findByMpesaTransactionId(mpesaId).isPresent();
            boolean isClientGenDuplicated = !clientGenId.isEmpty() && orderRepository.findByClientGeneratedId(clientGenId).isPresent();

            if (isMpesaDuplicated || isClientGenDuplicated) {
                totalIgnored++;
                ignoredIds.add(clientGenId.isEmpty() ? mpesaId : clientGenId);
                continue;
            }

            // Gravação do elemento legítimo offline
            OrderEntity entity = mapRequestToEntity(ord);
            // Preserva e vincula o ID local como representação única
            if (!clientGenId.isEmpty()) {
                entity.setId("SUIT-SYNC-" + clientGenId);
            } else {
                entity.setId("SUIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }

            OrderEntity saved = orderRepository.save(entity);
            totalSynced++;
            syncedIds.add(saved.getId());
        }

        return new SyncBatchResponse(totalProcessed, totalSynced, totalIgnored, syncedIds, ignoredIds);
    }

    /**
     * Resgata a listagem de todas as encomendas do banco
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Detalhes individuais completos por ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Encomenda " + id + " não localizada no sistema."));
        return mapEntityToResponse(entity);
    }

    /**
     * Atualização de status operacional (Fase de fabricação física)
     */
    @Transactional
    public OrderResponse updateOrderStatus(String id, UpdateOrderStatusRequest request) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Encomenda " + id + " não localizada no sistema."));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
            entity.setOrderStatus(newStatus);
            
            // Adiciona observações nas notas textuais
            if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
                String existingNotes = entity.getNotes() != null ? entity.getNotes() : "";
                entity.setNotes(existingNotes + "\n[" + LocalDateTime.now() + " - " + newStatus + "]: " + request.getNotes());
            }

            OrderEntity updated = orderRepository.save(entity);
            return mapEntityToResponse(updated);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado físico '" + request.getStatus() + "' inválido no sistema de alfaiataria.");
        }
    }

    /**
     * Validação Financeira Manual de Comprovativo por Admins
     */
    @Transactional
    public OrderResponse validatePayment(String id, ValidatePaymentRequest request) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Encomenda " + id + " não localizada no sistema."));

        if (request.getApproved()) {
            entity.setPaymentStatus(PaymentStatus.VALIDATED);
            entity.setOrderStatus(OrderStatus.PAYMENT_VALIDATED); // Avança fase industrial automaticamente
            entity.setNotes((entity.getNotes() != null ? entity.getNotes() : "") + "\n[Finanças]: Pagamento M-Pesa (" + entity.getMpesaTransactionId() + ") Validado com Sucesso.");
        } else {
            entity.setPaymentStatus(PaymentStatus.REJECTED);
            entity.setOrderStatus(OrderStatus.PAYMENT_PENDING); // Retrocede estado operacional para ação corretiva
            entity.setNotes((entity.getNotes() != null ? entity.getNotes() : "") + "\n[Finanças - ERRO]: Recusado. Comentários: " + request.getComments());
        }

        OrderEntity updated = orderRepository.save(entity);
        return mapEntityToResponse(updated);
    }

    /**
     * Atualiza URL de comprovativo multipart fisico
     */
    @Transactional
    public void updateProofUrl(String id, String relativePath) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Encomenda " + id + " não localizada para anexo de ficheiro."));
        
        entity.setPaymentProofUrl(relativePath);
        entity.setPaymentStatus(PaymentStatus.PROOF_UPLOADED);
        orderRepository.save(entity);
    }

    // --- CONVERSORES INTERNOS DE MAPEAMENTO ---
    private OrderEntity mapRequestToEntity(CreateOrderRequest req) {
        OrderEntity ent = new OrderEntity();
        ent.setClientName(req.getClientName());
        ent.setClientPhone(req.getClientPhone());
        ent.setClientEmail(req.getClientEmail());
        ent.setModel(req.getModel());
        ent.setLapel(req.getLapel());
        ent.setSleeves(req.getSleeves());
        ent.setButtons(req.getButtons());
        ent.setPockets(req.getPockets());
        ent.setLining(req.getLining());
        ent.setFabric(req.getFabric());
        ent.setColorHex(req.getColorHex());
        ent.setFitType(req.getFitType());
        
        ent.setShoulders(req.getShoulders());
        ent.setChest(req.getChest());
        ent.setWaist(req.getWaist());
        ent.setHips(req.getHips());
        ent.setSleeveLength(req.getSleeveLength());
        ent.setTrouserLength(req.getTrouserLength());
        
        ent.setHeight(req.getHeight());
        ent.setWeight(req.getWeight());
        ent.setNotes(req.getNotes());
        
        ent.setDeliveryType(DeliveryType.valueOf(req.getDeliveryType().toUpperCase()));
        ent.setDeliveryAddress(req.getDeliveryAddress());
        ent.setPickupPoint(req.getPickupPoint());
        ent.setMpesaTransactionId(req.getMpesaTransactionId().trim());
        ent.setTotalPrice(req.getTotalPrice());
        
        ent.setClientGeneratedId(req.getClientGeneratedId());
        ent.setSyncPending(req.getClientGeneratedId() != null);
        ent.setSyncSource("Android-KMP");
        
        return ent;
    }

    private OrderResponse mapEntityToResponse(OrderEntity ent) {
        return new OrderResponse(
                ent.getId(),
                ent.getClientName(),
                ent.getModel(),
                ent.getOrderStatus().name(),
                ent.getPaymentStatus().name(),
                ent.getTotalPrice(),
                ent.getPaymentProofUrl(),
                ent.getCreatedAtDate() != null ? ent.getCreatedAtDate() : LocalDateTime.now(),
                ent.getUpdatedAtDate() != null ? ent.getUpdatedAtDate() : LocalDateTime.now()
        );
    }
}
