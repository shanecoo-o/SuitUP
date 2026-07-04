package com.suitup.backend.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.suitup.backend.catalog.SuitModelRepository;
import com.suitup.backend.dashboard.dto.AdminDashboardResponse;
import com.suitup.backend.order.OrderRepository;
import com.suitup.backend.order.OrderEntity;
import com.suitup.backend.order.OrderStatus;
import com.suitup.backend.payment.PaymentRepository;
import com.suitup.backend.payment.PaymentEntity;
import com.suitup.backend.payment.PaymentMethod;
import com.suitup.backend.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DashboardServiceTest {

    @Test
    void aggregatesRepositoryMetrics() {
        OrderRepository orders = mock(OrderRepository.class);
        PaymentRepository payments = mock(PaymentRepository.class);
        SuitModelRepository catalog = mock(SuitModelRepository.class);
        when(orders.count()).thenReturn(12L);
        when(orders.countByStatus(OrderStatus.IN_PRODUCTION)).thenReturn(3L);
        when(catalog.countByActive(true)).thenReturn(6L);
        when(catalog.countByActive(false)).thenReturn(2L);
        when(payments.countByStatus(PaymentStatus.PENDING)).thenReturn(4L);
        when(payments.countByStatus(PaymentStatus.CONFIRMED)).thenReturn(7L);
        when(payments.countByStatus(PaymentStatus.REJECTED)).thenReturn(1L);
        when(payments.sumAmountByStatus(PaymentStatus.CONFIRMED)).thenReturn(new BigDecimal("45600.00"));
        OrderEntity recentOrder = order();
        PaymentEntity recentPayment = payment(recentOrder);
        when(orders.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(recentOrder));
        when(payments.findTop5ByStatusOrderBySubmittedAtDesc(PaymentStatus.PENDING))
            .thenReturn(List.of(recentPayment));

        AdminDashboardResponse response = new DashboardService(orders, payments, catalog).getDashboard();

        assertThat(response.totalOrders()).isEqualTo(12);
        assertThat(response.activeSuitModels()).isEqualTo(6);
        assertThat(response.pendingPayments()).isEqualTo(4);
        assertThat(response.confirmedRevenue()).isEqualByComparingTo("45600.00");
        assertThat(response.ordersByStatus()).containsEntry(OrderStatus.IN_PRODUCTION, 3L);
        assertThat(response.recentOrders()).singleElement()
            .extracting(item -> item.orderNumber())
            .isEqualTo("SU-2026-001");
        assertThat(response.recentPendingPayments()).singleElement()
            .extracting(item -> item.paymentId())
            .isEqualTo(recentPayment.getId());
    }

    @Test
    void treatsNullConfirmedRevenueAsZero() {
        OrderRepository orders = mock(OrderRepository.class);
        PaymentRepository payments = mock(PaymentRepository.class);
        SuitModelRepository catalog = mock(SuitModelRepository.class);
        when(orders.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of());
        when(payments.findTop5ByStatusOrderBySubmittedAtDesc(PaymentStatus.PENDING))
            .thenReturn(List.of());

        AdminDashboardResponse response = new DashboardService(orders, payments, catalog).getDashboard();

        assertThat(response.confirmedRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private OrderEntity order() {
        OrderEntity order = new OrderEntity();
        order.setId(UUID.randomUUID());
        order.setOrderNumber("SU-2026-001");
        order.setCustomerName("Joao Cliente");
        order.setCustomerPhone("+258840000001");
        order.setStatus(OrderStatus.RECEIVED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTotalAmount(new BigDecimal("8500.00"));
        order.setCurrency("MZN");
        order.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return order;
    }

    private PaymentEntity payment(OrderEntity order) {
        PaymentEntity payment = new PaymentEntity();
        payment.setId(UUID.randomUUID());
        payment.setOrder(order);
        payment.setMethod(PaymentMethod.MPESA);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("MZN");
        payment.setTransactionReference("MPESA-123");
        payment.setSubmittedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return payment;
    }
}
