package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.secondphone.dto.ProductImageBackfillPreviewResult;
import com.example.secondphone.entity.Product;
import com.example.secondphone.repository.ProductImageRepository;
import com.example.secondphone.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductImageBackfillPreviewServiceTests {
    @Mock ProductRepository products;
    @Mock ProductImageRepository images;
    private ProductImageBackfillPreviewService service;

    @BeforeEach
    void setUp() {
        service = new ProductImageBackfillPreviewService(products, images,
                new AppleProductModelNormalizer(), new AppleProductImageSourceRegistry());
    }

    @Test
    void groupsSameModelAndDoesNotIncludeProductsWithExistingImages() {
        Product first = product(1L, "iPhone 13 128GB A級", null);
        Product second = product(2L, "iPhone 13 256GB 藍色", "");
        Product existing = product(3L, "iPhone 14 Pro Max 256GB", "/uploads/products/existing.jpg");
        Product unknown = product(4L, "Apple 配件 九成新", null);
        when(products.findAll()).thenReturn(List.of(first, second, existing, unknown));
        when(images.existsByProductId(1L)).thenReturn(false);
        when(images.existsByProductId(2L)).thenReturn(false);
        when(images.existsByProductId(4L)).thenReturn(false);

        ProductImageBackfillPreviewResult result = service.preview();

        assertEquals(4, result.scannedProducts());
        assertEquals(1, result.uniqueModels());
        assertEquals(1, result.matchedModels());
        assertEquals(0, result.updatedProducts());
        assertEquals(1, result.skippedProducts());
        assertEquals(1, result.failedModels());
        assertEquals(2, result.models().get(0).productCount());
        assertTrue(result.models().get(0).matched());
        assertFalse(result.models().get(0).placeholder());
    }

    private Product product(Long id, String name, String imageUrl) {
        Product product = new Product();
        product.setId(id);
        product.setProductName(name);
        product.setBrand("Apple");
        product.setImageUrl(imageUrl);
        return product;
    }
}
