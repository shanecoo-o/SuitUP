package com.suitup.app.data.repository.remote

import com.suitup.app.data.mapper.toDomain
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.map
import com.suitup.app.data.remote.payments.ConfirmPaymentRequestDto
import com.suitup.app.data.remote.payments.PaymentProofMetadataRequestDto
import com.suitup.app.data.remote.payments.PaymentStatusHistoryDto
import com.suitup.app.data.remote.payments.PaymentsApi
import com.suitup.app.data.remote.payments.RejectPaymentRequestDto
import com.suitup.app.data.remote.payments.SubmitPaymentRequestDto
import com.suitup.app.data.remote.upload.UploadedFileMetadataDto
import com.suitup.app.domain.model.PaymentRecord

class RemotePaymentRepository(private val api: PaymentsApi) {
    suspend fun submit(
        orderId: String,
        request: SubmitPaymentRequestDto,
    ): ApiResult<PaymentRecord> = api.submitPayment(orderId, request).map { it.toDomain() }

    suspend fun getLatest(orderId: String): ApiResult<PaymentRecord> =
        api.getPaymentForOrder(orderId).map { it.toDomain() }

    suspend fun getAttempts(orderId: String): ApiResult<List<PaymentRecord>> =
        api.getPaymentAttempts(orderId).map { payments -> payments.map { it.toDomain() } }

    suspend fun getTimeline(orderId: String): ApiResult<List<PaymentStatusHistoryDto>> =
        api.getPaymentTimeline(orderId)

    suspend fun registerProofMetadata(
        orderId: String,
        request: PaymentProofMetadataRequestDto,
    ): ApiResult<UploadedFileMetadataDto> = api.registerProofMetadata(orderId, request)

    suspend fun adminGetAll(): ApiResult<List<PaymentRecord>> =
        api.adminGetPayments().map { payments -> payments.map { it.toDomain() } }

    suspend fun adminGetPending(): ApiResult<List<PaymentRecord>> =
        api.adminGetPendingPayments().map { payments -> payments.map { it.toDomain() } }

    suspend fun adminConfirm(
        paymentId: String,
        note: String? = null,
    ): ApiResult<PaymentRecord> = api.adminConfirmPayment(
        paymentId,
        ConfirmPaymentRequestDto(note),
    ).map { it.toDomain() }

    suspend fun adminReject(
        paymentId: String,
        reason: String,
        note: String? = null,
    ): ApiResult<PaymentRecord> = api.adminRejectPayment(
        paymentId,
        RejectPaymentRequestDto(reason, note),
    ).map { it.toDomain() }
}
