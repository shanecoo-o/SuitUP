package com.suitup.backend.order.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * REQUISITO DE ADMINISTRAÇÃO E OFICINAS
 * Permite que Alfaiates alterem o rastreamento do fato (Ex: IN_PRODUCTION).
 */
public class UpdateOrderStatusRequest {

    @NotBlank(message = "O novo estado físico é obrigatório")
    private String status; // Ex: PAYMENT_VALIDATED, IN_PRODUCTION, READY_FOR_PICKUP

    private String notes; // Justificativa ou notas do corte das mangas e forro

    public UpdateOrderStatusRequest() {}

    public UpdateOrderStatusRequest(String status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
