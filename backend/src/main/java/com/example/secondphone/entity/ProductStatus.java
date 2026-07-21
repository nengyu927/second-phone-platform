package com.example.secondphone.entity;

public enum ProductStatus {
    DRAFT,
    ACTIVE,
    OUT_OF_STOCK,
    DISCONTINUED;

    public static String normalize(String value) {
        if (value == null || value.isBlank()) return ACTIVE.name();
        return switch (value.trim().toUpperCase()) {
            case "AVAILABLE" -> ACTIVE.name();
            case "SOLD_OUT" -> OUT_OF_STOCK.name();
            case "DISABLED" -> DISCONTINUED.name();
            default -> ProductStatus.valueOf(value.trim().toUpperCase()).name();
        };
    }
}
