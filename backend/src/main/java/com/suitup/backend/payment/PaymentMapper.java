package com.suitup.backend.payment;

import com.suitup.backend.payment.dto.PaymentResponse;
import com.suitup.backend.payment.dto.PaymentStatusHistoryResponse;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(PaymentEntity entity) {
        List<PaymentStatusHistoryResponse> history = entity.getStatusHistory().stream()
            .sorted(Comparator.comparing(
                PaymentStatusHistoryEntity::getCreatedAt,
                Comparator.nullsLast(Comparator.naturalOrder())
            ))
            .map(item -> new PaymentStatusHistoryResponse(
                item.getId(),
                item.getOldStatus(),
                item.getNewStatus(),
                item.getChangedByUser() == null ? null : item.getChangedByUser().getId(),
                item.getNote(),
                item.getCreatedAt()
            ))
            .toList();

        return new PaymentResponse(
            entity.getId(),
            entity.getOrder() == null ? null : entity.getOrder().getId(),
            entity.getMethod(),
            entity.getStatus(),
            entity.getAmount(),
            entity.getCurrency(),
            entity.getTransactionReference(),
            entity.getProofFile() == null ? null : entity.getProofFile().getId(),
            entity.getSubmittedAt(),
            entity.getConfirmedAt(),
            entity.getRejectedAt(),
            entity.getReviewedByUser() == null ? null : entity.getReviewedByUser().getId(),
            entity.getRejectionReason(),
            history,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
