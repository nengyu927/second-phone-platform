package com.example.secondphone.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.secondphone.dto.*;
import com.example.secondphone.entity.Category;
import com.example.secondphone.exception.DuplicateResourceException;
import com.example.secondphone.exception.ResourceNotFoundException;
import com.example.secondphone.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    public CategoryService(CategoryRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true) public List<CategoryResponse> findAll(boolean activeOnly) {
        return repository.findAll().stream().filter(c -> !activeOnly || c.isActive()).map(this::map).toList();
    }
    @Transactional public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCase(name)) throw new DuplicateResourceException("分類名稱已存在");
        Category category = new Category(); category.setName(name); category.setSlug(BrandService.slug(name)); category.setActive(request.active());
        return map(repository.save(category));
    }
    @Transactional public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getEntity(id); String name = request.name().trim();
        repository.findByNameIgnoreCase(name).filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new DuplicateResourceException("分類名稱已存在"); });
        category.setName(name); category.setSlug(BrandService.slug(name)); category.setActive(request.active());
        return map(repository.save(category));
    }
    @Transactional(readOnly = true) public Category getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("找不到指定分類"));
    }
    @Transactional public void deactivate(Long id) { Category category = getEntity(id); category.setActive(false); repository.save(category); }
    private CategoryResponse map(Category c) { return new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.isActive(), c.getCreatedAt(), c.getUpdatedAt()); }
}
