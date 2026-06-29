package com.suitup.backend.order;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suitup.backend.common.GlobalExceptionHandler;
import com.suitup.backend.common.InvalidStateException;
import com.suitup.backend.order.dto.CreateOrderItemRequest;
import com.suitup.backend.order.dto.CreateOrderRequest;
import com.suitup.backend.order.dto.MeasurementRequest;
import com.suitup.backend.order.dto.OrderResponse;
import com.suitup.backend.order.dto.UpdateOrderStatusRequest;
import com.suitup.backend.payment.PaymentStatus;
import com.suitup.backend.security.CustomUserDetails;
import com.suitup.backend.security.CustomUserDetailsService;
import com.suitup.backend.security.JwtAuthenticationFilter;
import com.suitup.backend.security.JwtService;
import com.suitup.backend.security.RestAccessDeniedHandler;
import com.suitup.backend.security.RestAuthenticationEntryPoint;
import com.suitup.backend.security.SecurityConfig;
import com.suitup.backend.user.RoleCode;
import com.suitup.backend.user.RoleEntity;
import com.suitup.backend.user.UserEntity;
import java.math.BigDecimal;
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

@WebMvcTest({OrderController.class, AdminOrderController.class})
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
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private OrderService orderService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    void createRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void customerCreatesOrderWithAuthenticatedIdentityAndHeaderIdempotencyKey() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);
        CreateOrderRequest request = validRequest(UUID.randomUUID());
        OrderResponse created = response(UUID.randomUUID(), customer.getId());
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/orders")
                .with(user(customer))
                .header("Idempotency-Key", "checkout-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/orders/" + created.id()))
            .andExpect(jsonPath("$.status").value("RECEIVED"));

        ArgumentCaptor<CreateOrderRequest> captor = ArgumentCaptor.forClass(CreateOrderRequest.class);
        verify(orderService).createOrder(captor.capture());
        assertThat(captor.getValue().customerUserId()).isEqualTo(customer.getId());
        assertThat(captor.getValue().idempotencyKey()).isEqualTo("checkout-123");
    }

    @Test
    void createRejectsEmptyItems() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);
        CreateOrderRequest invalid = new CreateOrderRequest(
            null,
            "Joao Cliente",
            "+258840000001",
            "joao@example.com",
            FulfillmentType.PICKUP,
            null,
            "Loja Maputo",
            null,
            null,
            List.of(),
            measurements()
        );

        mockMvc.perform(post("/api/orders")
                .with(user(customer))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.fieldErrors.items").exists());

        verifyNoInteractions(orderService);
    }

    @Test
    void myOrdersRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/orders/my"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void ownerCanReadOrder() throws Exception {
        CustomUserDetails customer = userDetails(RoleCode.CUSTOMER);
        UUID orderId = UUID.randomUUID();
        when(orderService.getAccessibleById(orderId, customer.getId(), false))
            .thenReturn(response(orderId, customer.getId()));

        mockMvc.perform(get("/api/orders/{id}", orderId).with(user(customer)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    void adminCanListOrders() throws Exception {
        CustomUserDetails admin = userDetails(RoleCode.ADMIN);
        when(orderService.listAllForAdmin()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/orders").with(user(admin)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void customerCannotAccessAdminOrders() throws Exception {
        mockMvc.perform(get("/api/admin/orders").with(user(userDetails(RoleCode.CUSTOMER))))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void adminCanUpdateOrderStatus() throws Exception {
        CustomUserDetails admin = userDetails(RoleCode.ADMIN);
        UUID orderId = UUID.randomUUID();
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(
            OrderStatus.IN_ANALYSIS,
            "Dados confirmados"
        );
        when(orderService.updateStatus(
            eq(orderId), eq(OrderStatus.IN_ANALYSIS), eq(admin.getId()), eq("Dados confirmados")
        )).thenReturn(response(orderId, UUID.randomUUID()));

        mockMvc.perform(patch("/api/admin/orders/{id}/status", orderId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        verify(orderService).updateStatus(
            orderId, OrderStatus.IN_ANALYSIS, admin.getId(), "Dados confirmados"
        );
    }

    @Test
    void invalidStatusTransitionReturnsStructuredBadRequest() throws Exception {
        CustomUserDetails admin = userDetails(RoleCode.ADMIN);
        UUID orderId = UUID.randomUUID();
        when(orderService.updateStatus(eq(orderId), eq(OrderStatus.DELIVERED), eq(admin.getId()), any()))
            .thenThrow(new InvalidStateException("Transicao nao permitida"));

        mockMvc.perform(patch("/api/admin/orders/{id}/status", orderId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"DELIVERED\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("INVALID_STATE"));
    }

    private CreateOrderRequest validRequest(UUID clientSuppliedUserId) {
        return new CreateOrderRequest(
            clientSuppliedUserId,
            "Joao Cliente",
            "+258840000001",
            "joao@example.com",
            FulfillmentType.PICKUP,
            null,
            "Loja Maputo",
            null,
            "body-key",
            List.of(new CreateOrderItemRequest(
                UUID.randomUUID(),
                "La Premium",
                "Preto",
                objectMapper.createObjectNode().put("lapel", "classica"),
                1
            )),
            measurements()
        );
    }

    private MeasurementRequest measurements() {
        return new MeasurementRequest(
            new BigDecimal("178"),
            new BigDecimal("102"),
            new BigDecimal("88"),
            new BigDecimal("46"),
            new BigDecimal("64"),
            new BigDecimal("104"),
            null,
            null,
            null
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

    private OrderResponse response(UUID id, UUID customerId) {
        return new OrderResponse(
            id,
            "SU-2026-ABC12345",
            customerId,
            "Joao Cliente",
            "+258840000001",
            "joao@example.com",
            OrderStatus.RECEIVED,
            PaymentStatus.PENDING,
            FulfillmentType.PICKUP,
            null,
            "Loja Maputo",
            null,
            new BigDecimal("8500.00"),
            BigDecimal.ZERO,
            new BigDecimal("8500.00"),
            "MZN",
            List.of(),
            null,
            List.of(),
            List.of(),
            null,
            null
        );
    }
}
