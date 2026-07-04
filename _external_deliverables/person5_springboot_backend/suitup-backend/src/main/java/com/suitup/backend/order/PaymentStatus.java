package com.suitup.backend.order;

/**
 * ESTADOS EXCLUSIVOS DO PAGAMENTO M-PESA
 * Auxilia os administradores na filtragem e aprovação manual de transações em Moçambique.
 */
public enum PaymentStatus {
    PENDING,          // Pedido criado mas comprovativo M-Pesa ainda não foi associado
    PROOF_UPLOADED,   // Imagem de fatura carregada em multipart pendente de revisão
    VALIDATED,        // Transação correspondente validada em banco
    REJECTED          // Comprovativo falso, parcial ou exposto a fraudes
}
