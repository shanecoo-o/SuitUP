package com.suitup.backend.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suitup.backend.catalog.CatalogService;
import com.suitup.backend.catalog.SuitModelEntity;
import com.suitup.backend.common.IdempotencyKeyRepository;
import com.suitup.backend.common.InvalidStateException;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.order.dto.CreateOrderItemRequest;
import com.suitup.backend.order.dto.CreateOrderRequest;
import com.suitup.backend.order.dto.MeasurementRequest;
import com.suitup.backend.order.dto.OrderResponse;
import com.suitup.backend.payment.PaymentMapper;
import com.suitup.backend.payment.PaymentStatus;
import com.suitup.backend.user.UserRepository;
import com.suitup.backend.user.UserEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private CatalogService catalogService;
    private OrderService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        catalogService = mock(CatalogService.class);
        objectMapper = new ObjectMapper();
        service = new OrderService(
            orderRepository,
            mock(UserRepository.class),
            catalogService,
            mock(IdempotencyKeyRepository.class),
            new OrderMapper(new PaymentMapper()),
            objectMapper
        );
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void recalculatesPricesAndCreatesSnapshots() {
        UUID modelId = UUID.randomUUID();
        SuitModelEntity model = new SuitModelEntity();
        model.setName("Fato Azul Executivo");
        model.setCategory("Executivo");
        model.setFabricType("Lã Premium");
        model.setColor("Azul Marinho");
        model.setPrice(new BigDecimal("9500.00"));
        model.setActive(true);
        when(catalogService.requireActiveEntity(modelId)).thenReturn(model);

        CreateOrderRequest request = new CreateOrderRequest(
            null,
            "João da Silva",
            "+258841234567",
            "joao@example.com",
            FulfillmentType.DELIVERY,
            "Av. Julius Nyerere, Maputo",
            null,
            null,
            null,
            List.of(new CreateOrderItemRequest(
                modelId,
                "Linho Premium",
                "Azul Noite",
                objectMapper.createObjectNode().put("lapel", "Entalhada"),
                2
            )),
            measurements()
        );

        OrderResponse response = service.createOrder(request);

        assertThat(response.status()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.subtotalAmount()).isEqualByComparingTo("19000.00");
        assertThat(response.deliveryFee()).isEqualByComparingTo("150.00");
        assertThat(response.totalAmount()).isEqualByComparingTo("19150.00");
        assertThat(response.items()).singleElement().satisfies(item -> {
            assertThat(item.fabric()).isEqualTo("Linho Premium");
            assertThat(item.color()).isEqualTo("Azul Noite");
            assertThat(item.lineTotal()).isEqualByComparingTo("19000.00");
        });
        assertThat(response.statusHistory()).singleElement()
            .extracting(history -> history.newStatus())
            .isEqualTo(OrderStatus.RECEIVED);
    }

    @Test
    void blocksProductionBeforePaymentConfirmation() {
        UUID orderId = UUID.randomUUID();
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.MEASUREMENTS_CONFIRMED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.updateStatus(orderId, OrderStatus.IN_PRODUCTION, null, null))
            .isInstanceOf(InvalidStateException.class)
            .hasMessageContaining("pagamento");
    }

    @Test
    void allowsOwnerAndHidesOrderFromAnotherCustomer() {
        UUID orderId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UserEntity owner = new UserEntity("Cliente", "cliente@example.com", null, "hash");
        owner.setId(ownerId);
        OrderEntity order = new OrderEntity();
        order.setCustomerUser(owner);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThat(service.getAccessibleById(orderId, ownerId, false).customerUserId())
            .isEqualTo(ownerId);
        assertThatThrownBy(() -> service.getAccessibleById(orderId, UUID.randomUUID(), false))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    private MeasurementRequest measurements() {
        return new MeasurementRequest(
            new BigDecimal("178"),
            new BigDecimal("102"),
            new BigDecimal("88"),
            new BigDecimal("46"),
            new BigDecimal("64"),
            new BigDecimal("104"),
            new BigDecimal("40"),
            new BigDecimal("96"),
            null
        );
    }
}
