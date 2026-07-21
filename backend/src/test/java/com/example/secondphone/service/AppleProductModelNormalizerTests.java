package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AppleProductModelNormalizerTests {
    private final AppleProductModelNormalizer normalizer = new AppleProductModelNormalizer();

    @Test
    void removesCapacityConditionColorUsageAndNotesByExtractingStandardModel() {
        assertEquals("iPhone 13", normalizer.normalize("iPhone 13 128GB A級 九成新 藍色 外觀小刮痕").orElseThrow());
        assertEquals("iPhone 14 Pro Max", normalizer.normalize("iPhone 14 Pro Max 256GB 深紫色 B級").orElseThrow());
        assertEquals("Apple Watch Series 8", normalizer.normalize("Apple Watch Series 8 45mm 午夜色").orElseThrow());
        assertEquals("iPad Air 5", normalizer.normalize("iPad Air 5 64GB 九成新").orElseThrow());
    }

    @Test
    void rejectsNamesWithoutRecognizableAppleModel() {
        assertTrue(normalizer.normalize("Apple 二手商品 A級").isEmpty());
    }
}
