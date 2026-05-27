package com.mystreet.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mystreet.dto.ProductDTO;
import com.mystreet.exception.NotFoundException;
import com.mystreet.model.Product;
import com.mystreet.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public List<Product> getProducts(String brand, String size) {
		boolean hasBrand = brand != null && !brand.isEmpty();
		boolean hasSize = size != null && !size.isEmpty();

		if (hasBrand && hasSize) {
			return productRepository.findByBrandIgnoreCaseAndSizesCsvContaining(brand, size);
		} else if (hasBrand) {
			return productRepository.findByBrandIgnoreCase(brand);
		} else if (hasSize) {
			return productRepository.findBySizesCsvContaining(size);
		}
		return productRepository.findAll();
	}

	public Product getProductById(UUID id) {
		return productRepository.findById(id.toString()).orElseThrow(() -> new NotFoundException("Product not found"));
	}

	public Product createProduct(ProductDTO dto) {
		Product product = new Product();
		product.setName(dto.getName());
		product.setBrand(dto.getBrand());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setImageUrl(dto.getImageUrl());
		product.setSizesCsv(dto.getSizesCsv());
		product.setStockQty(dto.getStockQty());

		return productRepository.save(product);
	}

	public Product updateProduct(UUID id, ProductDTO dto) {
		Product product = getProductById(id);

		product.setName(dto.getName());
		product.setBrand(dto.getBrand());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setImageUrl(dto.getImageUrl());
		product.setSizesCsv(dto.getSizesCsv());
		product.setStockQty(dto.getStockQty());

		return productRepository.save(product);
	}

	public void deleteProduct(UUID id) {
		Product product = getProductById(id);
		productRepository.delete(product);
	}
}
