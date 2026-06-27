package com.suitup.backend.order.dto;

import jakarta.validation.constraints.*;

/**
 * REQUISITOS PARA CRIAÇÃO DE PEDIDO DE DESIGN (Bespoke Suit)
 * Agrega e valida de forma estrita cada opção de desenho estilizado e medidas
 * inseridos por utilizadores.
 */
public class CreateOrderRequest {

    @NotBlank(message = "O nome do cliente é obrigatório")
    private String clientName;

    @NotBlank(message = "Contacto telefónico obrigatório")
    @Pattern(regexp = "^\\+?[0-9\\s\\-]{8,20}$", message = "Número telefónico inválido")
    private String clientPhone;

    @NotBlank(message = "E-mail de contacto obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String clientEmail;

    @NotBlank(message = "Selecione o modelo do fato")
    private String model;

    @NotBlank(message = "Estilo de lapela obrigatório")
    private String lapel;

    @NotBlank(message = "Configuração de punho/manga obrigatório")
    private String sleeves;

    @NotBlank(message = "Configuração de botões obrigatória")
    private String buttons;

    @NotBlank(message = "Tipo de bolsos obrigatório")
    private String pockets;

    @NotBlank(message = "Material do forro interno obrigatório")
    private String lining;

    @NotBlank(message = "Escolha o tecido de confecção")
    private String fabric;

    @NotBlank(message = "Indique o código hexadecimal da cor")
    @Size(min = 4, max = 7, message = "Cor deve ser um Hexadecimal (Ex: #FF0000)")
    private String colorHex;

    @NotBlank(message = "Tipo de caimento/ajuste é obrigatório")
    private String fitType;

    // Medidas anatómicas (maiores do que zero de acordo com as regras de negócio)
    @NotNull(message = "Medida do ombro necessária")
    @DecimalMin(value = "10.0", message = "Ombro deve ser maior que 10 cm")
    private Double shoulders;

    @NotNull(message = "Medida do peito necessária")
    @DecimalMin(value = "20.0", message = "Peito deve ser maior que 20 cm")
    private Double chest;

    @NotNull(message = "Medida de cintura necessária")
    @DecimalMin(value = "20.0", message = "Cintura deve ser maior que 20 cm")
    private Double waist;

    @NotNull(message = "Medida do quadril necessária")
    @DecimalMin(value = "20.0", message = "Hips deve ser maior que 20 cm")
    private Double hips;

    @NotNull(message = "Medida da manga necessária")
    @DecimalMin(value = "10.0", message = "Manga deve ser maior que 10 cm")
    private Double sleeveLength;

    @NotNull(message = "Comprimento das calças necessário")
    @DecimalMin(value = "30.0", message = "Comprimento de calças deve ser superior a 30 cm")
    private Double trouserLength;

    @NotNull(message = "Sua altura é obrigatória")
    @DecimalMin(value = "1.0", message = "Altura corporal mínima exigida é 1.0 metro/cm")
    private Double height;

    @NotNull(message = "Seu peso corporal aproximado é obrigatório")
    @DecimalMin(value = "30.0", message = "Peso corporal mínimo exigido é 30 Kg")
    private Double weight;

    private String notes; // Comentários adicionais de produção

    @NotBlank(message = "Informe o tipo de entrega (DELIVERY ou PICKUP)")
    private String deliveryType;

    private String deliveryAddress;
    private String pickupPoint;

    @NotBlank(message = "O ID de Transação do M-Pesa é obrigatório para iniciar o processo")
    private String mpesaTransactionId;

    @NotNull(message = "Preço total é obrigatório")
    private Double totalPrice;

    private String clientGeneratedId; // ID provisório gerado via app enquanto offline

    public CreateOrderRequest() {}

    // --- GETTERS E SETTERS ---
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

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPickupPoint() { return pickupPoint; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }

    public String getMpesaTransactionId() { return mpesaTransactionId; }
    public void setMpesaTransactionId(String mpesaTransactionId) { this.mpesaTransactionId = mpesaTransactionId; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getClientGeneratedId() { return clientGeneratedId; }
    public void setClientGeneratedId(String clientGeneratedId) { this.clientGeneratedId = clientGeneratedId; }
}
