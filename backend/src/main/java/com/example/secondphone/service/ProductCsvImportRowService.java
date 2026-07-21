package com.example.secondphone.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondphone.entity.Brand;
import com.example.secondphone.entity.Category;
import com.example.secondphone.entity.Product;
import com.example.secondphone.entity.ProductStatus;
import com.example.secondphone.repository.ProductRepository;

@Service
public class ProductCsvImportRowService {
    public enum ImportOutcome { CREATED, SKIPPED }

    private final ProductRepository products;

    public ProductCsvImportRowService(ProductRepository products) {
        this.products = products;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportOutcome importRow(String name, BigDecimal price, String description, Brand brand, Category category) {
        if (products.existsByProductNameIgnoreCaseAndPrice(name, price)) return ImportOutcome.SKIPPED;

        Product product = new Product();
        product.setProductCode("SP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        product.setProductName(name);
        product.setBrandEntity(brand);
        product.setBrand(brand.getName());
        product.setCategory(category);
        product.setModel(name);
        product.setConditionLevel("GOOD");
        product.setPrice(price);
        product.setCost(BigDecimal.ZERO);
        product.setStock(1);
        product.setReservedStock(0);
        product.setDescription(description);
        product.setStatus(ProductStatus.ACTIVE.name());
        product.setFeatured(false);
        products.saveAndFlush(product);
        return ImportOutcome.CREATED;
    }
}
