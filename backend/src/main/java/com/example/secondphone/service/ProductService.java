package com.example.secondphone.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.secondphone.entity.Product;
import com.example.secondphone.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "商品不存在"));
    }

    public Product create(Product product) {
        validatePriceAndStock(product);
        return productRepository.save(product);
    }

    public Product update(Long id, Product product) {
        validatePriceAndStock(product);
        Product existingProduct = findById(id);
        existingProduct.setProductName(product.getProductName());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setModel(product.getModel());
        existingProduct.setStorageCapacity(product.getStorageCapacity());
        existingProduct.setColor(product.getColor());
        existingProduct.setConditionLevel(product.getConditionLevel());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setStatus(product.getStatus());
        return productRepository.save(existingProduct);
    }

    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    public List<Product> search(String keyword, String brand, String status) {
        List<Product> products;
        if (hasText(keyword)) {
            products = productRepository.findByProductNameContainingIgnoreCase(keyword.trim());
        } else if (hasText(brand)) {
            products = productRepository.findByBrandContainingIgnoreCase(brand.trim());
        } else if (hasText(status)) {
            products = productRepository.findByStatus(status.trim());
        } else {
            products = findAll();
        }

        return products.stream()
                .filter(product -> !hasText(keyword)
                        || containsIgnoreCase(product.getProductName(), keyword.trim()))
                .filter(product -> !hasText(brand)
                        || containsIgnoreCase(product.getBrand(), brand.trim()))
                .filter(product -> !hasText(status)
                        || status.trim().equalsIgnoreCase(product.getStatus()))
                .toList();
    }

    private void validatePriceAndStock(Product product) {
        if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "售價不可小於 0");
        }
        if (product.getStock() != null && product.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "庫存不可小於 0");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword.toLowerCase());
    }
}
