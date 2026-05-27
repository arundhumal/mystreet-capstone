package com.mystreet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mystreet.dto.OrderDTO;
import com.mystreet.dto.OrderRequest;
import com.mystreet.exception.NotFoundException;
import com.mystreet.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class OrderControllerTest {

    @Autowired private WebApplicationContext wac;
    @MockitoBean private OrderService orderService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private OrderDTO makeOrderDTO(String status) {
        OrderDTO dto = new OrderDTO();
        dto.setId(UUID.randomUUID().toString());
        dto.setShippingAddress("123 Main St");
        dto.setPaymentMode("COD");
        dto.setStatus(status);
        dto.setTotalAmount(new BigDecimal("119.99"));
        dto.setCreatedAt(LocalDateTime.now());
        dto.setItems(List.of());
        return dto;
    }

    private OrderRequest makeOrderRequest() {
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(UUID.randomUUID().toString());
        item.setSize("9");
        item.setQuantity(1);

        OrderRequest req = new OrderRequest();
        req.setShippingAddress("123 Main St");
        req.setPaymentMode("COD");
        req.setItems(List.of(item));
        return req;
    }

    // --- POST /api/orders ---

    @Test
    void createOrder_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeOrderRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void createOrder_authenticated_returns201() throws Exception {
        when(orderService.createOrder(any(OrderRequest.class), eq("user@example.com")))
                .thenReturn(makeOrderDTO("PLACED"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeOrderRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.shippingAddress").value("123 Main St"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void createOrder_missingItems_returns400() throws Exception {
        OrderRequest req = new OrderRequest();
        req.setShippingAddress("123 Main St");
        req.setPaymentMode("COD");
        req.setItems(List.of()); // empty — violates @NotEmpty

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/orders/mine ---

    @Test
    void getMyOrders_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/orders/mine"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getMyOrders_authenticated_returns200WithList() throws Exception {
        when(orderService.getUserOrders("user@example.com"))
                .thenReturn(List.of(makeOrderDTO("PLACED"), makeOrderDTO("SHIPPED")));

        mockMvc.perform(get("/api/orders/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("PLACED"))
                .andExpect(jsonPath("$[1].status").value("SHIPPED"));
    }

    // --- GET /api/orders/{id} ---

    @Test
    void getOrderById_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getOrderById_authenticated_returns200() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrderById(eq(orderId), eq("user@example.com")))
                .thenReturn(makeOrderDTO("DELIVERED"));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getOrderById_nonexistent_returns404() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrderById(eq(orderId), eq("user@example.com")))
                .thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }
}
