package com.suitup.backend.order;

/**
 * ESTADOS EXCLUSIVOS DE RASTREAMENTO FÍSICO (Tracking steps)
 * Representa cada fase do processo industrial de alfaiataria bespoke.
 */
public enum OrderStatus {
    PAYMENT_PENDING,     // Aguardando upload e triagem do comprovativo M-Pesa
    PAYMENT_VALIDATED,   // Pagamento validado adm, pronto para tirar moldes e corte
    IN_PRODUCTION,       // Nas oficinas recebendo costura e forro sob medida
    READY_FOR_PICKUP,    // Fato finalizado pendente de levantamento no ateliê UniZambeze
    OUT_FOR_DELIVERY,    // A caminho do endereço de entrega selecionado no app
    DELIVERED,           // Entregue formalmente ao portador
    CANCELLED            // Cancelado por inviabilidade técnica de medidas ou estorno
}
