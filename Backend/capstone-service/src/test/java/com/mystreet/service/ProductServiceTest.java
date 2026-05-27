package com.mystreet.service;

import com.mystreet.dto.ProductDTO;
import com.mystreet.exception.NotFoundException;
import com.mystreet.model.Product;
import com.mystreet.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @InjectMocks private ProductService productService;

    private Product makeProduct(String name, String brand, String sizes) {
        Product p = new Product();
        p.setName(name);
        p.setBrand(brand);
        p.setSizesCsv(sizes);
        p.setPrice(new BigDecimal("99.99"));
        p.setStockQty(10);
        return p;
    }

    private ProductDTO makeProductDTO(String name, String brand, String sizes) {
        ProductDTO dto = new ProductDTO();
        dto.setName(name);
        dto.setBrand(brand);
        dto.setSizesCsv(sizes);
        dto.setPrice(new BigDecimal("99.99"));
        dto.setStockQty(10);
        return dto;
    }

    // --- getProducts ---

    @Test
    void getProducts_noFilters_callsFindAll() {
        List<Product> all = List.of(makeProduct("Air Max", "Nike", "8,9,10"), makeProduct("Ultraboost", "Adidas", "9,10"));
        when(productRepository.findAll()).thenReturn(all);

        List<Product> result = productService.getProducts(null, null);

        assertThat(result).hasSize(2);
        verify(productRepository).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void getProducts_emptyFilters_callsFindAll() {
        when(productRepository.findAll()).thenReturn(List.of());

        productService.getProducts("", "");

        verify(productRepository).findAll();
    }

    @Test
    void getProducts_brandOnly_callsFindByBrand() {
        List<Product> nikeProducts = List.of(makeProduct("Air Max", "Nike", "8,9,10"));
        when(productRepository.findByBrandIgnoreCase("Nike")).thenReturn(nikeProducts);

        List<Product> result = productService.getProducts("Nike", null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Nike");
        verify(productRepository).findByBrandIgnoreCase("Nike");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void getProducts_sizeOnly_callsFindBySize() {
        List<Product> size10Products = List.of(makeProduct("Air Max", "Nike", "8,9,10"), makeProduct("Ultraboost", "Adidas", "9,10"));
        when(productRepository.findBySizesCsvContaining("10")).thenReturn(size10Products);

        List<Product> result = productService.getProducts(null, "10");

        assertThat(result).hasSize(2);
        verify(productRepository).findBySizesCsvContaining("10");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void getProducts_brandAndSize_callsCombinedQuery() {
        List<Product> filtered = List.of(makeProduct("Air Max", "Nike", "9,10"));
        when(productRepository.findByBrandIgnoreCaseAndSizesCsvContaining("Nike", "10")).thenReturn(filtered);

        List<Product> result = productService.getProducts("Nike", "10");

        assertThat(result).hasSize(1);
        verify(productRepository).findByBrandIgnoreCaseAndSizesCsvContaining("Nike", "10");
        verifyNoMoreInteractions(productRepository);
    }

    // --- getProductById ---

    @Test
    void getProductById_existingId_returnsProduct() {
        UUID id = UUID.randomUUID();
        Product product = makeProduct("Air Max", "Nike", "9,10");
        when(productRepository.findById(id.toString())).thenReturn(Optional.of(product));

        Product result = productService.getProductById(id);

        assertThat(result.getName()).isEqualTo("Air Max");
    }

    @Test
    void getProductById_unknownId_throwsNotFoundException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id.toString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Product not found");
    }

    // --- createProduct ---

    @Test
    void createProduct_validDto_savesAndReturnsProduct() {
        ProductDTO dto = makeProductDTO("Air Max", "Nike", "8,9,10");
        Product saved = makeProduct("Air Max", "Nike", "8,9,10");
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.createProduct(dto);

        assertThat(result.getName()).isEqualTo("Air Max");
        assertThat(result.getBrand()).isEqualTo("Nike");
        verify(productRepository).save(any(Product.class));
    }

    // --- updateProduct ---

    @Test
    void updateProduct_existingId_updatesFieldsAndSaves() {
        UUID id = UUID.randomUUID();
        Product existing = makeProduct("Old Name", "OldBrand", "7");
        when(productRepository.findById(id.toString())).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductDTO dto = makeProductDTO("New Name", "NewBrand", "8,9");
        dto.setPrice(new BigDecimal("149.99"));
        dto.setStockQty(25);

        Product result = productService.updateProduct(id, dto);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getBrand()).isEqualTo("NewBrand");
        assertThat(result.getStockQty()).isEqualTo(25);
        verify(productRepository).save(existing);
    }

    @Test
    void updateProduct_unknownId_throwsNotFoundException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id.toString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(id, makeProductDTO("X", "Y", "8")))
                .isInstanceOf(NotFoundException.class);
        verify(productRepository, never()).save(any());
    }

    // --- deleteProduct ---

    @Test
    void deleteProduct_existingId_deletesProduct() {
        UUID id = UUID.randomUUID();
        Product product = makeProduct("Air Max", "Nike", "9");
        when(productRepository.findById(id.toString())).thenReturn(Optional.of(product));

        productService.deleteProduct(id);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_unknownId_throwsNotFoundException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id.toString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(id))
                .isInstanceOf(NotFoundException.class);
        verify(productRepository, never()).delete(any());
    }
}
