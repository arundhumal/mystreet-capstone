package com.mystreet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private String id;
    private List<OrderItemDTO> items;
    private String shippingAddress;
    private String paymentMode;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    @Data
    public static class OrderItemDTO {
        private String productId;
        private String productName;
        private String size;
        private Integer quantity;
        private BigDecimal priceAtOrder;
    }
}
