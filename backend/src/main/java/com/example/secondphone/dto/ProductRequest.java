package com.example.secondphone.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record ProductRequest(
        @Size(max = 40) String productCode,
        @NotBlank(message = "商品名稱為必填") @Size(max = 150) String productName,
        Long brandId,
        @Size(max = 50) String brand,
        Long categoryId,
        @NotBlank(message = "型號為必填") @Size(max = 100) String model,
        @Size(max = 30) String storageCapacity,
        @Size(max = 50) String color,
        @NotBlank(message = "機況為必填") String conditionLevel,
        @NotNull(message = "售價為必填") @DecimalMin(value = "0", message = "售價不可小於 0") BigDecimal price,
        @DecimalMin(value = "0", message = "成本不可小於 0") BigDecimal cost,
        @NotNull(message = "庫存為必填") @Min(value = 0, message = "庫存不可小於 0") Integer stock,
        @Min(value = 0, message = "保留庫存不可小於 0") Integer reservedStock,
        String description,
        @Size(max = 500) String imageUrl,
        @NotBlank(message = "商品狀態為必填") String status,
        boolean featured) {}
