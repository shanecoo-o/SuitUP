package mz.ac.unizambeze.suitup.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ENTIDADE JPA (Spring Boot + Hibernate)
 * Mapeia os dados do pedido de fato sob medida para uma tabela PostgreSQL herdada
 * no servidor central da nuvem do Ateliê de Alfaiataria.
 */
@Entity
@Table(name = "suit_orders")
public class SuitOrderEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id; // ID gerado (Ex: SUIT-2026-X)

    // Detalhes da Configuração de Design do Fato
    @Column(nullable = false)
    private String modelName; // Ex: "Classic Slim-Fit", "Modern Double-Breasted"

    @Column(nullable = false)
    private String lapelType; // Ex: "Notched Lapel", "Peak Lapel"

    @Column(nullable = false, length = 7)
    private String fabricColor; // Guardado como Hex (Ex: #1a1a1a) ou nome

    @Column(nullable = false)
    private String liningPatternKey; // Tipo de forro interior (ex: maroon, silk)

    @Column(nullable = false)
    private String fitType; // Slim, Modern ou Classic

    // Atributos de Medidas Físicas do Cliente (em Centímetros)
    @Column(nullable = false)
    private Double shouldersSize;

    @Column(nullable = false)
    private Double chestSize;

    @Column(nullable = false)
    private Double sleevesSize;

    @Column(nullable = false)
    private Double waistSize;

    // Dados de Pagamento via Carteira Móvel de Moçambique
    @Column(name = "mpesa_transaction_id", nullable = false, unique = true)
    private String mpesaTransactionId; // Chave para auditoria administrativa

    // Gestão de Processo Industrial / Alfaiataria Física
    @Column(nullable = false)
    private String statusStr; // "Design Concluído", "Corte", "Confecção", "Finalizado"

    @Column(nullable = false)
    private LocalDateTime createdAtDate;

    // Construtor Padrão para o JPA/Hibernate
    public SuitOrderEntity() {}

    // Construtor Completo
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

    // --- GETTERS E SETTERS (Essenciais para extração e mapeamento do Jackson JSON) ---

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

    public String getStatusStr() { return statusStr; }
    public void setStatusStr(String statusStr) { this.statusStr = statusStr; }

    public LocalDateTime getCreatedAtDate() { return createdAtDate; }
    public void setCreatedAtDate(LocalDateTime createdAtDate) { this.createdAtDate = createdAtDate; }
}
