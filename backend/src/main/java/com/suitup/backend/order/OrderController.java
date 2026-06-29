package com.suitup.backend.order;

import com.suitup.backend.auth.InvalidCredentialsException;
import com.suitup.backend.common.BadRequestException;
import com.suitup.backend.order.dto.CreateOrderRequest;
import com.suitup.backend.order.dto.OrderResponse;
import com.suitup.backend.order.dto.OrderStatusHistoryResponse;
import com.suitup.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
        @Valid @RequestBody CreateOrderRequest request,
        @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
        Authentication authentication
    ) {
        CustomUserDetails user = currentUser(authentication);
        String effectiveKey = idempotencyKey == null || idempotencyKey.isBlank()
            ? request.idempotencyKey()
            : idempotencyKey.trim();
        if (effectiveKey != null && effectiveKey.length() > 150) {
            throw new BadRequestException("A chave de idempotencia deve ter no maximo 150 caracteres");
        }
        UUID customerUserId = isAdmin(user) ? request.customerUserId() : user.getId();
        CreateOrderRequest securedRequest = new CreateOrderRequest(
            customerUserId,
            request.customerName(),
            request.customerPhone(),
            request.customerEmail(),
            request.fulfillmentType(),
            request.deliveryAddress(),
            request.pickupLocation(),
            request.notes(),
            effectiveKey,
            request.items(),
            request.measurement()
        );
        OrderResponse response = orderService.createOrder(securedRequest);
        return ResponseEntity.created(URI.create("/api/orders/" + response.id())).body(response);
    }

    @GetMapping("/my")
    public List<OrderResponse> myOrders(Authentication authentication) {
        CustomUserDetails user = currentUser(authentication);
        return orderService.listCustomerOrders(user.getId(), user.getPhone());
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable UUID id, Authentication authentication) {
        CustomUserDetails user = currentUser(authentication);
        return orderService.getAccessibleById(id, user.getId(), isAdmin(user));
    }

    @GetMapping("/{id}/timeline")
    public List<OrderStatusHistoryResponse> timeline(
        @PathVariable UUID id,
        Authentication authentication
    ) {
        CustomUserDetails user = currentUser(authentication);
        return orderService.getAccessibleTimeline(id, user.getId(), isAdmin(user));
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
}
