package com.example.secondphone.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.secondphone.dto.*;
import com.example.secondphone.entity.Brand;
import com.example.secondphone.exception.DuplicateResourceException;
import com.example.secondphone.exception.ResourceNotFoundException;
import com.example.secondphone.repository.BrandRepository;

@Service
public class BrandService {
    private final BrandRepository repository;
    public BrandService(BrandRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAll(boolean activeOnly) {
        return repository.findAll().stream().filter(b -> !activeOnly || b.isActive()).map(this::map).toList();
    }

    @Transactional
    public BrandResponse create(BrandRequest request) {
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCase(name)) throw new DuplicateResourceException("品牌名稱已存在");
        Brand brand = new Brand();
        brand.setName(name); brand.setSlug(slug(name)); brand.setActive(request.active());
        return map(repository.save(brand));
    }

    @Transactional
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = getEntity(id);
        String name = request.name().trim();
        repository.findByNameIgnoreCase(name).filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new DuplicateResourceException("品牌名稱已存在"); });
        brand.setName(name); brand.setSlug(slug(name)); brand.setActive(request.active());
        return map(repository.save(brand));
    }

    @Transactional(readOnly = true)
    public Brand getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("找不到指定品牌"));
    }
    @Transactional public void deactivate(Long id) { Brand brand = getEntity(id); brand.setActive(false); repository.save(brand); }

    private BrandResponse map(Brand b) { return new BrandResponse(b.getId(), b.getName(), b.getSlug(), b.isActive(), b.getCreatedAt(), b.getUpdatedAt()); }
    static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String result = normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\p{IsHan}]+", "-").replaceAll("(^-|-$)", "");
        return result.isBlank() ? "item-" + Integer.toUnsignedString(value.hashCode()) : result;
    }
}
