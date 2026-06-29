package com.suitup.backend.dashboard;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.suitup.backend.common.GlobalExceptionHandler;
import com.suitup.backend.dashboard.dto.AdminDashboardResponse;
import com.suitup.backend.security.CustomUserDetailsService;
import com.suitup.backend.security.JwtAuthenticationFilter;
import com.suitup.backend.security.JwtService;
import com.suitup.backend.security.RestAccessDeniedHandler;
import com.suitup.backend.security.RestAuthenticationEntryPoint;
import com.suitup.backend.security.SecurityConfig;
import com.suitup.backend.order.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminDashboardController.class)
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
class AdminDashboardControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private DashboardService dashboardService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    void dashboardRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotReadDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminReceivesDashboardMetrics() throws Exception {
        when(dashboardService.getDashboard()).thenReturn(new AdminDashboardResponse(
            10,
            5,
            1,
            2,
            6,
            2,
            new BigDecimal("45000.00"),
            "MZN",
            Map.of(OrderStatus.RECEIVED, 3L, OrderStatus.IN_PRODUCTION, 1L),
            List.of(),
            List.of()
        ));

        mockMvc.perform(get("/api/admin/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalOrders").value(10))
            .andExpect(jsonPath("$.pendingPayments").value(2))
            .andExpect(jsonPath("$.confirmedPayments").value(6))
            .andExpect(jsonPath("$.rejectedPayments").value(2))
            .andExpect(jsonPath("$.confirmedRevenue").value(45000.00))
            .andExpect(jsonPath("$.activeSuitModels").value(5))
            .andExpect(jsonPath("$.inactiveSuitModels").value(1))
            .andExpect(jsonPath("$.ordersByStatus.RECEIVED").value(3));
    }
}
