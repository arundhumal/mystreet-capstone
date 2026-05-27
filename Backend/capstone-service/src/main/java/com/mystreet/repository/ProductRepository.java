package com.mystreet.repository;

import com.mystreet.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByBrandIgnoreCase(String brand);
    List<Product> findBySizesCsvContaining(String size);
    List<Product> findByBrandIgnoreCaseAndSizesCsvContaining(String brand, String size);
}
