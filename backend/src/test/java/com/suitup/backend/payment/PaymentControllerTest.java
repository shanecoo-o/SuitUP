package com.suitup.backend.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suitup.backend.common.GlobalExceptionHandler;
import com.suitup.backend.common.InvalidStateException;
import com.suitup.backend.order.OrderService;
import com.suitup.backend.payment.dto.ConfirmPaymentRequest;
import com.suitup.backend.payment.dto.PaymentProofMetadataRequest;
import com.suitup.backend.payment.dto.PaymentResponse;
import com.suitup.backend.payment.dto.RejectPaymentRequest;
import com.suitup.backend.payment.dto.SubmitPaymentRequest;
import com.suitup.backend.security.CustomUserDetails;
import com.suitup.backend.security.CustomUserDetailsService;
import com.suitup.backend.security.JwtAuthenticationFilter;
import com.suitup.backend.security.JwtService;
import com.suitup.backend.security.RestAccessDeniedHandler;
import com.suitup.backend.security.RestAuthenticationEntryPoint;
import com.suitup.backend.security.SecurityConfig;
import com.suitup.backend.upload.UploadMetadataService;
import com.suitup.backend.upload.UploadedFilePurpose;
import com.suitup.backend.upload.dto.CreateUploadedFileMetadataRequest;
import com.suitup.backend.upload.dto.UploadedFileResponse;
import com.suitup.backend.user.RoleCode;
import com.suitup.backend.user.RoleEntity;
import com.suitup.backend.user.UserEntity;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({OrderPaymentController.class, AdminPaymentController.class})
@Import({
    SecurityConfig.class,
    JwtAuthenticationFilter.class,
    RestAuthenticationEntryPoint.class,
    RestAccessDeniedHandler.class,
    GlobalExceptionHandler.class
})
@TestPropertySource(properties = {
    "app.security.jwt.secret=suitup-test-secret-with-at-least-32-bytes",
    "app.security.cors.allowed-origins=http://localhost:3000"
})
class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private PaymentService paymentService;
    @MockitoBean private OrderService orderService;
    @MockitoBean private UploadMetadataService uploadMetadataService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    void submitRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/orders/{id}/payment", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void customerCanSubmitPaymentForOwnedOrder() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);
        UUID orderId = UUID.randomUUID();
        SubmitPaymentRequest request = submitRequest();
        when(paymentService.submitPayment(orderId, request, customer.getId(), false))
            .thenReturn(response(UUID.randomUUID(), orderId, PaymentStatus.PENDING));

        mockMvc.perform(post("/api/orders/{id}/payment", orderId)
                .with(user(customer))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(paymentService).submitPayment(orderId, request, customer.getId(), false);
    }

    @Test
    void submitValidationRejectsInvalidAmountAndMissingMethod() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);

        mockMvc.perform(post("/api/orders/{id}/payment", UUID.randomUUID())
                .with(user(customer))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":-1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.fieldErrors.method").exists())
            .andExpect(jsonPath("$.fieldErrors.amount").exists());

        verifyNoInteractions(paymentService);
    }

    @Test
    void customerCanReadLatestPaymentUsingOwnerAwareService() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);
        UUID orderId = UUID.randomUUID();
        when(paymentService.getLatestForOrder(orderId, customer.getId(), false))
            .thenReturn(response(UUID.randomUUID(), orderId, PaymentStatus.PENDING));

        mockMvc.perform(get("/api/orders/{id}/payment", orderId).with(user(customer)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(orderId.toString()));
    }

    @Test
    void adminCanListPendingPayments() throws Exception {
        when(paymentService.listPending()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/payments/pending").with(user(userDetails(RoleCode.ADMIN))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void customerCannotAccessAdminPayments() throws Exception {
        mockMvc.perform(get("/api/admin/payments").with(user(userDetails(RoleCode.CUSTOMER))))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void adminCanConfirmPendingPaymentWithAuthenticatedReviewer() throws Exception {
        CustomUserDetails admin = userDetails(RoleCode.ADMIN);
        UUID paymentId = UUID.randomUUID();
        ConfirmPaymentRequest clientRequest = new ConfirmPaymentRequest(UUID.randomUUID(), "Validado");
        when(paymentService.confirmPayment(eq(paymentId), any(ConfirmPaymentRequest.class)))
            .thenReturn(response(paymentId, UUID.randomUUID(), PaymentStatus.CONFIRMED));

        mockMvc.perform(patch("/api/admin/payments/{id}/confirm", paymentId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));

        ArgumentCaptor<ConfirmPaymentRequest> captor = ArgumentCaptor.forClass(ConfirmPaymentRequest.class);
        verify(paymentService).confirmPayment(eq(paymentId), captor.capture());
        assertThat(captor.getValue().reviewedByUserId()).isEqualTo(admin.getId());
    }

    @Test
    void adminCanRejectPendingPaymentWithReason() throws Exception {
        CustomUserDetails admin = userDetails(RoleCode.ADMIN);
        UUID paymentId = UUID.randomUUID();
        RejectPaymentRequest clientRequest = new RejectPaymentRequest(
            UUID.randomUUID(), "Comprovativo ilegivel", "Solicitar novo comprovativo"
        );
        when(paymentService.rejectPayment(eq(paymentId), any(RejectPaymentRequest.class)))
            .thenReturn(response(paymentId, UUID.randomUUID(), PaymentStatus.REJECTED));

        mockMvc.perform(patch("/api/admin/payments/{id}/reject", paymentId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("REJECTED"));

        ArgumentCaptor<RejectPaymentRequest> captor = ArgumentCaptor.forClass(RejectPaymentRequest.class);
        verify(paymentService).rejectPayment(eq(paymentId), captor.capture());
        assertThat(captor.getValue().reviewedByUserId()).isEqualTo(admin.getId());
        assertThat(captor.getValue().rejectionReason()).isEqualTo("Comprovativo ilegivel");
    }

    @Test
    void invalidReviewStateReturnsStructuredBadRequest() throws Exception {
        CustomUserDetails admin = userDetails(RoleCode.ADMIN);
        UUID paymentId = UUID.randomUUID();
        when(paymentService.confirmPayment(eq(paymentId), any(ConfirmPaymentRequest.class)))
            .thenThrow(new InvalidStateException("Pagamento ja revisto"));

        mockMvc.perform(patch("/api/admin/payments/{id}/confirm", paymentId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("INVALID_STATE"));
    }

    @Test
    void customerCanCreateSafeProofMetadataForOwnedOrder() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);
        UUID orderId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        PaymentProofMetadataRequest request = new PaymentProofMetadataRequest(
            "comprovativo.png", null, "image/png", 2048, null, null
        );
        when(uploadMetadataService.createMetadata(any(CreateUploadedFileMetadataRequest.class)))
            .thenReturn(new UploadedFileResponse(
                fileId,
                customer.getId(),
                UploadedFilePurpose.PAYMENT_PROOF,
                request.originalName(),
                "stored.png",
                request.contentType(),
                request.sizeBytes(),
                "metadata://payment-proofs/test",
                null,
                OffsetDateTime.now(ZoneOffset.UTC)
            ));

        mockMvc.perform(post("/api/orders/{id}/payment-proof-metadata", orderId)
                .with(user(customer))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(fileId.toString()))
            .andExpect(jsonPath("$.purpose").value("PAYMENT_PROOF"));

        verify(orderService).getAccessibleById(orderId, customer.getId(), false);
        ArgumentCaptor<CreateUploadedFileMetadataRequest> captor =
            ArgumentCaptor.forClass(CreateUploadedFileMetadataRequest.class);
        verify(uploadMetadataService).createMetadata(captor.capture());
        assertThat(captor.getValue().ownerUserId()).isEqualTo(customer.getId());
        assertThat(captor.getValue().purpose()).isEqualTo(UploadedFilePurpose.PAYMENT_PROOF);
    }

    private SubmitPaymentRequest submitRequest() {
        return new SubmitPaymentRequest(
            PaymentMethod.MPESA,
            new BigDecimal("8500.00"),
            "MPESA-123",
            null,
            "Pagamento submetido"
        );
    }

    private CustomUserDetails userDetails(RoleCode roleCode) {
        RoleEntity role = new RoleEntity(roleCode, roleCode.name());
        role.setId(UUID.randomUUID());
        UserEntity user = new UserEntity("Utilizador", roleCode.name().toLowerCase() + "@example.com", null, "hash");
        user.setId(UUID.randomUUID());
        user.addRole(role);
        return CustomUserDetails.from(user);
    }

    private PaymentResponse response(UUID id, UUID orderId, PaymentStatus status) {
        return new PaymentResponse(
            id,
            orderId,
            PaymentMethod.MPESA,
            status,
            new BigDecimal("8500.00"),
            "MZN",
            "MPESA-123",
            null,
            OffsetDateTime.now(ZoneOffset.UTC),
            status == PaymentStatus.CONFIRMED ? OffsetDateTime.now(ZoneOffset.UTC) : null,
            status == PaymentStatus.REJECTED ? OffsetDateTime.now(ZoneOffset.UTC) : null,
            null,
            null,
            List.of(),
            null,
            null
        );
    }
}
