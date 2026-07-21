package com.example.secondphone.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.secondphone.dto.ProductImageBackfillApplyResult;
import com.example.secondphone.dto.ProductImageBackfillFailure;
import com.example.secondphone.entity.Product;
import com.example.secondphone.repository.ProductImageRepository;
import com.example.secondphone.repository.ProductRepository;
import com.example.secondphone.service.AppleProductImageSourceRegistry.ImageSource;
import com.example.secondphone.service.ProductImageBackfillRowService.UpdateOutcome;
import com.example.secondphone.service.ProductImageBackfillStorageService.ImageAcquisitionException;

@Service
public class ProductImageBackfillApplyService {
    private final ProductRepository products;
    private final ProductImageRepository images;
    private final AppleProductModelNormalizer normalizer;
    private final AppleProductImageSourceRegistry sources;
    private final ProductImageBackfillStorageService storage;
    private final ProductImageBackfillRowService rowService;

    public ProductImageBackfillApplyService(ProductRepository products, ProductImageRepository images,
            AppleProductModelNormalizer normalizer, AppleProductImageSourceRegistry sources,
            ProductImageBackfillStorageService storage, ProductImageBackfillRowService rowService) {
        this.products = products;
        this.images = images;
        this.normalizer = normalizer;
        this.sources = sources;
        this.storage = storage;
        this.rowService = rowService;
    }

    public ProductImageBackfillApplyResult apply(List<String> confirmedModels) {
        Set<String> confirmed = new HashSet<>(confirmedModels);
        List<Product> appleProducts = products.findAll().stream().filter(this::isApple).toList();
        List<Product> candidates = appleProducts.stream().filter(this::hasNoImage).toList();
        Map<String, List<Product>> grouped = new LinkedHashMap<>();
        List<ProductImageBackfillFailure> failures = new ArrayList<>();
        Set<String> failedModels = new LinkedHashSet<>();
        int unrecognizedProducts = 0;

        for (Product product : candidates) {
            var normalized = normalizer.normalize(product.getProductName());
            if (normalized.isEmpty()) {
                unrecognizedProducts++;
                failedModels.add(product.getProductName());
                failures.add(new ProductImageBackfillFailure(product.getProductName(), "無法從商品名稱辨識 Apple 標準機型"));
            } else {
                grouped.computeIfAbsent(normalized.get(), ignored -> new ArrayList<>()).add(product);
            }
        }

        int updated = 0;
        int skipped = appleProducts.size() - candidates.size() + unrecognizedProducts;
        for (Map.Entry<String, List<Product>> entry : grouped.entrySet()) {
            String model = entry.getKey();
            List<Product> modelProducts = entry.getValue();
            if (!confirmed.contains(model)) {
                skipped += modelProducts.size();
                continue;
            }

            ImageSource source = sources.find(model).orElse(null);
            String imageUrl;
            if (source == null) {
                failedModels.add(model);
                failures.add(new ProductImageBackfillFailure(model, "無合法外部圖片，已使用專案統一 placeholder"));
                try {
                    imageUrl = storage.placeholder(model, normalizer.productType(model));
                } catch (ImageAcquisitionException exception) {
                    failures.add(new ProductImageBackfillFailure(model, exception.getMessage()));
                    skipped += modelProducts.size();
                    continue;
                }
            } else {
                try {
                    imageUrl = storage.acquire(model, normalizer.productType(model), source);
                } catch (ImageAcquisitionException exception) {
                    failedModels.add(model);
                    failures.add(new ProductImageBackfillFailure(model, "外部圖片下載失敗，已改用 placeholder：" + exception.getMessage()));
                    try {
                        imageUrl = storage.placeholder(model, normalizer.productType(model));
                    } catch (ImageAcquisitionException placeholderException) {
                        failures.add(new ProductImageBackfillFailure(model, placeholderException.getMessage()));
                        skipped += modelProducts.size();
                        continue;
                    }
                }
            }

            for (Product product : modelProducts) {
                try {
                    if (rowService.attach(product.getId(), imageUrl, model) == UpdateOutcome.UPDATED) updated++;
                    else skipped++;
                } catch (RuntimeException exception) {
                    failedModels.add(model);
                    failures.add(new ProductImageBackfillFailure(model, "商品 #" + product.getId() + " 圖片寫入失敗"));
                }
            }
        }

        int matched = (int) grouped.keySet().stream().filter(model -> sources.find(model).isPresent()).count();
        return new ProductImageBackfillApplyResult(
                appleProducts.size(), grouped.size(), matched, updated, skipped,
                failedModels.size(), List.copyOf(failures));
    }

    private boolean isApple(Product product) {
        String brand = product.getBrandEntity() == null ? product.getBrand() : product.getBrandEntity().getName();
        return brand != null && brand.equalsIgnoreCase("Apple");
    }

    private boolean hasNoImage(Product product) {
        return (product.getImageUrl() == null || product.getImageUrl().isBlank()) && !images.existsByProductId(product.getId());
    }
}
