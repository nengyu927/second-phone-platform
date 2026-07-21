package com.example.secondphone.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondphone.entity.Product;
import com.example.secondphone.entity.ProductImage;
import com.example.secondphone.repository.ProductImageRepository;
import com.example.secondphone.repository.ProductRepository;

@Service
public class ProductImageBackfillRowService {
    public enum UpdateOutcome { UPDATED, SKIPPED }

    private final ProductRepository products;
    private final ProductImageRepository images;

    public ProductImageBackfillRowService(ProductRepository products, ProductImageRepository images) {
        this.products = products;
        this.images = images;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UpdateOutcome attach(Long productId, String imageUrl, String normalizedModelName) {
        Product product = products.findById(productId).orElse(null);
        if (product == null || (product.getImageUrl() != null && !product.getImageUrl().isBlank())
                || images.existsByProductId(productId)) return UpdateOutcome.SKIPPED;

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(imageUrl);
        image.setAltText(normalizedModelName + " 代表圖片");
        image.setPrimaryImage(true);
        image.setSortOrder(0);
        images.save(image);
        product.setImageUrl(imageUrl);
        products.save(product);
        return UpdateOutcome.UPDATED;
    }
}
