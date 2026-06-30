package com.suitup.app.data.remote.dashboard

import com.suitup.app.data.remote.orders.OrderStatusDto
import com.suitup.app.data.remote.payments.PaymentMethodDto
import com.suitup.app.data.remote.payments.PaymentStatusDto
import kotlinx.serialization.Serializable

@Serializable
data class DashboardOrderSummaryDto(
    val id: String,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val status: OrderStatusDto,
    val paymentStatus: PaymentStatusDto,
    val totalAmount: Double,
    val currency: String,
    val createdAt: String,
)

@Serializable
data class DashboardPaymentSummaryDto(
    val paymentId: String,
    val orderId: String,
    val orderNumber: String,
    val customerName: String,
    val method: PaymentMethodDto,
    val status: PaymentStatusDto,
    val amount: Double,
    val currency: String,
    val transactionReference: String? = null,
    val submittedAt: String,
)

@Serializable
data class AdminDashboardDto(
    val totalOrders: Long,
    val activeSuitModels: Long,
    val inactiveSuitModels: Long,
    val pendingPayments: Long,
    val confirmedPayments: Long,
    val rejectedPayments: Long,
    val confirmedRevenue: Double,
    val currency: String,
    val ordersByStatus: Map<String, Long> = emptyMap(),
    val recentOrders: List<DashboardOrderSummaryDto> = emptyList(),
    val recentPendingPayments: List<DashboardPaymentSummaryDto> = emptyList(),
)
