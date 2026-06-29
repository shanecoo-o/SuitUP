package com.suitup.backend.payment;

import com.suitup.backend.common.BadRequestException;
import com.suitup.backend.common.DuplicateResourceException;
import com.suitup.backend.common.InvalidStateException;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.order.OrderEntity;
import com.suitup.backend.order.OrderRepository;
import com.suitup.backend.payment.dto.ConfirmPaymentRequest;
import com.suitup.backend.payment.dto.PaymentResponse;
import com.suitup.backend.payment.dto.PaymentStatusHistoryResponse;
import com.suitup.backend.payment.dto.RejectPaymentRequest;
import com.suitup.backend.payment.dto.SubmitPaymentRequest;
import com.suitup.backend.upload.UploadedFileEntity;
import com.suitup.backend.upload.UploadedFilePurpose;
import com.suitup.backend.upload.UploadedFileRepository;
import com.suitup.backend.user.UserEntity;
import com.suitup.backend.user.UserRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(
        PaymentRepository paymentRepository,
        OrderRepository orderRepository,
        UserRepository userRepository,
        UploadedFileRepository uploadedFileRepository,
        PaymentMapper paymentMapper
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.uploadedFileRepository = uploadedFileRepository;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public PaymentResponse submitPayment(
        UUID orderId,
        SubmitPaymentRequest request,
        UUID currentUserId,
        boolean admin
    ) {
        if (request == null || request.method() == null) {
            throw new BadRequestException("O método de pagamento é obrigatório");
        }
        OrderEntity order = requireAccessibleOrder(orderId, currentUserId, admin);
        if (request.amount() == null || request.amount().compareTo(order.getTotalAmount()) != 0) {
            throw new BadRequestException("O valor do pagamento deve corresponder ao total do pedido");
        }
        boolean hasOpenPayment = paymentRepository.findByOrderId(orderId).stream()
            .anyMatch(payment -> payment.getStatus() != PaymentStatus.REJECTED);
        if (hasOpenPayment) {
            throw new DuplicateResourceException("O pedido já possui um pagamento pendente ou confirmado");
        }

        String reference = trimToNull(request.transactionReference());
        if (request.method() != PaymentMethod.CASH_ON_PICKUP && reference == null) {
            throw new BadRequestException("A referencia da transaccao e obrigatoria para este metodo");
        }
        if (reference != null
            && paymentRepository.existsByMethodAndTransactionReference(request.method(), reference)) {
            throw new DuplicateResourceException("A referência da transacção já foi utilizada");
        }

        UploadedFileEntity proof = resolvePaymentProof(request.proofFileId(), currentUserId, admin);
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setMethod(request.method());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency(order.getCurrency());
        payment.setTransactionReference(reference);
        payment.setProofFile(proof);
        payment.setSubmittedAt(OffsetDateTime.now(ZoneOffset.UTC));
        payment.addStatusHistory(new PaymentStatusHistoryEntity(
            null,
            PaymentStatus.PENDING,
            null,
            trimToNull(request.note()) == null
                ? "Pagamento submetido para validação"
                : request.note().trim()
        ));

        order.setPaymentStatus(PaymentStatus.PENDING);
        orderRepository.save(order);
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse confirmPayment(UUID paymentId, ConfirmPaymentRequest request) {
        if (request == null) {
            throw new BadRequestException("Os dados de confirmação são obrigatórios");
        }
        PaymentEntity payment = requirePayment(paymentId);
        requirePending(payment);
        UserEntity reviewer = resolveReviewer(request.reviewedByUserId());
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setConfirmedAt(now);
        payment.setRejectedAt(null);
        payment.setReviewedByUser(reviewer);
        payment.setRejectionReason(null);
        payment.addStatusHistory(new PaymentStatusHistoryEntity(
            PaymentStatus.PENDING,
            PaymentStatus.CONFIRMED,
            reviewer,
            trimToNull(request.note())
        ));
        payment.getOrder().setPaymentStatus(PaymentStatus.CONFIRMED);
        orderRepository.save(payment.getOrder());
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse rejectPayment(UUID paymentId, RejectPaymentRequest request) {
        if (request == null || request.rejectionReason() == null || request.rejectionReason().isBlank()) {
            throw new BadRequestException("O motivo da rejeição é obrigatório");
        }
        PaymentEntity payment = requirePayment(paymentId);
        requirePending(payment);
        UserEntity reviewer = resolveReviewer(request.reviewedByUserId());

        payment.setStatus(PaymentStatus.REJECTED);
        payment.setRejectedAt(OffsetDateTime.now(ZoneOffset.UTC));
        payment.setConfirmedAt(null);
        payment.setReviewedByUser(reviewer);
        payment.setRejectionReason(request.rejectionReason().trim());
        payment.addStatusHistory(new PaymentStatusHistoryEntity(
            PaymentStatus.PENDING,
            PaymentStatus.REJECTED,
            reviewer,
            trimToNull(request.note()) == null
                ? request.rejectionReason().trim()
                : request.note().trim()
        ));
        payment.getOrder().setPaymentStatus(PaymentStatus.REJECTED);
        orderRepository.save(payment.getOrder());
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listPending() {
        return paymentRepository.findByStatusOrderByCreatedAtAsc(PaymentStatus.PENDING).stream()
            .map(paymentMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listAllForAdmin() {
        return paymentRepository.findAll().stream().map(paymentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByIdForAdmin(UUID paymentId) {
        return paymentMapper.toResponse(requirePayment(paymentId));
    }

    @Transactional(readOnly = true)
    public List<PaymentStatusHistoryResponse> getTimelineForAdmin(UUID paymentId) {
        return paymentMapper.toResponse(requirePayment(paymentId)).statusHistory();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listForOrder(UUID orderId, UUID currentUserId, boolean admin) {
        requireAccessibleOrder(orderId, currentUserId, admin);
        return paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
            .map(paymentMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getLatestForOrder(UUID orderId, UUID currentUserId, boolean admin) {
        requireAccessibleOrder(orderId, currentUserId, admin);
        return paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
            .findFirst()
            .map(paymentMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado para o pedido: " + orderId));
    }

    @Transactional(readOnly = true)
    public List<PaymentStatusHistoryResponse> getLatestTimelineForOrder(
        UUID orderId,
        UUID currentUserId,
        boolean admin
    ) {
        return getLatestForOrder(orderId, currentUserId, admin).statusHistory();
    }

    private OrderEntity requireOrder(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + orderId));
    }

    private OrderEntity requireAccessibleOrder(UUID orderId, UUID currentUserId, boolean admin) {
        OrderEntity order = requireOrder(orderId);
        UUID ownerId = order.getCustomerUser() == null ? null : order.getCustomerUser().getId();
        if (!admin && (ownerId == null || !ownerId.equals(currentUserId))) {
            throw new ResourceNotFoundException("Pedido não encontrado: " + orderId);
        }
        return order;
    }

    private PaymentEntity requirePayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + paymentId));
    }

    private void requirePending(PaymentEntity payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidStateException("Apenas pagamentos pendentes podem ser revistos");
        }
    }

    private UserEntity resolveReviewer(UUID reviewerId) {
        return reviewerId == null ? null : userRepository.findById(reviewerId)
            .orElseThrow(() -> new ResourceNotFoundException("Administrador revisor não encontrado"));
    }

    private UploadedFileEntity resolvePaymentProof(UUID proofFileId, UUID currentUserId, boolean admin) {
        if (proofFileId == null) {
            return null;
        }
        UploadedFileEntity proof = uploadedFileRepository.findById(proofFileId)
            .orElseThrow(() -> new ResourceNotFoundException("Comprovativo não encontrado"));
        if (proof.getPurpose() != UploadedFilePurpose.PAYMENT_PROOF) {
            throw new BadRequestException("O ficheiro indicado não é um comprovativo de pagamento");
        }
        UUID ownerId = proof.getOwnerUser() == null ? null : proof.getOwnerUser().getId();
        if (!admin && (ownerId == null || !ownerId.equals(currentUserId))) {
            throw new ResourceNotFoundException("Comprovativo não encontrado");
        }
        return proof;
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
