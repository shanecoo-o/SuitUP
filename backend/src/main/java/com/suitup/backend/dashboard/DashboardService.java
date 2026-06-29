package com.suitup.backend.dashboard;

import com.suitup.backend.catalog.SuitModelRepository;
import com.suitup.backend.common.MoneyValidator;
import com.suitup.backend.dashboard.dto.AdminDashboardResponse;
import com.suitup.backend.dashboard.dto.DashboardOrderSummaryResponse;
import com.suitup.backend.dashboard.dto.DashboardPaymentSummaryResponse;
import com.suitup.backend.order.OrderEntity;
import com.suitup.backend.order.OrderRepository;
import com.suitup.backend.order.OrderStatus;
import com.suitup.backend.payment.PaymentRepository;
import com.suitup.backend.payment.PaymentEntity;
import com.suitup.backend.payment.PaymentStatus;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final SuitModelRepository suitModelRepository;

    public DashboardService(
        OrderRepository orderRepository,
        PaymentRepository paymentRepository,
        SuitModelRepository suitModelRepository
    ) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.suitModelRepository = suitModelRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        Map<OrderStatus, Long> counts = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, orderRepository.countByStatus(status));
        }
        BigDecimal revenue = paymentRepository.sumAmountByStatus(PaymentStatus.CONFIRMED);

        return new AdminDashboardResponse(
            orderRepository.count(),
            suitModelRepository.countByActive(true),
            suitModelRepository.countByActive(false),
            paymentRepository.countByStatus(PaymentStatus.PENDING),
            paymentRepository.countByStatus(PaymentStatus.CONFIRMED),
            paymentRepository.countByStatus(PaymentStatus.REJECTED),
            revenue == null ? BigDecimal.ZERO : revenue,
            MoneyValidator.DEFAULT_CURRENCY,
            Map.copyOf(counts),
            orderRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(this::toOrderSummary)
                .toList(),
            paymentRepository.findTop5ByStatusOrderBySubmittedAtDesc(PaymentStatus.PENDING).stream()
                .map(this::toPaymentSummary)
                .toList()
        );
    }

    private DashboardOrderSummaryResponse toOrderSummary(OrderEntity order) {
        return new DashboardOrderSummaryResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getCustomerName(),
            order.getCustomerPhone(),
            order.getStatus(),
            order.getPaymentStatus(),
            order.getTotalAmount(),
            order.getCurrency(),
            order.getCreatedAt()
        );
    }

    private DashboardPaymentSummaryResponse toPaymentSummary(PaymentEntity payment) {
        return new DashboardPaymentSummaryResponse(
            payment.getId(),
            payment.getOrder().getId(),
            payment.getOrder().getOrderNumber(),
            payment.getOrder().getCustomerName(),
            payment.getMethod(),
            payment.getStatus(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getTransactionReference(),
            payment.getSubmittedAt()
        );
    }
}
