package com.example.secondphone.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.secondphone.dto.*;
import com.example.secondphone.entity.*;
import com.example.secondphone.exception.ResourceNotFoundException;
import com.example.secondphone.repository.ProductImageRepository;

@Service
public class ProductImageService {
    private final ProductImageRepository images;
    private final ProductCatalogService products;
    public ProductImageService(ProductImageRepository images, ProductCatalogService products) { this.images = images; this.products = products; }

    @Transactional
    public ProductImageResponse add(Long productId, ProductImageRequest request) {
        Product product = products.getEntity(productId);
        List<ProductImage> existing = images.findByProductIdOrderBySortOrderAscIdAsc(productId);
        boolean primary = request.primaryImage() || existing.isEmpty();
        if (primary) existing.forEach(i -> i.setPrimaryImage(false));
        ProductImage image = new ProductImage(); image.setProduct(product); image.setImageUrl(request.imageUrl().trim());
        image.setAltText(request.altText()); image.setPrimaryImage(primary); image.setSortOrder(request.sortOrder());
        image = images.save(image);
        if (primary) { product.setImageUrl(image.getImageUrl()); }
        return map(image);
    }

    @Transactional
    public void delete(Long productId, Long imageId) {
        ProductImage image = images.findById(imageId).filter(i -> i.getProduct().getId().equals(productId))
                .orElseThrow(() -> new ResourceNotFoundException("找不到指定商品圖片"));
        boolean wasPrimary = image.isPrimaryImage(); images.delete(image);
        if (wasPrimary) {
            List<ProductImage> remaining = images.findByProductIdOrderBySortOrderAscIdAsc(productId);
            if (!remaining.isEmpty()) { remaining.get(0).setPrimaryImage(true); products.getEntity(productId).setImageUrl(remaining.get(0).getImageUrl()); }
            else products.getEntity(productId).setImageUrl(null);
        }
    }

    private ProductImageResponse map(ProductImage i) { return new ProductImageResponse(i.getId(), i.getImageUrl(), i.getAltText(), i.isPrimaryImage(), i.getSortOrder()); }
}
