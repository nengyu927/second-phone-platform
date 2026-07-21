package com.example.secondphone.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondphone.dto.ProductImageBackfillFailure;
import com.example.secondphone.dto.ProductImageBackfillModelPreview;
import com.example.secondphone.dto.ProductImageBackfillPreviewResult;
import com.example.secondphone.entity.Product;
import com.example.secondphone.repository.ProductImageRepository;
import com.example.secondphone.repository.ProductRepository;
import com.example.secondphone.service.AppleProductImageSourceRegistry.ImageSource;

@Service
public class ProductImageBackfillPreviewService {
    private final ProductRepository products;
    private final ProductImageRepository images;
    private final AppleProductModelNormalizer normalizer;
    private final AppleProductImageSourceRegistry sources;

    public ProductImageBackfillPreviewService(ProductRepository products, ProductImageRepository images,
            AppleProductModelNormalizer normalizer, AppleProductImageSourceRegistry sources) {
        this.products = products;
        this.images = images;
        this.normalizer = normalizer;
        this.sources = sources;
    }

    @Transactional(readOnly = true)
    public ProductImageBackfillPreviewResult preview() {
        List<Product> appleProducts = products.findAll().stream().filter(this::isApple).toList();
        List<Product> candidates = appleProducts.stream().filter(this::hasNoImage).toList();
        Map<String, List<Product>> grouped = new LinkedHashMap<>();
        List<ProductImageBackfillFailure> failures = new ArrayList<>();

        for (Product product : candidates) {
            normalizer.normalize(product.getProductName()).ifPresentOrElse(
                    model -> grouped.computeIfAbsent(model, ignored -> new ArrayList<>()).add(product),
                    () -> failures.add(new ProductImageBackfillFailure(product.getProductName(), "無法從商品名稱辨識 Apple 標準機型")));
        }

        List<ProductImageBackfillModelPreview> previews = new ArrayList<>();
        int matched = 0;
        for (Map.Entry<String, List<Product>> entry : grouped.entrySet()) {
            String model = entry.getKey();
            List<Product> modelProducts = entry.getValue();
            ImageSource source = sources.find(model).orElse(null);
            boolean isMatched = source != null;
            if (isMatched) matched++;
            String failureReason = isMatched ? null : "尚無經確認的 Apple 官方或 Wikimedia Commons 圖片，正式執行時將使用統一文字 placeholder";
            if (!isMatched) failures.add(new ProductImageBackfillFailure(model, failureReason));
            previews.add(new ProductImageBackfillModelPreview(
                    model,
                    normalizer.productType(model),
                    modelProducts.size(),
                    modelProducts.stream().map(Product::getId).toList(),
                    modelProducts.stream().map(Product::getProductName).toList(),
                    isMatched,
                    !isMatched,
                    isMatched ? source.sourceType() : "PROJECT_PLACEHOLDER",
                    isMatched ? source.previewUrl() : null,
                    isMatched ? source.sourcePageUrl() : null,
                    failureReason));
        }

        long unrecognized = candidates.size() - grouped.values().stream().mapToLong(List::size).sum();
        int failedModels = grouped.size() - matched + (int) unrecognized;
        return new ProductImageBackfillPreviewResult(
                appleProducts.size(), grouped.size(), matched, 0,
                appleProducts.size() - candidates.size(), failedModels,
                List.copyOf(failures), List.copyOf(previews));
    }

    private boolean isApple(Product product) {
        String brand = product.getBrandEntity() == null ? product.getBrand() : product.getBrandEntity().getName();
        return brand != null && brand.equalsIgnoreCase("Apple");
    }

    private boolean hasNoImage(Product product) {
        return (product.getImageUrl() == null || product.getImageUrl().isBlank()) && !images.existsByProductId(product.getId());
    }
}
