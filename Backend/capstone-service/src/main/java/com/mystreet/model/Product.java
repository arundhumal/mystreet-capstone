package com.mystreet.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
	@Id
	@Column(columnDefinition = "VARCHAR(36)")
	private String id = UUID.randomUUID().toString();

	@Column(nullable = false)
	private String name;

	private String brand;

	@Column(length = 1000)
	private String description;

	@Column(nullable = false)
	private BigDecimal price;

	private String imageUrl;

	@Column(nullable = false)
	private String sizesCsv;

	@Column(nullable = false)
	private Integer stockQty = 0;

	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}
