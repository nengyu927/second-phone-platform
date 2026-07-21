package com.example.secondphone.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.dto.*;
import com.example.secondphone.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api") @Tag(name = "分類", description = "分類公開查詢與後台維護")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }
    @GetMapping("/categories") public List<CategoryResponse> publicList() { return service.findAll(true); }
    @GetMapping("/admin/categories") public List<CategoryResponse> adminList() { return service.findAll(false); }
    @PostMapping("/admin/categories") public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = service.create(request); return ResponseEntity.created(URI.create("/api/admin/categories/" + response.id())).body(response);
    }
    @PutMapping("/admin/categories/{id}") public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) { return service.update(id, request); }
    @DeleteMapping("/admin/categories/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.deactivate(id); return ResponseEntity.noContent().build(); }
}
