package com.mystreet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mystreet.dto.ProductDTO;
import com.mystreet.exception.NotFoundException;
import com.mystreet.model.Product;
import com.mystreet.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductControllerTest {

    @Autowired private WebApplicationContext wac;
    @MockitoBean private ProductService productService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private Product makeProduct(String name, String brand) {
        Product p = new Product();
        p.setName(name);
        p.setBrand(brand);
        p.setSizesCsv("8,9,10");
        p.setPrice(new BigDecimal("99.99"));
        p.setStockQty(20);
        return p;
    }

    private ProductDTO makeProductDTO(String name, String brand) {
        ProductDTO dto = new ProductDTO();
        dto.setName(name);
        dto.setBrand(brand);
        dto.setSizesCsv("8,9,10");
        dto.setPrice(new BigDecimal("99.99"));
        dto.setStockQty(20);
        return dto;
    }

    // --- GET /api/products (public) ---

    @Test
    void getAllProducts_noFilters_returns200WithList() throws Exception {
        when(productService.getProducts(null, null))
                .thenReturn(List.of(makeProduct("Air Max", "Nike"), makeProduct("Ultraboost", "Adidas")));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Air Max"));
    }

    @Test
    void getAllProducts_withBrandFilter_returns200() throws Exception {
        when(productService.getProducts("Nike", null))
                .thenReturn(List.of(makeProduct("Air Max", "Nike")));

        mockMvc.perform(get("/api/products").param("brand", "Nike"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Nike"));
    }

    @Test
    void getAllProducts_withSizeFilter_returns200() throws Exception {
        when(productService.getProducts(null, "10"))
                .thenReturn(List.of(makeProduct("Air Max", "Nike")));

        mockMvc.perform(get("/api/products").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // --- GET /api/products/{id} (public) ---

    @Test
    void getProductById_existingId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.getProductById(id)).thenReturn(makeProduct("Air Max", "Nike"));

        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Air Max"));
    }

    @Test
    void getProductById_nonexistentId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.getProductById(id)).thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    // --- POST /api/products (ADMIN only via @PreAuthorize on a permitAll URL) ---

    @Test
    void createProduct_unauthenticated_returns403() throws Exception {
        // URL is permitAll(); @PreAuthorize("hasRole('ADMIN')") blocks → 403
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeProductDTO("Air Max", "Nike"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_userRole_returns403() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeProductDTO("Air Max", "Nike"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_adminRole_returns201() throws Exception {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(makeProduct("Air Max", "Nike"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeProductDTO("Air Max", "Nike"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Air Max"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_missingName_returns400() throws Exception {
        ProductDTO dto = makeProductDTO("", "Nike");
        dto.setName("");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // --- PUT /api/products/{id} (ADMIN only) ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_adminRole_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.updateProduct(eq(id), any(ProductDTO.class))).thenReturn(makeProduct("Updated Max", "Nike"));

        mockMvc.perform(put("/api/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeProductDTO("Updated Max", "Nike"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Max"));
    }

    // --- DELETE /api/products/{id} (ADMIN only) ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_adminRole_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(productService).deleteProduct(id);

        mockMvc.perform(delete("/api/products/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_nonexistentId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NotFoundException("Product not found")).when(productService).deleteProduct(id);

        mockMvc.perform(delete("/api/products/{id}", id))
                .andExpect(status().isNotFound());
    }
}
