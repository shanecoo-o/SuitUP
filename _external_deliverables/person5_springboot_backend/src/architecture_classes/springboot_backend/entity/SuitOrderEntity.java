package mz.ac.unizambeze.suitup.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ENTIDADE JPA DE ENCOMENDA (Suit Order)
 * Armazena as seleções de estilo, medidas corporais detalhadas para o alfaiate,
 * o comprovativo M-Pesa (transação e imagem), e o estado de tracking de confeção.
 */
@Entity
@Table(name = "suit_orders")
public class SuitOrderEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id; // Ex: SUIT-2026-X

    // Detalhes de Design e Caimento
    @Column(nullable = false)
    private String modelName; // Ex: "Classic Slim-Fit"

    @Column(nullable = false)
    private String lapelType; // Ex: "Notched Lapel"

    @Column(nullable = false, length = 7)
    private String fabricColor; // Hexadecimal (Ex: #C8A96A)

    @Column(nullable = false)
    private String liningPatternKey; // Ex: "Solid Maroon"

    @Column(nullable = false)
    private String fitType; // Slim, Modern ou Classic

    // Medidas Físicas em centímetros
    @Column(nullable = false)
    private Double shouldersSize;

    @Column(nullable = false)
    private Double chestSize;

    @Column(nullable = false)
    private Double sleevesSize;

    @Column(nullable = false)
    private Double waistSize;

    // Transação e Comprovante de Pagamento M-Pesa
    @Column(name = "mpesa_transaction_id", nullable = false, unique = true)
    private String mpesaTransactionId; // Ex: "JK9875HH8"

    @Column(name = "payment_proof_url", nullable = true)
    private String paymentProofUrl; // URL relativa ao ficheiro salvo no servidor físico (Ex: /uploads/payment_proofs/...)

    @Column(name = "payment_validated", nullable = false)
    private boolean paymentValidated = false; // Se a administração validou a transação M-Pesa

    @Column(name = "admin_notes", nullable = true, length = 500)
    private String adminNotes; // Notas internas da equipe de alfaiatia

    // Fluxo Industrial / Rastreamento de Confeção Física
    @Column(nullable = false)
    private String statusStr; // "Design Concluído", "Corte", "Confecção", "Finalizado"

    @Column(nullable = false)
    private LocalDateTime createdAtDate;

    public SuitOrderEntity() {}

    public SuitOrderEntity(String id, String modelName, String lapelType, String fabricColor, 
                           String liningPatternKey, String fitType, Double shouldersSize, 
                           Double chestSize, Double sleevesSize, Double waistSize, 
                           String mpesaTransactionId, String statusStr) {
        this.id = id;
        this.modelName = modelName;
        this.lapelType = lapelType;
        this.fabricColor = fabricColor;
        this.liningPatternKey = liningPatternKey;
        this.fitType = fitType;
        this.shouldersSize = shouldersSize;
        this.chestSize = chestSize;
        this.sleevesSize = sleevesSize;
        this.waistSize = waistSize;
        this.mpesaTransactionId = mpesaTransactionId;
        this.statusStr = statusStr;
        this.createdAtDate = LocalDateTime.now();
    }

    // --- GETTERS E SETTERS ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getLapelType() { return lapelType; }
    public void setLapelType(String lapelType) { this.lapelType = lapelType; }

    public String getFabricColor() { return fabricColor; }
    public void setFabricColor(String fabricColor) { this.fabricColor = fabricColor; }

    public String getLiningPatternKey() { return liningPatternKey; }
    public void setLiningPatternKey(String liningPatternKey) { this.liningPatternKey = liningPatternKey; }

    public String getFitType() { return fitType; }
    public void setFitType(String fitType) { this.fitType = fitType; }

    public Double getShouldersSize() { return shouldersSize; }
    public void setShouldersSize(Double shouldersSize) { this.shouldersSize = shouldersSize; }

    public Double getChestSize() { return chestSize; }
    public void setChestSize(Double chestSize) { this.chestSize = chestSize; }

    public Double getSleevesSize() { return sleevesSize; }
    public void setSleevesSize(Double sleevesSize) { this.sleevesSize = sleevesSize; }

    public Double getWaistSize() { return waistSize; }
    public void setWaistSize(Double waistSize) { this.waistSize = waistSize; }

    public String getMpesaTransactionId() { return mpesaTransactionId; }
    public void setMpesaTransactionId(String mpesaTransactionId) { this.mpesaTransactionId = mpesaTransactionId; }

    public String getPaymentProofUrl() { return paymentProofUrl; }
    public void setPaymentProofUrl(String paymentProofUrl) { this.paymentProofUrl = paymentProofUrl; }

    public boolean isPaymentValidated() { return paymentValidated; }
    public void setPaymentValidated(boolean paymentValidated) { this.paymentValidated = paymentValidated; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public String getStatusStr() { return statusStr; }
    public void setStatusStr(String statusStr) { this.statusStr = statusStr; }

    public LocalDateTime getCreatedAtDate() { return createdAtDate; }
    public void setCreatedAtDate(LocalDateTime createdAtDate) { this.createdAtDate = createdAtDate; }
}
