package com.mystreet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    @NotBlank(message = "Product name is required")
    private String name;

    private String brand;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private String imageUrl;

    @NotBlank(message = "Sizes are required")
    private String sizesCsv;

    @NotNull(message = "Stock quantity is required")
    private Integer stockQty;
}
