package com.example.secondphone.dto;

import java.util.List;

public record ProductImageBackfillModelPreview(
        String normalizedModelName,
        String productType,
        int productCount,
        List<Long> productIds,
        List<String> productNames,
        boolean matched,
        boolean placeholder,
        String sourceType,
        String previewUrl,
        String sourcePageUrl,
        String failureReason) {
}
