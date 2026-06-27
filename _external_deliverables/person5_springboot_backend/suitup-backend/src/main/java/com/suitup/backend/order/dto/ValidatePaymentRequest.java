package com.suitup.backend.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * REQUISITO DE ADMINISTRAÇÃO E CONCILIAÇÃO FINANCEIRA
 * Permite aprovar ou reprovar manualmente faturas de depósitos enviadas pelos clientes.
 */
public class ValidatePaymentRequest {

    @NotNull(message = "A indicação do estado de validação financeira é obrigatória")
    private Boolean approved; // true para VALIDATED, false para REJECTED

    private String comments; // Razão complementar (Ex: "Código de transação M-Pesa não localizado")

    public ValidatePaymentRequest() {}

    public ValidatePaymentRequest(Boolean approved, String comments) {
        this.approved = approved;
        this.comments = comments;
    }

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
