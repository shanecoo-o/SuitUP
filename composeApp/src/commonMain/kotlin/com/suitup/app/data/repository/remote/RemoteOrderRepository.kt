package com.suitup.app.data.repository.remote

import com.suitup.app.data.mapper.toDomain
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.map
import com.suitup.app.data.remote.orders.CreateOrderRequestDto
import com.suitup.app.data.remote.orders.OrderStatusHistoryDto
import com.suitup.app.data.remote.orders.OrdersApi
import com.suitup.app.data.remote.orders.UpdateOrderStatusRequestDto
import com.suitup.app.domain.model.Pedido

class RemoteOrderRepository(private val api: OrdersApi) {
    suspend fun create(
        request: CreateOrderRequestDto,
        idempotencyKey: String? = null,
    ): ApiResult<Pedido> = api.createOrder(request, idempotencyKey).map { it.toDomain() }

    suspend fun getMine(): ApiResult<List<Pedido>> =
        api.getMyOrders().map { orders -> orders.map { it.toDomain() } }

    suspend fun getById(id: String): ApiResult<Pedido> =
        api.getOrderById(id).map { it.toDomain() }

    suspend fun getTimeline(id: String): ApiResult<List<OrderStatusHistoryDto>> =
        api.getOrderTimeline(id)

    suspend fun adminGetAll(): ApiResult<List<Pedido>> =
        api.adminGetOrders().map { orders -> orders.map { it.toDomain() } }

    suspend fun adminGetById(id: String): ApiResult<Pedido> =
        api.adminGetOrderById(id).map { it.toDomain() }

    suspend fun adminGetTimeline(id: String): ApiResult<List<OrderStatusHistoryDto>> =
        api.adminGetOrderTimeline(id)

    suspend fun adminUpdateStatus(
        id: String,
        request: UpdateOrderStatusRequestDto,
    ): ApiResult<Pedido> = api.adminUpdateOrderStatus(id, request).map { it.toDomain() }
}
