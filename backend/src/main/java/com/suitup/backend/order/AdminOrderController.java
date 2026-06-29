package com.suitup.backend.order;

import com.suitup.backend.auth.InvalidCredentialsException;
import com.suitup.backend.order.dto.OrderResponse;
import com.suitup.backend.order.dto.OrderStatusHistoryResponse;
import com.suitup.backend.order.dto.UpdateOrderStatusRequest;
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
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> listAll() {
        return orderService.listAllForAdmin();
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable UUID id) {
        return orderService.getById(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateOrderStatusRequest request,
        Authentication authentication
    ) {
        return orderService.updateStatus(id, request.status(), currentUserId(authentication), request.note());
    }

    @GetMapping("/{id}/timeline")
    public List<OrderStatusHistoryResponse> timeline(@PathVariable UUID id) {
        return orderService.getAccessibleTimeline(id, null, true);
    }

    private UUID currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new InvalidCredentialsException("Sessao invalida");
        }
        return user.getId();
    }
}
