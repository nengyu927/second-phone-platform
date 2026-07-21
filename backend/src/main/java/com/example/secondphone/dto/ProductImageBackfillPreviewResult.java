package com.example.secondphone.dto;

import java.util.List;

public record ProductImageBackfillPreviewResult(
        int scannedProducts,
        int uniqueModels,
        int matchedModels,
        int updatedProducts,
        int skippedProducts,
        int failedModels,
        List<ProductImageBackfillFailure> failures,
        List<ProductImageBackfillModelPreview> models) {
}
