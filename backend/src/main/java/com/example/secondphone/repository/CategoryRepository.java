package com.example.secondphone.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.secondphone.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Category> findByNameIgnoreCase(String name);
}
