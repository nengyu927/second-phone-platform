package com.example.secondphone.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.dto.*;
import com.example.secondphone.service.BrandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api") @Tag(name = "品牌", description = "品牌公開查詢與後台維護")
public class BrandController {
    private final BrandService service;
    public BrandController(BrandService service) { this.service = service; }
    @GetMapping("/brands") public List<BrandResponse> publicList() { return service.findAll(true); }
    @GetMapping("/admin/brands") public List<BrandResponse> adminList() { return service.findAll(false); }
    @PostMapping("/admin/brands") public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
        BrandResponse response = service.create(request); return ResponseEntity.created(URI.create("/api/admin/brands/" + response.id())).body(response);
    }
    @PutMapping("/admin/brands/{id}") public BrandResponse update(@PathVariable Long id, @Valid @RequestBody BrandRequest request) { return service.update(id, request); }
    @DeleteMapping("/admin/brands/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.deactivate(id); return ResponseEntity.noContent().build(); }
}
