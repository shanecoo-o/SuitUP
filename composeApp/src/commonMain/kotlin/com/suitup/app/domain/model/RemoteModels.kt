package com.suitup.app.domain.model

data class PaymentRecord(
    val id: String,
    val orderId: String,
    val method: String,
    val status: PaymentStatus,
    val amountMt: Int,
    val currency: String,
    val transactionReference: String?,
    val proofFileId: String?,
    val submittedAt: String,
    val confirmedAt: String?,
    val rejectedAt: String?,
    val reviewedByUserId: String?,
    val rejectionReason: String?,
)

data class AdminOrderSummary(
    val id: String,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val status: String,
    val paymentStatus: PaymentStatus,
    val totalMt: Int,
    val createdAt: String,
)

data class AdminPaymentSummary(
    val paymentId: String,
    val orderId: String,
    val orderNumber: String,
    val customerName: String,
    val method: String,
    val status: PaymentStatus,
    val amountMt: Int,
    val transactionReference: String?,
    val submittedAt: String,
)

data class AdminDashboardSummary(
    val totalOrders: Int,
    val activeSuitModels: Int,
    val inactiveSuitModels: Int,
    val pendingPayments: Int,
    val confirmedPayments: Int,
    val rejectedPayments: Int,
    val confirmedRevenueMt: Int,
    val ordersByStatus: Map<String, Int>,
    val recentOrders: List<AdminOrderSummary>,
    val recentPendingPayments: List<AdminPaymentSummary>,
)
