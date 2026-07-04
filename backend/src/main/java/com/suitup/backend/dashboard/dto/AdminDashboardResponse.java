package com.suitup.backend.dashboard.dto;

import com.suitup.backend.order.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record AdminDashboardResponse(
    long totalOrders,
    long activeSuitModels,
    long inactiveSuitModels,
    long pendingPayments,
    long confirmedPayments,
    long rejectedPayments,
    BigDecimal confirmedRevenue,
    String currency,
    Map<OrderStatus, Long> ordersByStatus,
    List<DashboardOrderSummaryResponse> recentOrders,
    List<DashboardPaymentSummaryResponse> recentPendingPayments
) {
}
