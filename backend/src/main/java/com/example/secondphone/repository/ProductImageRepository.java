package com.example.secondphone.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.secondphone.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdOrderBySortOrderAscIdAsc(Long productId);
    void deleteByProductId(Long productId);
}
