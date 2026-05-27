package com.mystreet.service;

import com.mystreet.dto.OrderDTO;
import com.mystreet.dto.OrderRequest;
import com.mystreet.exception.BadRequestException;
import com.mystreet.exception.NotFoundException;
import com.mystreet.model.Order;
import com.mystreet.model.OrderItem;
import com.mystreet.model.Product;
import com.mystreet.model.User;
import com.mystreet.repository.OrderRepository;
import com.mystreet.repository.ProductRepository;
import com.mystreet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderDTO createOrder(OrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMode(request.getPaymentMode());
        order.setStatus("PLACED");

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).orElseThrow(() -> new NotFoundException("Product not found: " + itemRequest.getProductId()));

            if (product.getStockQty() < itemRequest.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setSize(itemRequest.getSize());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtOrder(product.getPrice());

            order.getItems().add(orderItem);

            // Update stock
            product.setStockQty(product.getStockQty() - itemRequest.getQuantity());
            productRepository.save(product);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        return convertToDTO(order);
    }

    public List<OrderDTO> getUserOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("User not found"));

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OrderDTO getOrderById(UUID id, String userEmail) {
        Order order = orderRepository.findById(id.toString()).orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("Unauthorized access to order");
        }

        return convertToDTO(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMode(order.getPaymentMode());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderDTO.OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
            OrderDTO.OrderItemDTO itemDTO = new OrderDTO.OrderItemDTO();
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setSize(item.getSize());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPriceAtOrder(item.getPriceAtOrder());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}
