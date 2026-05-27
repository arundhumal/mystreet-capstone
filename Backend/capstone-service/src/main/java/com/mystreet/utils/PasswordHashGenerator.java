package com.mystreet.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String adminPassword = "admin123";
		String userPassword = "user123";

		String adminHash = encoder.encode(adminPassword);
		String userHash = encoder.encode(userPassword);

		System.out.println("Admin password (admin123) hash: " + adminHash);
		System.out.println("User password (user123) hash: " + userHash);

		// Verify they work
		System.out.println("\nVerification:");
		System.out.println("Admin password matches: " + encoder.matches(adminPassword, adminHash));
		System.out.println("User password matches: " + encoder.matches(userPassword, userHash));
	}
}