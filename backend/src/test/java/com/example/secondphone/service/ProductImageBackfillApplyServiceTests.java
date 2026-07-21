package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.secondphone.dto.ProductImageBackfillApplyResult;
import com.example.secondphone.entity.Product;
import com.example.secondphone.repository.ProductImageRepository;
import com.example.secondphone.repository.ProductRepository;
import com.example.secondphone.service.ProductImageBackfillRowService.UpdateOutcome;

@ExtendWith(MockitoExtension.class)
class ProductImageBackfillApplyServiceTests {
    @Mock ProductRepository products;
    @Mock ProductImageRepository images;
    @Mock ProductImageBackfillStorageService storage;
    @Mock ProductImageBackfillRowService rows;
    private ProductImageBackfillApplyService service;

    @BeforeEach
    void setUp() {
        service = new ProductImageBackfillApplyService(products, images, new AppleProductModelNormalizer(),
                new AppleProductImageSourceRegistry(), storage, rows);
    }

    @Test
    void onlyAppliesExplicitlyConfirmedModelsAndUsesPlaceholderWhenUnmatched() {
        Product confirmed = product(1L, "iPhone 15 128GB A級");
        Product notConfirmed = product(2L, "iPhone 13 256GB");
        when(products.findAll()).thenReturn(List.of(confirmed, notConfirmed));
        when(storage.placeholder("iPhone 15", "iPhone")).thenReturn("/uploads/products/iphone-15/placeholder.png");
        when(rows.attach(1L, "/uploads/products/iphone-15/placeholder.png", "iPhone 15")).thenReturn(UpdateOutcome.UPDATED);

        ProductImageBackfillApplyResult result = service.apply(List.of("iPhone 15"));

        assertEquals(1, result.updatedProducts());
        assertEquals(1, result.skippedProducts());
        assertEquals(1, result.failedModels());
        verify(rows).attach(1L, "/uploads/products/iphone-15/placeholder.png", "iPhone 15");
    }

    private Product product(Long id, String name) {
        Product product = new Product(); product.setId(id); product.setProductName(name); product.setBrand("Apple");
        return product;
    }
}
