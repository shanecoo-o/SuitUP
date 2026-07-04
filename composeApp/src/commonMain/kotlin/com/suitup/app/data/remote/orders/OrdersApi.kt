package com.suitup.app.data.remote.orders

import com.suitup.app.data.remote.config.ApiConfig
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.RemoteJson
import com.suitup.app.data.remote.http.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.Json

class OrdersApi(
    private val client: HttpClient,
    private val config: ApiConfig,
    private val json: Json = RemoteJson.instance,
) {
    suspend fun createOrder(
        request: CreateOrderRequestDto,
        idempotencyKey: String? = null,
    ): ApiResult<OrderDto> = safeApiCall(json) {
        client.post(config.url("/api/orders")) {
            if (!idempotencyKey.isNullOrBlank()) header("Idempotency-Key", idempotencyKey)
            setBody(request)
        }
    }

    suspend fun getMyOrders(): ApiResult<List<OrderDto>> =
        safeApiCall(json) { client.get(config.url("/api/orders/my")) }

    suspend fun getOrderById(id: String): ApiResult<OrderDto> =
        safeApiCall(json) { client.get(config.url("/api/orders/$id")) }

    suspend fun getOrderTimeline(id: String): ApiResult<List<OrderStatusHistoryDto>> =
        safeApiCall(json) { client.get(config.url("/api/orders/$id/timeline")) }

    suspend fun adminGetOrders(): ApiResult<List<OrderDto>> =
        safeApiCall(json) { client.get(config.url("/api/admin/orders")) }

    suspend fun adminGetOrderById(id: String): ApiResult<OrderDto> =
        safeApiCall(json) { client.get(config.url("/api/admin/orders/$id")) }

    suspend fun adminGetOrderTimeline(id: String): ApiResult<List<OrderStatusHistoryDto>> =
        safeApiCall(json) { client.get(config.url("/api/admin/orders/$id/timeline")) }

    suspend fun adminUpdateOrderStatus(
        id: String,
        request: UpdateOrderStatusRequestDto,
    ): ApiResult<OrderDto> = safeApiCall(json) {
        client.patch(config.url("/api/admin/orders/$id/status")) { setBody(request) }
    }
}
