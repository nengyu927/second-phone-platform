package com.example.secondphone.dto;

import java.util.List;

public record ProductCsvImportResult(
        int totalCount,
        int successCount,
        int failedCount,
        int skippedCount,
        List<ProductCsvImportFailure> failures) {
}
