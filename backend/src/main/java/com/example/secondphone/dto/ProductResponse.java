package com.example.secondphone.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id, String productCode, String productName,
        Long brandId, String brand, Long categoryId, String category,
        String model, String storageCapacity, String color, String conditionLevel,
        BigDecimal cost, BigDecimal price, int stock, int reservedStock, int availableStock,
        String description, String imageUrl, String status, boolean featured,
        List<ProductImageResponse> images, LocalDateTime createdAt, LocalDateTime updatedAt) {}
