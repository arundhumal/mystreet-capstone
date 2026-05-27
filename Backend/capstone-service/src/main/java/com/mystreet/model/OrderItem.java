package com.mystreet.model;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
	@Id
	@Column(columnDefinition = "VARCHAR(36)")
	private String id = UUID.randomUUID().toString();

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private String size;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private BigDecimal priceAtOrder;
}
