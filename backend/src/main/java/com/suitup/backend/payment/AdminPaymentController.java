package com.suitup.backend.payment;

import com.suitup.backend.auth.InvalidCredentialsException;
import com.suitup.backend.payment.dto.ConfirmPaymentRequest;
import com.suitup.backend.payment.dto.PaymentResponse;
import com.suitup.backend.payment.dto.PaymentStatusHistoryResponse;
import com.suitup.backend.payment.dto.RejectPaymentRequest;
import com.suitup.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/payments")
public class AdminPaymentController {

    private final PaymentService paymentService;

    public AdminPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<PaymentResponse> listAll() {
        return paymentService.listAllForAdmin();
    }

    @GetMapping("/pending")
    public List<PaymentResponse> listPending() {
        return paymentService.listPending();
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getById(@PathVariable UUID paymentId) {
        return paymentService.getByIdForAdmin(paymentId);
    }

    @PatchMapping("/{paymentId}/confirm")
    public PaymentResponse confirm(
        @PathVariable UUID paymentId,
        @Valid @RequestBody ConfirmPaymentRequest request,
        Authentication authentication
    ) {
        return paymentService.confirmPayment(
            paymentId,
            new ConfirmPaymentRequest(currentUserId(authentication), request.note())
        );
    }

    @PatchMapping("/{paymentId}/reject")
    public PaymentResponse reject(
        @PathVariable UUID paymentId,
        @Valid @RequestBody RejectPaymentRequest request,
        Authentication authentication
    ) {
        return paymentService.rejectPayment(
            paymentId,
            new RejectPaymentRequest(
                currentUserId(authentication),
                request.rejectionReason(),
                request.note()
            )
        );
    }

    @GetMapping("/{paymentId}/timeline")
    public List<PaymentStatusHistoryResponse> timeline(@PathVariable UUID paymentId) {
        return paymentService.getTimelineForAdmin(paymentId);
    }

    private UUID currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new InvalidCredentialsException("Sessao invalida");
        }
        return user.getId();
    }
}
