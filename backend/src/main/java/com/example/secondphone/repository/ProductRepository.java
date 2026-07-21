package com.example.secondphone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.secondphone.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsByProductCode(String productCode);

    List<Product> findTop5ByOrderByCreatedAtDesc();

    long countByStockLessThanEqual(Integer stock);

    List<Product> findByBrandContainingIgnoreCase(String brand);

    List<Product> findByProductNameContainingIgnoreCase(String productName);

    List<Product> findByStatus(String status);
}
