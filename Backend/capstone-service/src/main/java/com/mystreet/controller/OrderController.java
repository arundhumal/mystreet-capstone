package com.mystreet.controller;

import com.mystreet.dto.OrderDTO;
import com.mystreet.dto.OrderRequest;
import com.mystreet.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        OrderDTO order = orderService.createOrder(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<OrderDTO>> getMyOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        List<OrderDTO> orders = orderService.getUserOrders(userEmail);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable UUID id, Authentication authentication) {
        String userEmail = authentication.getName();
        OrderDTO order = orderService.getOrderById(id, userEmail);
        return ResponseEntity.ok(order);
    }
}
