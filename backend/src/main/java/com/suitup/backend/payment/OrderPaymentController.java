package com.suitup.backend.payment;

import com.suitup.backend.auth.InvalidCredentialsException;
import com.suitup.backend.order.OrderService;
import com.suitup.backend.payment.dto.PaymentProofMetadataRequest;
import com.suitup.backend.payment.dto.PaymentResponse;
import com.suitup.backend.payment.dto.PaymentStatusHistoryResponse;
import com.suitup.backend.payment.dto.SubmitPaymentRequest;
import com.suitup.backend.security.CustomUserDetails;
import com.suitup.backend.upload.UploadMetadataService;
import com.suitup.backend.upload.UploadedFilePurpose;
import com.suitup.backend.upload.dto.CreateUploadedFileMetadataRequest;
import com.suitup.backend.upload.dto.UploadedFileResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders/{orderId}")
public class OrderPaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final UploadMetadataService uploadMetadataService;

    public OrderPaymentController(
        PaymentService paymentService,
        OrderService orderService,
        UploadMetadataService uploadMetadataService
    ) {
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.uploadMetadataService = uploadMetadataService;
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> submit(
        @PathVariable UUID orderId,
        @Valid @RequestBody SubmitPaymentRequest request,
        Authentication authentication
    ) {
        CustomUserDetails user = currentUser(authentication);
        PaymentResponse response = paymentService.submitPayment(
            orderId,
            request,
            user.getId(),
            isAdmin(user)
        );
        return ResponseEntity.created(URI.create(
            "/api/orders/" + orderId + "/payment"
        )).body(response);
    }

    @GetMapping("/payment")
    public PaymentResponse latest(@PathVariable UUID orderId, Authentication authentication) {
        CustomUserDetails user = currentUser(authentication);
        return paymentService.getLatestForOrder(orderId, user.getId(), isAdmin(user));
    }

    @GetMapping("/payments")
    public List<PaymentResponse> list(@PathVariable UUID orderId, Authentication authentication) {
        CustomUserDetails user = currentUser(authentication);
        return paymentService.listForOrder(orderId, user.getId(), isAdmin(user));
    }

    @GetMapping("/payment-timeline")
    public List<PaymentStatusHistoryResponse> timeline(
        @PathVariable UUID orderId,
        Authentication authentication
    ) {
        CustomUserDetails user = currentUser(authentication);
        return paymentService.getLatestTimelineForOrder(orderId, user.getId(), isAdmin(user));
    }

    @PostMapping("/payment-proof-metadata")
    public ResponseEntity<UploadedFileResponse> createProofMetadata(
        @PathVariable UUID orderId,
        @Valid @RequestBody PaymentProofMetadataRequest request,
        Authentication authentication
    ) {
        CustomUserDetails user = currentUser(authentication);
        orderService.getAccessibleById(orderId, user.getId(), isAdmin(user));
        UUID metadataId = UUID.randomUUID();
        String storedName = request.storedName() == null || request.storedName().isBlank()
            ? metadataId + "-" + safeName(request.originalName())
            : request.storedName().trim();
        String storagePath = request.storagePath() == null || request.storagePath().isBlank()
            ? "metadata://payment-proofs/" + metadataId
            : request.storagePath().trim();
        UploadedFileResponse response = uploadMetadataService.createMetadata(
            new CreateUploadedFileMetadataRequest(
                user.getId(),
                UploadedFilePurpose.PAYMENT_PROOF,
                request.originalName(),
                storedName,
                request.contentType(),
                request.sizeBytes(),
                storagePath,
                request.publicUrl()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new InvalidCredentialsException("Sessao invalida");
        }
        return user;
    }

    private boolean isAdmin(CustomUserDetails user) {
        return user.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private String safeName(String originalName) {
        return originalName.trim().replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
