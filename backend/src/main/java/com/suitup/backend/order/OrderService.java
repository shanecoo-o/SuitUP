package com.suitup.backend.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suitup.backend.catalog.CatalogService;
import com.suitup.backend.catalog.SuitModelEntity;
import com.suitup.backend.common.BadRequestException;
import com.suitup.backend.common.DuplicateResourceException;
import com.suitup.backend.common.IdempotencyKeyEntity;
import com.suitup.backend.common.IdempotencyKeyRepository;
import com.suitup.backend.common.InvalidStateException;
import com.suitup.backend.common.MoneyValidator;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.measurement.MeasurementEntity;
import com.suitup.backend.order.dto.CreateOrderItemRequest;
import com.suitup.backend.order.dto.CreateOrderRequest;
import com.suitup.backend.order.dto.MeasurementRequest;
import com.suitup.backend.order.dto.OrderResponse;
import com.suitup.backend.order.dto.OrderStatusHistoryResponse;
import com.suitup.backend.payment.PaymentStatus;
import com.suitup.backend.user.UserEntity;
import com.suitup.backend.user.UserRepository;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final BigDecimal DELIVERY_FEE_MZN = new BigDecimal("150.00");
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = transitions();

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CatalogService catalogService;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;

    public OrderService(
        OrderRepository orderRepository,
        UserRepository userRepository,
        CatalogService catalogService,
        IdempotencyKeyRepository idempotencyKeyRepository,
        OrderMapper orderMapper,
        ObjectMapper objectMapper
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.catalogService = catalogService;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.orderMapper = orderMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Os dados do pedido são obrigatórios");
        }
        validateFulfillment(request);
        validateMeasurements(request.measurement());
        if (request.items() == null || request.items().isEmpty()) {
            throw new BadRequestException("O pedido deve conter pelo menos um item");
        }

        String idempotencyKey = trimToNull(request.idempotencyKey());
        String requestHash = idempotencyKey == null ? null : requestHash(request);
        OrderEntity replay = resolveIdempotentReplay(idempotencyKey, requestHash);
        if (replay != null) {
            return orderMapper.toResponse(replay);
        }

        UserEntity customer = request.customerUserId() == null ? null : userRepository
            .findById(request.customerUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        OrderEntity order = new OrderEntity();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerUser(customer);
        order.setCustomerName(request.customerName().trim());
        order.setCustomerPhone(request.customerPhone().trim());
        order.setCustomerEmail(trimToNull(request.customerEmail()));
        order.setStatus(OrderStatus.RECEIVED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setFulfillmentType(request.fulfillmentType());
        order.setDeliveryAddress(request.fulfillmentType() == FulfillmentType.DELIVERY
            ? request.deliveryAddress().trim()
            : null);
        order.setPickupLocation(request.fulfillmentType() == FulfillmentType.PICKUP
            ? request.pickupLocation().trim()
            : null);
        order.setNotes(trimToNull(request.notes()));
        order.setCurrency(MoneyValidator.DEFAULT_CURRENCY);
        order.setIdempotencyKey(idempotencyKey);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CreateOrderItemRequest itemRequest : request.items()) {
            if (itemRequest == null || itemRequest.suitModelId() == null) {
                throw new BadRequestException("Todos os itens devem indicar um modelo de fato");
            }
            if (itemRequest.quantity() <= 0) {
                throw new BadRequestException("A quantidade deve ser maior que zero");
            }
            if (itemRequest.designSnapshot() == null) {
                throw new BadRequestException("A configuração personalizada do fato é obrigatória");
            }
            SuitModelEntity model = catalogService.requireActiveEntity(itemRequest.suitModelId());
            BigDecimal lineTotal = model.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            subtotal = subtotal.add(lineTotal);

            OrderItemEntity item = new OrderItemEntity();
            item.setSuitModel(model);
            item.setSuitNameSnapshot(model.getName());
            item.setCategorySnapshot(model.getCategory());
            item.setFabricSnapshot(defaultIfBlank(itemRequest.fabric(), model.getFabricType()));
            item.setColorSnapshot(defaultIfBlank(itemRequest.color(), model.getColor()));
            item.setDesignSnapshot(itemRequest.designSnapshot());
            item.setUnitPrice(model.getPrice());
            item.setQuantity(itemRequest.quantity());
            item.setLineTotal(lineTotal);
            order.addItem(item);
        }

        BigDecimal deliveryFee = request.fulfillmentType() == FulfillmentType.DELIVERY
            ? DELIVERY_FEE_MZN
            : BigDecimal.ZERO;
        order.setSubtotalAmount(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setTotalAmount(subtotal.add(deliveryFee));
        order.setMeasurement(orderMapper.toMeasurementEntity(request.measurement()));
        order.addStatusHistory(new OrderStatusHistoryEntity(
            null,
            OrderStatus.RECEIVED,
            customer,
            "Pedido recebido"
        ));

        OrderEntity saved = orderRepository.save(order);
        if (idempotencyKey != null) {
            saveIdempotencyKey(idempotencyKey, requestHash, customer, saved.getId());
        }
        return orderMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(UUID id) {
        return orderMapper.toResponse(requireById(id));
    }

    @Transactional(readOnly = true)
    public OrderResponse getAccessibleById(UUID id, UUID currentUserId, boolean admin) {
        return orderMapper.toResponse(requireAccessibleById(id, currentUserId, admin));
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponse> getAccessibleTimeline(
        UUID id,
        UUID currentUserId,
        boolean admin
    ) {
        return orderMapper.toResponse(requireAccessibleById(id, currentUserId, admin)).statusHistory();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listCustomerOrders(UUID userId, String phoneFallback) {
        List<OrderEntity> orders = userId != null
            ? orderRepository.findByCustomerUserIdOrderByCreatedAtDesc(userId)
            : orderRepository.findByCustomerPhoneOrderByCreatedAtDesc(phoneFallback);
        return orders.stream().map(orderMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listAllForAdmin() {
        return orderRepository.findAll().stream().map(orderMapper::toResponse).toList();
    }

    @Transactional
    public OrderResponse updateStatus(UUID orderId, OrderStatus newStatus, UUID changedByUserId, String note) {
        OrderEntity order = requireById(orderId);
        OrderStatus oldStatus = order.getStatus();
        if (!ALLOWED_TRANSITIONS.getOrDefault(oldStatus, Set.of()).contains(newStatus)) {
            throw new InvalidStateException("Transição de " + oldStatus + " para " + newStatus + " não permitida");
        }
        if (newStatus == OrderStatus.IN_PRODUCTION && order.getPaymentStatus() != PaymentStatus.CONFIRMED) {
            throw new InvalidStateException("O pagamento deve estar confirmado antes da produção");
        }

        UserEntity changedBy = changedByUserId == null ? null : userRepository.findById(changedByUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilizador responsável não encontrado"));
        order.setStatus(newStatus);
        order.addStatusHistory(new OrderStatusHistoryEntity(oldStatus, newStatus, changedBy, trimToNull(note)));
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderEntity requireById(UUID id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + id));
    }

    private OrderEntity requireAccessibleById(UUID id, UUID currentUserId, boolean admin) {
        OrderEntity order = requireById(id);
        UUID ownerId = order.getCustomerUser() == null ? null : order.getCustomerUser().getId();
        if (!admin && (ownerId == null || !ownerId.equals(currentUserId))) {
            throw new ResourceNotFoundException("Pedido nÃ£o encontrado: " + id);
        }
        return order;
    }

    private OrderEntity resolveIdempotentReplay(String key, String requestHash) {
        if (key == null) {
            return null;
        }
        return idempotencyKeyRepository.findByKey(key).map(existing -> {
            if (!existing.getRequestHash().equals(requestHash)) {
                throw new DuplicateResourceException("A chave de idempotência já foi usada com outro pedido");
            }
            if (existing.getResourceId() == null) {
                throw new DuplicateResourceException("O pedido com esta chave ainda está em processamento");
            }
            return requireById(existing.getResourceId());
        }).orElse(null);
    }

    private void saveIdempotencyKey(String key, String hash, UserEntity user, UUID orderId) {
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setKey(key);
        entity.setRequestHash(hash);
        entity.setUser(user);
        entity.setResourceType("ORDER");
        entity.setResourceId(orderId);
        entity.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusHours(24));
        idempotencyKeyRepository.save(entity);
    }

    private String requestHash(CreateOrderRequest request) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(request);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(payload));
        } catch (JsonProcessingException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Não foi possível calcular o hash do pedido", exception);
        }
    }

    private void validateFulfillment(CreateOrderRequest request) {
        if (request.fulfillmentType() == null) {
            throw new BadRequestException("O tipo de entrega é obrigatório");
        }
        if (request.fulfillmentType() == FulfillmentType.DELIVERY
            && (request.deliveryAddress() == null || request.deliveryAddress().isBlank())) {
            throw new BadRequestException("O endereço é obrigatório para entrega");
        }
        if (request.fulfillmentType() == FulfillmentType.PICKUP
            && (request.pickupLocation() == null || request.pickupLocation().isBlank())) {
            throw new BadRequestException("O ponto de levantamento é obrigatório");
        }
    }

    private void validateMeasurements(MeasurementRequest request) {
        if (request == null) {
            throw new BadRequestException("As medidas são obrigatórias");
        }
        requirePositive(request.heightCm(), "heightCm");
        requirePositive(request.chestCm(), "chestCm");
        requirePositive(request.waistCm(), "waistCm");
        requirePositive(request.shouldersCm(), "shouldersCm");
        requirePositive(request.sleeveCm(), "sleeveCm");
        requirePositive(request.trouserLengthCm(), "trouserLengthCm");
        if (request.neckCm() != null) requirePositive(request.neckCm(), "neckCm");
        if (request.hipCm() != null) requirePositive(request.hipCm(), "hipCm");
    }

    private void requirePositive(BigDecimal value, String field) {
        if (value == null || value.signum() <= 0) {
            throw new BadRequestException(field + " deve ser maior que zero");
        }
    }

    private String generateOrderNumber() {
        return "SU-" + OffsetDateTime.now(ZoneOffset.UTC).getYear() + "-"
            + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static Map<OrderStatus, Set<OrderStatus>> transitions() {
        Map<OrderStatus, Set<OrderStatus>> transitions = new EnumMap<>(OrderStatus.class);
        transitions.put(OrderStatus.RECEIVED, Set.of(OrderStatus.IN_ANALYSIS, OrderStatus.CANCELLED));
        transitions.put(OrderStatus.IN_ANALYSIS, Set.of(OrderStatus.MEASUREMENTS_CONFIRMED, OrderStatus.CANCELLED));
        transitions.put(OrderStatus.MEASUREMENTS_CONFIRMED, Set.of(OrderStatus.IN_PRODUCTION, OrderStatus.CANCELLED));
        transitions.put(OrderStatus.IN_PRODUCTION, Set.of(OrderStatus.READY_FOR_DELIVERY, OrderStatus.CANCELLED));
        transitions.put(OrderStatus.READY_FOR_DELIVERY, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED));
        transitions.put(OrderStatus.DELIVERED, Set.of());
        transitions.put(OrderStatus.CANCELLED, Set.of());
        return Map.copyOf(transitions);
    }
}
