package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.secondphone.entity.Product;
import com.example.secondphone.repository.ProductImageRepository;
import com.example.secondphone.repository.ProductRepository;
import com.example.secondphone.service.ProductImageBackfillRowService.UpdateOutcome;

@ExtendWith(MockitoExtension.class)
class ProductImageBackfillRowServiceTests {
    @Mock ProductRepository products;
    @Mock ProductImageRepository images;
    private ProductImageBackfillRowService service;

    @BeforeEach
    void setUp() { service = new ProductImageBackfillRowService(products, images); }

    @Test
    void attachesImageThroughProductImageMechanism() {
        Product product = new Product(); product.setId(1L); product.setProductName("iPhone 13");
        when(products.findById(1L)).thenReturn(Optional.of(product));

        UpdateOutcome outcome = service.attach(1L, "/uploads/products/iphone-13/representative.jpg", "iPhone 13");

        assertEquals(UpdateOutcome.UPDATED, outcome);
        assertEquals("/uploads/products/iphone-13/representative.jpg", product.getImageUrl());
        verify(images).save(any());
        verify(products).save(product);
    }

    @Test
    void neverOverwritesExistingProductImage() {
        Product product = new Product(); product.setId(1L); product.setImageUrl("/uploads/products/existing.jpg");
        when(products.findById(1L)).thenReturn(Optional.of(product));

        assertEquals(UpdateOutcome.SKIPPED, service.attach(1L, "/new.jpg", "iPhone 13"));
        verify(images, never()).save(any());
        verify(products, never()).save(any());
    }
}
