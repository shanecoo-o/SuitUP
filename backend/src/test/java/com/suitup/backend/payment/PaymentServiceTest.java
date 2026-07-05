package com.suitup.backend.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.suitup.backend.order.OrderEntity;
import com.suitup.backend.order.OrderRepository;
import com.suitup.backend.common.BadRequestException;
import com.suitup.backend.common.DuplicateResourceException;
import com.suitup.backend.common.InvalidStateException;
import com.suitup.backend.payment.dto.ConfirmPaymentRequest;
import com.suitup.backend.payment.dto.PaymentResponse;
import com.suitup.backend.payment.dto.RejectPaymentRequest;
import com.suitup.backend.payment.dto.SubmitPaymentRequest;
import com.suitup.backend.upload.UploadedFileRepository;
import com.suitup.backend.upload.FileStorageService;
import com.suitup.backend.upload.UploadedFileEntity;
import com.suitup.backend.upload.UploadedFilePurpose;
import com.suitup.backend.upload.dto.StoredFileResponse;
import com.suitup.backend.user.UserRepository;
import com.suitup.backend.user.UserEntity;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class PaymentServiceTest {

    private PaymentRepository paymentRepository;
    private OrderRepository orderRepository;
    private PaymentService service;
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        orderRepository = mock(OrderRepository.class);
        fileStorageService = mock(FileStorageService.class);
        service = new PaymentService(
            paymentRepository,
            orderRepository,
            mock(UserRepository.class),
            mock(UploadedFileRepository.class),
            new PaymentMapper(),
            fileStorageService
        );
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void confirmsPendingPaymentAndUpdatesOrder() {
        UUID paymentId = UUID.randomUUID();
        OrderEntity order = order();
        PaymentEntity payment = pendingPayment(order);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        PaymentResponse response = service.confirmPayment(paymentId, new ConfirmPaymentRequest(null, "Validado"));

        assertThat(response.status()).isEqualTo(PaymentStatus.CONFIRMED);
        assertThat(response.confirmedAt()).isNotNull();
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.CONFIRMED);
        assertThat(response.statusHistory()).singleElement()
            .extracting(history -> history.newStatus())
            .isEqualTo(PaymentStatus.CONFIRMED);
    }

    @Test
    void rejectsPendingPaymentAndStoresReason() {
        UUID paymentId = UUID.randomUUID();
        OrderEntity order = order();
        PaymentEntity payment = pendingPayment(order);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        PaymentResponse response = service.rejectPayment(
            paymentId,
            new RejectPaymentRequest(null, "Comprovativo ilegível", null)
        );

        assertThat(response.status()).isEqualTo(PaymentStatus.REJECTED);
        assertThat(response.rejectionReason()).isEqualTo("Comprovativo ilegível");
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.REJECTED);
    }

    @Test
    void submitsOwnPaymentUsingOrderTotalAndPendingStatus() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderEntity order = order();
        order.setCustomerUser(user(customerId));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(orderId)).thenReturn(List.of());

        PaymentResponse response = service.submitPayment(
            orderId,
            new SubmitPaymentRequest(
                PaymentMethod.MPESA,
                new BigDecimal("8500.00"),
                "MPESA-123",
                null,
                "Pagamento mobile"
            ),
            customerId,
            false
        );

        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.amount()).isEqualByComparingTo("8500.00");
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.statusHistory()).singleElement()
            .extracting(history -> history.note())
            .isEqualTo("Pagamento mobile");
    }

    @Test
    void blocksPaymentForAnotherCustomersOrder() {
        UUID orderId = UUID.randomUUID();
        OrderEntity order = order();
        order.setCustomerUser(user(UUID.randomUUID()));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.submitPayment(
            orderId,
            new SubmitPaymentRequest(
                PaymentMethod.MPESA,
                new BigDecimal("8500.00"),
                "MPESA-OTHER",
                null,
                null
            ),
            UUID.randomUUID(),
            false
        )).isInstanceOf(com.suitup.backend.common.ResourceNotFoundException.class);
    }

    @Test
    void rejectsDuplicateTransactionReference() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderEntity order = order();
        order.setCustomerUser(user(customerId));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(orderId)).thenReturn(List.of());
        when(paymentRepository.existsByMethodAndTransactionReference(PaymentMethod.MPESA, "DUP-123"))
            .thenReturn(true);

        assertThatThrownBy(() -> service.submitPayment(
            orderId,
            new SubmitPaymentRequest(
                PaymentMethod.MPESA,
                new BigDecimal("8500.00"),
                "DUP-123",
                null,
                null
            ),
            customerId,
            false
        )).isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void rejectsAmountMismatchAndMissingElectronicReference() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderEntity order = order();
        order.setCustomerUser(user(customerId));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(orderId)).thenReturn(List.of());

        assertThatThrownBy(() -> service.submitPayment(
            orderId,
            new SubmitPaymentRequest(PaymentMethod.MPESA, new BigDecimal("1.00"), "REF", null, null),
            customerId,
            false
        )).isInstanceOf(BadRequestException.class).hasMessageContaining("total");

        assertThatThrownBy(() -> service.submitPayment(
            orderId,
            new SubmitPaymentRequest(PaymentMethod.MPESA, new BigDecimal("8500.00"), null, null, null),
            customerId,
            false
        )).isInstanceOf(BadRequestException.class).hasMessageContaining("referencia");
    }

    @Test
    void cannotReviewNonPendingPayment() {
        UUID paymentId = UUID.randomUUID();
        PaymentEntity payment = pendingPayment(order());
        payment.setStatus(PaymentStatus.CONFIRMED);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> service.confirmPayment(
            paymentId,
            new ConfirmPaymentRequest(null, null)
        )).isInstanceOf(InvalidStateException.class);
        assertThatThrownBy(() -> service.rejectPayment(
            paymentId,
            new RejectPaymentRequest(null, "Duplicado", null)
        )).isInstanceOf(InvalidStateException.class);
    }

    @Test
    void mapsPaymentHistoryInChronologicalOrder() {
        PaymentEntity payment = pendingPayment(order());
        PaymentStatusHistoryEntity confirmed = new PaymentStatusHistoryEntity(
            PaymentStatus.PENDING, PaymentStatus.CONFIRMED, null, "Segundo"
        );
        confirmed.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        PaymentStatusHistoryEntity submitted = new PaymentStatusHistoryEntity(
            null, PaymentStatus.PENDING, null, "Primeiro"
        );
        submitted.setCreatedAt(confirmed.getCreatedAt().minusMinutes(1));
        payment.addStatusHistory(confirmed);
        payment.addStatusHistory(submitted);

        assertThat(new PaymentMapper().toResponse(payment).statusHistory())
            .extracting(history -> history.note())
            .containsExactly("Primeiro", "Segundo");
    }

    @Test
    void customerUploadsProofForOwnPendingPayment() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderEntity order = order();
        order.setCustomerUser(user(customerId));
        PaymentEntity payment = pendingPayment(order);
        MockMultipartFile file = new MockMultipartFile(
            "file", "proof.pdf", "application/pdf", "%PDF-test".getBytes()
        );
        UploadedFileEntity stored = new UploadedFileEntity();
        stored.setId(UUID.randomUUID());
        stored.setPurpose(UploadedFilePurpose.PAYMENT_PROOF);
        stored.setOriginalName("proof.pdf");
        stored.setContentType("application/pdf");
        stored.setSizeBytes(file.getSize());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId)).thenReturn(List.of(payment));
        when(fileStorageService.store(file, UploadedFilePurpose.PAYMENT_PROOF, customerId))
            .thenReturn(stored);

        StoredFileResponse response = service.uploadProof(orderId, file, customerId, false);

        assertThat(payment.getProofFile()).isSameAs(stored);
        assertThat(response.fileId()).isEqualTo(stored.getId());
        assertThat(response.url()).isEqualTo("/api/files/" + stored.getId());
        verify(paymentRepository).save(payment);
    }

    @Test
    void customerCannotUploadProofForForeignOrder() {
        UUID orderId = UUID.randomUUID();
        OrderEntity order = order();
        order.setCustomerUser(user(UUID.randomUUID()));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.uploadProof(
            orderId,
            new MockMultipartFile("file", "proof.pdf", "application/pdf", "%PDF-test".getBytes()),
            UUID.randomUUID(),
            false
        )).isInstanceOf(com.suitup.backend.common.ResourceNotFoundException.class);

        verifyNoInteractions(fileStorageService);
    }

    private OrderEntity order() {
        OrderEntity order = new OrderEntity();
        order.setTotalAmount(new BigDecimal("8500.00"));
        order.setCurrency("MZN");
        order.setPaymentStatus(PaymentStatus.PENDING);
        return order;
    }

    private PaymentEntity pendingPayment(OrderEntity order) {
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setMethod(PaymentMethod.MPESA);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("MZN");
        payment.setSubmittedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return payment;
    }

    private UserEntity user(UUID id) {
        UserEntity user = new UserEntity("Cliente", "cliente@example.com", null, "hash");
        user.setId(id);
        return user;
    }
}
