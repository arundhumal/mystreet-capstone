package com.mystreet.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	@Id
	@Column(columnDefinition = "VARCHAR(36)")
	private String id = UUID.randomUUID().toString();

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private boolean isAdmin = false;

	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}