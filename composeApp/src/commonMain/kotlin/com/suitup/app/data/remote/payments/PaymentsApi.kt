package com.suitup.app.data.remote.payments

import com.suitup.app.data.remote.config.ApiConfig
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.RemoteJson
import com.suitup.app.data.remote.http.safeApiCall
import com.suitup.app.data.remote.upload.UploadedFileMetadataDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.Json

class PaymentsApi(
    private val client: HttpClient,
    private val config: ApiConfig,
    private val json: Json = RemoteJson.instance,
) {
    suspend fun submitPayment(
        orderId: String,
        request: SubmitPaymentRequestDto,
    ): ApiResult<PaymentDto> = safeApiCall(json) {
        client.post(config.url("/api/orders/$orderId/payment")) { setBody(request) }
    }

    suspend fun getPaymentForOrder(orderId: String): ApiResult<PaymentDto> =
        safeApiCall(json) { client.get(config.url("/api/orders/$orderId/payment")) }

    suspend fun getPaymentAttempts(orderId: String): ApiResult<List<PaymentDto>> =
        safeApiCall(json) { client.get(config.url("/api/orders/$orderId/payments")) }

    suspend fun getPaymentTimeline(orderId: String): ApiResult<List<PaymentStatusHistoryDto>> =
        safeApiCall(json) { client.get(config.url("/api/orders/$orderId/payment-timeline")) }

    suspend fun registerProofMetadata(
        orderId: String,
        request: PaymentProofMetadataRequestDto,
    ): ApiResult<UploadedFileMetadataDto> = safeApiCall(json) {
        client.post(config.url("/api/orders/$orderId/payment-proof-metadata")) { setBody(request) }
    }

    suspend fun adminGetPayments(): ApiResult<List<PaymentDto>> =
        safeApiCall(json) { client.get(config.url("/api/admin/payments")) }

    suspend fun adminGetPendingPayments(): ApiResult<List<PaymentDto>> =
        safeApiCall(json) { client.get(config.url("/api/admin/payments/pending")) }

    suspend fun adminGetPaymentById(paymentId: String): ApiResult<PaymentDto> =
        safeApiCall(json) { client.get(config.url("/api/admin/payments/$paymentId")) }

    suspend fun adminGetPaymentTimeline(
        paymentId: String,
    ): ApiResult<List<PaymentStatusHistoryDto>> =
        safeApiCall(json) { client.get(config.url("/api/admin/payments/$paymentId/timeline")) }

    suspend fun adminConfirmPayment(
        paymentId: String,
        request: ConfirmPaymentRequestDto,
    ): ApiResult<PaymentDto> = safeApiCall(json) {
        client.patch(config.url("/api/admin/payments/$paymentId/confirm")) { setBody(request) }
    }

    suspend fun adminRejectPayment(
        paymentId: String,
        request: RejectPaymentRequestDto,
    ): ApiResult<PaymentDto> = safeApiCall(json) {
        client.patch(config.url("/api/admin/payments/$paymentId/reject")) { setBody(request) }
    }
}
