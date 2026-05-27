package com.mystreet.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.Servlet;

@Configuration
public class H2ConsoleConfig {

	@Bean
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ServletRegistrationBean h2ConsoleServletRegistration() {
		String h2ConsolePath = "/h2-console";

		System.out.println("=== Attempting to register H2 Console ===");

		try {
			// Try JakartaWebServlet for H2 2.x
			System.out.println("Trying to load: org.h2.server.web.JakartaWebServlet");
			Class<?> servletClass = Class.forName("org.h2.server.web.JakartaWebServlet");
			System.out.println("Successfully loaded JakartaWebServlet");

			Object servlet = servletClass.getDeclaredConstructor().newInstance();
			System.out.println("Successfully created servlet instance");

			ServletRegistrationBean bean = new ServletRegistrationBean((Servlet) servlet);
			bean.addUrlMappings(h2ConsolePath + "/*");
			bean.setLoadOnStartup(1);

			System.out.println("H2 Console registered at: " + h2ConsolePath);
			return bean;

		} catch (ClassNotFoundException e) {
			System.out.println("JakartaWebServlet not found: " + e.getMessage());
			System.out.println("Trying to load: org.h2.server.web.WebServlet");

			try {
				// Fallback to WebServlet for H2 1.x
				Class<?> servletClass = Class.forName("org.h2.server.web.WebServlet");
				System.out.println("Successfully loaded WebServlet");

				Object servlet = servletClass.getDeclaredConstructor().newInstance();
				System.out.println("Successfully created servlet instance");

				ServletRegistrationBean bean = new ServletRegistrationBean((Servlet) servlet);
				bean.addUrlMappings(h2ConsolePath + "/*");
				bean.setLoadOnStartup(1);

				System.out.println("H2 Console registered at: " + h2ConsolePath);
				return bean;

			} catch (Exception ex) {
				System.err.println("Failed to load WebServlet: " + ex.getMessage());
				ex.printStackTrace();
				throw new RuntimeException(
						"Failed to register H2 Console servlet. Make sure H2 database is in your dependencies.", ex);
			}
		} catch (Exception e) {
			System.err.println("Unexpected error: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to register H2 Console servlet", e);
		}
	}
}
