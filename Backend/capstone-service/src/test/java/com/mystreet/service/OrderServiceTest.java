package com.mystreet.service;

import com.mystreet.dto.OrderDTO;
import com.mystreet.dto.OrderRequest;
import com.mystreet.exception.BadRequestException;
import com.mystreet.exception.NotFoundException;
import com.mystreet.model.Order;
import com.mystreet.model.Product;
import com.mystreet.model.User;
import com.mystreet.repository.OrderRepository;
import com.mystreet.repository.ProductRepository;
import com.mystreet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private OrderService orderService;

    private User makeUser(String email) {
        User user = new User();
        user.setEmail(email);
        return user;
    }

    private Product makeProduct(String id, String name, BigDecimal price, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);
        p.setStockQty(stock);
        return p;
    }

    private OrderRequest makeOrderRequest(String productId, String size, int qty) {
        OrderRequest req = new OrderRequest();
        req.setShippingAddress("123 Main St");
        req.setPaymentMode("COD");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(productId);
        item.setSize(size);
        item.setQuantity(qty);

        req.setItems(List.of(item));
        return req;
    }

    // --- createOrder ---

    @Test
    void createOrder_validRequest_createsOrderAndDeductsStock() {
        String productId = UUID.randomUUID().toString();
        User user = makeUser("buyer@example.com");
        Product product = makeProduct(productId, "Air Max", new BigDecimal("119.99"), 10);

        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderRequest req = makeOrderRequest(productId, "9", 2);
        OrderDTO result = orderService.createOrder(req, "buyer@example.com");

        assertThat(result.getShippingAddress()).isEqualTo("123 Main St");
        assertThat(result.getPaymentMode()).isEqualTo("COD");
        assertThat(result.getStatus()).isEqualTo("PLACED");
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("239.98"));
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProductName()).isEqualTo("Air Max");
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);

        // stock should be decremented
        assertThat(product.getStockQty()).isEqualTo(8);
        verify(productRepository).save(product);
    }

    @Test
    void createOrder_userNotFound_throwsNotFoundException() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(makeOrderRequest("pid", "9", 1), "ghost@example.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_productNotFound_throwsNotFoundException() {
        String productId = UUID.randomUUID().toString();
        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(makeUser("buyer@example.com")));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(makeOrderRequest(productId, "9", 1), "buyer@example.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_insufficientStock_throwsBadRequestException() {
        String productId = UUID.randomUUID().toString();
        Product product = makeProduct(productId, "Air Max", new BigDecimal("119.99"), 1);

        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(makeUser("buyer@example.com")));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.createOrder(makeOrderRequest(productId, "9", 5), "buyer@example.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Insufficient stock");
        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    // --- getUserOrders ---

    @Test
    void getUserOrders_existingUser_returnsMappedDTOs() {
        User user = makeUser("buyer@example.com");
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress("456 Oak Ave");
        order.setPaymentMode("MOCK");
        order.setStatus("PLACED");
        order.setTotalAmount(new BigDecimal("99.99"));

        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(order));

        List<OrderDTO> results = orderService.getUserOrders("buyer@example.com");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getShippingAddress()).isEqualTo("456 Oak Ave");
        assertThat(results.get(0).getStatus()).isEqualTo("PLACED");
    }

    @Test
    void getUserOrders_userNotFound_throwsNotFoundException() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getUserOrders("ghost@example.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }

    // --- getOrderById ---

    @Test
    void getOrderById_ownerAccess_returnsDTO() {
        UUID orderId = UUID.randomUUID();
        User user = makeUser("owner@example.com");
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress("789 Pine Rd");
        order.setPaymentMode("COD");
        order.setStatus("SHIPPED");
        order.setTotalAmount(new BigDecimal("59.99"));

        when(orderRepository.findById(orderId.toString())).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(orderId, "owner@example.com");

        assertThat(result.getStatus()).isEqualTo("SHIPPED");
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("59.99"));
    }

    @Test
    void getOrderById_orderNotFound_throwsNotFoundException() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId.toString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(orderId, "user@example.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Order not found");
    }

    @Test
    void getOrderById_differentUser_throwsBadRequestException() {
        UUID orderId = UUID.randomUUID();
        User owner = makeUser("owner@example.com");
        Order order = new Order();
        order.setUser(owner);
        order.setShippingAddress("789 Pine Rd");
        order.setPaymentMode("COD");
        order.setStatus("PLACED");
        order.setTotalAmount(BigDecimal.TEN);

        when(orderRepository.findById(orderId.toString())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrderById(orderId, "intruder@example.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unauthorized");
    }
}
