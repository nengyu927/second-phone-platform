package com.example.secondphone.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.secondphone.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Brand> findByNameIgnoreCase(String name);
}
