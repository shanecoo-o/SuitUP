package com.suitup.backend.order.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * REPRESENTAÇÃO REATIVA DE RETORNO (Order Response Layout)
 * Devolve de forma higienizada e compactada os dados de terno para consumo móvel,
 * incluindo fases estruturais do processo de costura/tracking.
 */
public class OrderResponse {
    private String id;
    private String clientName;
    private String model;
    private String status;
    private String paymentStatus;
    private Double totalPrice;
    private String paymentProofUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> trackingSteps = new ArrayList<>();

    public OrderResponse() {}

    public OrderResponse(String id, String clientName, String model, String status, 
                         String paymentStatus, Double totalPrice, String paymentProofUrl,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.clientName = clientName;
        this.model = model;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.totalPrice = totalPrice;
        this.paymentProofUrl = paymentProofUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.generateTrackingSteps(status);
    }

    /**
     * Gera dinamicamente os passos do rastreamento dependendo do estado real
     */
    private void generateTrackingSteps(String currentStatus) {
        this.trackingSteps.add("1. Design e Medidas — Concluído");
        
        if (currentStatus.equals("PAYMENT_PENDING")) {
            this.trackingSteps.add("2. Pagamento M-Pesa — Aguardando Comprovativo [Ação Necessária]");
        } else {
            this.trackingSteps.add("2. Pagamento M-Pesa — Submetido & Confirmado");
        }

        if (currentStatus.equals("PAYMENT_VALIDATED") || currentStatus.equals("IN_PRODUCTION") ||
            currentStatus.equals("READY_FOR_PICKUP") || currentStatus.equals("OUT_FOR_DELIVERY") || currentStatus.equals("DELIVERED")) {
            this.trackingSteps.add("3. Triagem de Alfaiate — Medidas Validada e Corte");
        } else if (currentStatus.equals("Pagamento Rejeitado")) {
            this.trackingSteps.add("3. Triagem — Rejeitado devido a erro de transação M-Pesa");
        } else {
            this.trackingSteps.add("3. Triagem — Pendente");
        }

        if (currentStatus.equals("IN_PRODUCTION") || currentStatus.equals("READY_FOR_PICKUP") || 
            currentStatus.equals("OUT_FOR_DELIVERY") || currentStatus.equals("DELIVERED")) {
            this.trackingSteps.add("4. Produção e Confecção — Em andamento nas Oficinas");
        } else {
            this.trackingSteps.add("4. Produção — Pendente");
        }

        if (currentStatus.equals("READY_FOR_PICKUP") || currentStatus.equals("OUT_FOR_DELIVERY") || currentStatus.equals("DELIVERED")) {
            this.trackingSteps.add("5. Finalização e Costura Manual — Pronto!");
        } else {
            this.trackingSteps.add("5. Finalização — Em progresso");
        }
    }

    // --- GETTERS E SETTERS ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getPaymentProofUrl() { return paymentProofUrl; }
    public void setPaymentProofUrl(String paymentProofUrl) { this.paymentProofUrl = paymentProofUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<String> getTrackingSteps() { return trackingSteps; }
    public void setTrackingSteps(List<String> trackingSteps) { this.trackingSteps = trackingSteps; }
}
