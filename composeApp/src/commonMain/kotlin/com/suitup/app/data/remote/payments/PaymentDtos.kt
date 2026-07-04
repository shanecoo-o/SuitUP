package com.suitup.app.data.remote.payments

import kotlinx.serialization.Serializable

@Serializable
enum class PaymentMethodDto { MPESA, EMOLA, BANK_TRANSFER, CASH_ON_PICKUP }

@Serializable
enum class PaymentStatusDto { PENDING, CONFIRMED, REJECTED }

@Serializable
data class SubmitPaymentRequestDto(
    val method: PaymentMethodDto,
    val amount: Double,
    val transactionReference: String? = null,
    val proofFileId: String? = null,
    val note: String? = null,
)

@Serializable
data class PaymentStatusHistoryDto(
    val id: String,
    val oldStatus: PaymentStatusDto? = null,
    val newStatus: PaymentStatusDto,
    val changedByUserId: String? = null,
    val note: String? = null,
    val createdAt: String,
)

@Serializable
data class PaymentDto(
    val id: String,
    val orderId: String,
    val method: PaymentMethodDto,
    val status: PaymentStatusDto,
    val amount: Double,
    val currency: String,
    val transactionReference: String? = null,
    val proofFileId: String? = null,
    val submittedAt: String,
    val confirmedAt: String? = null,
    val rejectedAt: String? = null,
    val reviewedByUserId: String? = null,
    val rejectionReason: String? = null,
    val statusHistory: List<PaymentStatusHistoryDto> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ConfirmPaymentRequestDto(val note: String? = null)

@Serializable
data class RejectPaymentRequestDto(
    val rejectionReason: String,
    val note: String? = null,
)

@Serializable
data class PaymentProofMetadataRequestDto(
    val originalName: String,
    val storedName: String? = null,
    val contentType: String,
    val sizeBytes: Long,
    val storagePath: String? = null,
    val publicUrl: String? = null,
)
