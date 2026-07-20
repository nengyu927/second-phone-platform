package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.secondphone.entity.Product;
import com.example.secondphone.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void createProductSuccessfully() {
        Product product = product("iPhone 15", "Apple");
        when(productRepository.save(product)).thenReturn(product);

        assertSame(product, productService.create(product));
        verify(productRepository).save(product);
    }

    @Test
    void findMissingProductReturnsNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.findById(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateProductSuccessfullyAndKeepsIdentityAndCreatedTime() {
        Product existing = product("舊商品", "Apple");
        existing.setId(1L);
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 20, 10, 0);
        existing.setCreatedAt(createdAt);
        Product changes = product("新商品", "Samsung");
        changes.setModel("S25");
        changes.setStorageCapacity("256GB");
        changes.setColor("黑色");
        changes.setConditionLevel("LIKE_NEW");
        changes.setPrice(new BigDecimal("18900.00"));
        changes.setStock(3);
        changes.setDescription("九成新展示機");
        changes.setImageUrl("https://example.com/s25.jpg");
        changes.setStatus("AVAILABLE");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        Product updated = productService.update(1L, changes);

        assertEquals(1L, updated.getId());
        assertEquals(createdAt, updated.getCreatedAt());
        assertEquals("新商品", updated.getProductName());
        assertEquals("Samsung", updated.getBrand());
        assertEquals("S25", updated.getModel());
        assertEquals(new BigDecimal("18900.00"), updated.getPrice());
        assertEquals(Integer.valueOf(3), updated.getStock());
    }

    @Test
    void deleteProductSuccessfully() {
        Product existing = product("iPhone 15", "Apple");
        existing.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        productService.delete(1L);

        verify(productRepository).delete(existing);
    }

    @Test
    void negativePriceReturnsBadRequest() {
        Product product = product("iPhone 15", "Apple");
        product.setPrice(new BigDecimal("-1.00"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.create(product));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, never()).save(product);
    }

    @Test
    void negativeStockReturnsBadRequest() {
        Product product = product("iPhone 15", "Apple");
        product.setStock(-1);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.create(product));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, never()).save(product);
    }

    @Test
    void searchProductsWithAndConditions() {
        Product matching = product("iPhone 15", "Apple");
        Product wrongStatus = product("iPhone 15 Pro", "Apple");
        wrongStatus.setStatus("SOLD_OUT");
        when(productRepository.findByProductNameContainingIgnoreCase("iPhone"))
                .thenReturn(List.of(matching, wrongStatus));

        List<Product> result = productService.search(" iPhone ", "Apple", "AVAILABLE");

        assertEquals(List.of(matching), result);
        verify(productRepository).findByProductNameContainingIgnoreCase("iPhone");
    }

    private Product product(String productName, String brand) {
        Product product = new Product();
        product.setProductName(productName);
        product.setBrand(brand);
        product.setModel("Test Model");
        product.setConditionLevel("GOOD");
        product.setPrice(new BigDecimal("10000.00"));
        product.setStock(1);
        product.setStatus("AVAILABLE");
        return product;
    }
}
