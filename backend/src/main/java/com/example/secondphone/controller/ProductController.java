package com.example.secondphone.controller;

import java.math.BigDecimal;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.dto.*;
import com.example.secondphone.service.ProductCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(name = "商品", description = "公開商品查詢與後台商品管理")
public class ProductController {
    private final ProductCatalogService service;
    public ProductController(ProductCatalogService service) { this.service = service; }

    @GetMapping @Operation(summary = "分頁查詢商品")
    public PagedResponse<ProductResponse> search(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long brandId, @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String condition, @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice, @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock, @RequestParam(required = false) Boolean featured,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "desc") String direction) {
        return service.search(keyword, brandId, categoryId, condition, status, minPrice, maxPrice, inStock, featured, page, size, sort, direction);
    }
    @GetMapping("/{id}") @Operation(summary = "查詢商品詳情")
    public ProductResponse findById(@PathVariable Long id) { return service.findResponse(id); }
    @PostMapping @Operation(summary = "新增商品")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = service.create(request); return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }
    @PutMapping("/{id}") @Operation(summary = "修改商品")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @Operation(summary = "刪除商品")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
