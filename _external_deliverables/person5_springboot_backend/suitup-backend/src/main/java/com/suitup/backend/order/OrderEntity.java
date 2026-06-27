package com.suitup.backend.order;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ENTIDADE RELACIONAL DE ENCOMENDAS (Suit Orders)
 * Mantém todos os dados de design premium, caimentos personalizados,
 * medidas corporais para produção física, dados de pagamentos, auditores e flags
 * de sincronização offline.
 */
@Entity
@Table(name = "suit_orders")
public class OrderEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 50)
    private String id; // ID Único Gerado no Backend ou Client (Ex: SUIT-2026-X)

    // Detalhes cadastrais básicos para comunicação física
    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "client_phone", nullable = false, length = 30)
    private String clientPhone;

    @Column(name = "client_email", nullable = false, length = 100)
    private String clientEmail;

    // Design Model / Estilos Premium Bespoke
    @Column(nullable = false, length = 100)
    private String model; // Ex: Classic Slim-Fit, Zambeze Imperial

    @Column(nullable = false, length = 50)
    private String lapel; // Estilo da Lapela

    @Column(nullable = false, length = 50)
    private String sleeves; // Dobra ou punho de mangas

    @Column(nullable = false, length = 50)
    private String buttons; // Botões simples ou duplos

    @Column(nullable = false, length = 50)
    private String pockets; // Bolsos em aba, etc.

    @Column(nullable = false, length = 50)
    private String lining; // Forro interno escolhido (Vibrant Silk, Maroon, etc.)

    @Column(nullable = false, length = 100)
    private String fabric; // Tipo de tecido (Super 120s Lã Merino, Linho, etc.)

    @Column(name = "color_hex", nullable = false, length = 7)
    private String colorHex; // Ex: #1E3A8A (Navy blue)

    @Column(name = "fit_type", nullable = false, length = 40)
    private String fitType; // Slim, Classic, Modern

    // Medidas Anatómicas / Corporais detalhadas em Centímetros (Double)
    @Column(nullable = false)
    private Double shoulders;

    @Column(nullable = false)
    private Double chest;

    @Column(nullable = false)
    private Double waist;

    @Column(nullable = false)
    private Double hips;

    @Column(name = "sleeve_length", nullable = false)
    private Double sleeveLength;

    @Column(name = "trouser_length", nullable = false)
    private Double trouserLength;

    @Column(nullable = false)
    private Double height; // Altura geral do cliente em metros/cm

    @Column(nullable = false)
    private Double weight; // Peso bruto corporal em Kg

    @Column(columnDefinition = "TEXT")
    private String notes; // Recomendações e notas específicas de ajuste

    // Parâmetros de Despacho / Logística física
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false, length = 30)
    private DeliveryType deliveryType;

    @Column(name = "delivery_address")
    private String deliveryAddress; // Endereço físico de receção

    @Column(name = "pickup_point", length = 100)
    private String pickupPoint; // Ponto de recolha UniZambeze selecionado

    // Processamento e Auditoria Financeira M-Pesa
    @Column(name = "mpesa_transaction_id", nullable = false, unique = true, length = 50)
    private String mpesaTransactionId; // Id de transação para controle administrativo duplicado

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_proof_url")
    private String paymentProofUrl; // Localização física relativa do comprovativo Multipart

    // Controle Industrial das Oficinas
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 30)
    private OrderStatus orderStatus = OrderStatus.PAYMENT_PENDING;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice; // Preço do Fato Mestre em Meticais (MZN)

    // Sincronização offline e Auditoria do Dispositivo Cliente
    @Column(name = "sync_pending", nullable = false)
    private Boolean syncPending = false;

    @Column(name = "sync_source", length = 50)
    private String syncSource; // Ex: "Android-KMP", "iOS-App"

    @Column(name = "client_generated_id", length = 100)
    private String clientGeneratedId; // ID provisório offline para anti-duplicação idêntica

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAtDate;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAtDate;

    // Construtor Padrão
    public OrderEntity() {}

    @PrePersist
    protected void onCreate() {
        this.createdAtDate = LocalDateTime.now();
        this.updatedAtDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAtDate = LocalDateTime.now();
    }

    // --- GETTERS E SETTERS ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getLapel() { return lapel; }
    public void setLapel(String lapel) { this.lapel = lapel; }

    public String getSleeves() { return sleeves; }
    public void setSleeves(String sleeves) { this.sleeves = sleeves; }

    public String getButtons() { return buttons; }
    public void setButtons(String buttons) { this.buttons = buttons; }

    public String getPockets() { return pockets; }
    public void setPockets(String pockets) { this.pockets = pockets; }

    public String getLining() { return lining; }
    public void setLining(String lining) { this.lining = lining; }

    public String getFabric() { return fabric; }
    public void setFabric(String fabric) { this.fabric = fabric; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public String getFitType() { return fitType; }
    public void setFitType(String fitType) { this.fitType = fitType; }

    public Double getShoulders() { return shoulders; }
    public void setShoulders(Double shoulders) { this.shoulders = shoulders; }

    public Double getChest() { return chest; }
    public void setChest(Double chest) { this.chest = chest; }

    public Double getWaist() { return waist; }
    public void setWaist(Double waist) { this.waist = waist; }

    public Double getHips() { return hips; }
    public void setHips(Double hips) { this.hips = hips; }

    public Double getSleeveLength() { return sleeveLength; }
    public void setSleeveLength(Double sleeveLength) { this.sleeveLength = sleeveLength; }

    public Double getTrouserLength() { return trouserLength; }
    public void setTrouserLength(Double trouserLength) { this.trouserLength = trouserLength; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public DeliveryType getDeliveryType() { return deliveryType; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPickupPoint() { return pickupPoint; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }

    public String getMpesaTransactionId() { return mpesaTransactionId; }
    public void setMpesaTransactionId(String mpesaTransactionId) { this.mpesaTransactionId = mpesaTransactionId; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentProofUrl() { return paymentProofUrl; }
    public void setPaymentProofUrl(String paymentProofUrl) { this.paymentProofUrl = paymentProofUrl; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public Boolean getSyncPending() { return syncPending; }
    public void setSyncPending(Boolean syncPending) { this.syncPending = syncPending; }

    public String getSyncSource() { return syncSource; }
    public void setSyncSource(String syncSource) { this.syncSource = syncSource; }

    public String getClientGeneratedId() { return clientGeneratedId; }
    public void setClientGeneratedId(String clientGeneratedId) { this.clientGeneratedId = clientGeneratedId; }

    public LocalDateTime getCreatedAtDate() { return createdAtDate; }
    public void setCreatedAtDate(LocalDateTime createdAtDate) { this.createdAtDate = createdAtDate; }

    public LocalDateTime getUpdatedAtDate() { return updatedAtDate; }
    public void setUpdatedAtDate(LocalDateTime updatedAtDate) { this.updatedAtDate = updatedAtDate; }
}
