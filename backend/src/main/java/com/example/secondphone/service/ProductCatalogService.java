package com.example.secondphone.service;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.secondphone.dto.*;
import com.example.secondphone.entity.*;
import com.example.secondphone.exception.*;
import com.example.secondphone.repository.*;
import jakarta.persistence.criteria.Predicate;

@Service
public class ProductCatalogService {
    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "price", "productName", "stock");
    private final ProductRepository products;
    private final BrandService brands;
    private final CategoryService categories;
    private final ProductImageRepository images;

    public ProductCatalogService(ProductRepository products, BrandService brands, CategoryService categories, ProductImageRepository images) {
        this.products = products; this.brands = brands; this.categories = categories; this.images = images;
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> search(String keyword, Long brandId, Long categoryId, String condition,
            String status, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Boolean featured,
            int page, int size, String sort, String direction) {
        String sortField = ALLOWED_SORTS.contains(sort) ? sort : "createdAt";
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 60), Sort.by(sortDirection, sortField));
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(keyword)) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("productName")), like), cb.like(cb.lower(root.get("model")), like), cb.like(cb.lower(root.get("productCode")), like)));
            }
            if (brandId != null) predicates.add(cb.equal(root.get("brandEntity").get("id"), brandId));
            if (categoryId != null) predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            if (hasText(condition)) predicates.add(cb.equal(cb.upper(root.get("conditionLevel")), condition.trim().toUpperCase()));
            if (hasText(status)) predicates.add(cb.equal(cb.upper(root.get("status")), ProductStatus.normalize(status)));
            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            if (Boolean.TRUE.equals(inStock)) predicates.add(cb.greaterThan(root.get("stock"), 0));
            if (featured != null) predicates.add(cb.equal(root.get("featured"), featured));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return PagedResponse.from(products.findAll(spec, pageable).map(this::map));
    }

    @Transactional(readOnly = true) public ProductResponse findResponse(Long id) { return map(getEntity(id)); }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product(); apply(product, request, true); return map(products.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getEntity(id); apply(product, request, false); return map(products.save(product));
    }

    @Transactional
    public void delete(Long id) { products.delete(getEntity(id)); }

    @Transactional(readOnly = true)
    public Product getEntity(Long id) { return products.findById(id).orElseThrow(() -> new ResourceNotFoundException("找不到指定商品")); }

    private void apply(Product product, ProductRequest request, boolean creating) {
        String code = hasText(request.productCode()) ? request.productCode().trim().toUpperCase() : product.getProductCode();
        if (!hasText(code) && creating) code = "SP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (!Objects.equals(code, product.getProductCode()) && products.existsByProductCode(code)) throw new DuplicateResourceException("商品編號已存在");
        Brand brandEntity = request.brandId() == null ? null : brands.getEntity(request.brandId());
        if (brandEntity == null && !hasText(request.brand())) throw new BusinessException("請選擇品牌或填寫既有品牌名稱");
        Category category = request.categoryId() == null ? null : categories.getEntity(request.categoryId());
        String condition = request.conditionLevel().trim().toUpperCase();
        try { ProductCondition.valueOf(condition); } catch (IllegalArgumentException ex) { throw new BusinessException("商品機況不正確"); }
        String status;
        try { status = ProductStatus.normalize(request.status()); } catch (IllegalArgumentException ex) { throw new BusinessException("商品狀態不正確"); }
        int reserved = request.reservedStock() == null ? 0 : request.reservedStock();
        if (reserved > request.stock()) throw new BusinessException("保留庫存不可大於總庫存");
        product.setProductCode(code); product.setProductName(request.productName().trim()); product.setBrandEntity(brandEntity);
        product.setBrand(brandEntity == null ? request.brand().trim() : brandEntity.getName()); product.setCategory(category);
        product.setModel(request.model().trim()); product.setStorageCapacity(request.storageCapacity()); product.setColor(request.color());
        product.setConditionLevel(condition); product.setPrice(request.price()); product.setCost(request.cost() == null ? BigDecimal.ZERO : request.cost());
        product.setStock(request.stock()); product.setReservedStock(reserved); product.setDescription(request.description()); product.setImageUrl(request.imageUrl());
        product.setStatus(request.stock() == 0 && ProductStatus.ACTIVE.name().equals(status) ? ProductStatus.OUT_OF_STOCK.name() : status);
        product.setFeatured(request.featured());
    }

    public ProductResponse map(Product product) {
        Brand b = product.getBrandEntity(); Category c = product.getCategory();
        List<ProductImageResponse> imageResponses = product.getId() == null ? List.of() : images.findByProductIdOrderBySortOrderAscIdAsc(product.getId()).stream()
                .map(i -> new ProductImageResponse(i.getId(), i.getImageUrl(), i.getAltText(), i.isPrimaryImage(), i.getSortOrder())).toList();
        String mainImage = imageResponses.stream().filter(ProductImageResponse::primaryImage).map(ProductImageResponse::imageUrl).findFirst().orElse(product.getImageUrl());
        return new ProductResponse(product.getId(), product.getProductCode(), product.getProductName(), b == null ? null : b.getId(),
                b == null ? product.getBrand() : b.getName(), c == null ? null : c.getId(), c == null ? null : c.getName(),
                product.getModel(), product.getStorageCapacity(), product.getColor(), product.getConditionLevel(), product.getCost(), product.getPrice(),
                product.getStock() == null ? 0 : product.getStock(), product.getReservedStock() == null ? 0 : product.getReservedStock(), product.getAvailableStock(),
                product.getDescription(), mainImage, product.getStatus(), product.isFeatured(), imageResponses, product.getCreatedAt(), product.getUpdatedAt());
    }
    private boolean hasText(String value) { return value != null && !value.isBlank(); }
}
