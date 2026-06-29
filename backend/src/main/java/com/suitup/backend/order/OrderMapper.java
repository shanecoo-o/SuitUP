package com.suitup.backend.order;

import com.suitup.backend.measurement.MeasurementEntity;
import com.suitup.backend.order.dto.MeasurementRequest;
import com.suitup.backend.order.dto.MeasurementResponse;
import com.suitup.backend.order.dto.OrderItemResponse;
import com.suitup.backend.order.dto.OrderResponse;
import com.suitup.backend.order.dto.OrderStatusHistoryResponse;
import com.suitup.backend.payment.PaymentMapper;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    private final PaymentMapper paymentMapper;

    public OrderMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    public MeasurementEntity toMeasurementEntity(MeasurementRequest request) {
        MeasurementEntity entity = new MeasurementEntity();
        entity.setHeightCm(request.heightCm());
        entity.setChestCm(request.chestCm());
        entity.setWaistCm(request.waistCm());
        entity.setShouldersCm(request.shouldersCm());
        entity.setSleeveCm(request.sleeveCm());
        entity.setTrouserLengthCm(request.trouserLengthCm());
        entity.setNeckCm(request.neckCm());
        entity.setHipCm(request.hipCm());
        entity.setNotes(request.notes());
        return entity;
    }

    public OrderResponse toResponse(OrderEntity entity) {
        List<OrderItemResponse> items = entity.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getId(),
                item.getSuitModel() == null ? null : item.getSuitModel().getId(),
                item.getSuitNameSnapshot(),
                item.getCategorySnapshot(),
                item.getFabricSnapshot(),
                item.getColorSnapshot(),
                item.getDesignSnapshot(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal()
            ))
            .toList();

        MeasurementEntity measurement = entity.getMeasurement();
        MeasurementResponse measurementResponse = measurement == null ? null : new MeasurementResponse(
            measurement.getId(),
            measurement.getHeightCm(),
            measurement.getChestCm(),
            measurement.getWaistCm(),
            measurement.getShouldersCm(),
            measurement.getSleeveCm(),
            measurement.getTrouserLengthCm(),
            measurement.getNeckCm(),
            measurement.getHipCm(),
            measurement.getNotes()
        );

        List<OrderStatusHistoryResponse> history = entity.getStatusHistory().stream()
            .map(item -> new OrderStatusHistoryResponse(
                item.getId(),
                item.getOldStatus(),
                item.getNewStatus(),
                item.getChangedByUser() == null ? null : item.getChangedByUser().getId(),
                item.getNote(),
                item.getCreatedAt()
            ))
            .toList();

        return new OrderResponse(
            entity.getId(),
            entity.getOrderNumber(),
            entity.getCustomerUser() == null ? null : entity.getCustomerUser().getId(),
            entity.getCustomerName(),
            entity.getCustomerPhone(),
            entity.getCustomerEmail(),
            entity.getStatus(),
            entity.getPaymentStatus(),
            entity.getFulfillmentType(),
            entity.getDeliveryAddress(),
            entity.getPickupLocation(),
            entity.getNotes(),
            entity.getSubtotalAmount(),
            entity.getDeliveryFee(),
            entity.getTotalAmount(),
            entity.getCurrency(),
            items,
            measurementResponse,
            entity.getPayments().stream().map(paymentMapper::toResponse).toList(),
            history,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
