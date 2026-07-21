package com.example.secondphone.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.example.secondphone.dto.*;
import com.example.secondphone.service.ProductImageService;
import com.example.secondphone.service.ProductImageStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/admin/products/{productId}/images") @Tag(name = "商品圖片", description = "商品多圖與主圖維護")
public class ProductImageController {
    private final ProductImageService service;
    private final ProductImageStorageService storage;
    public ProductImageController(ProductImageService service, ProductImageStorageService storage) { this.service = service; this.storage = storage; }
    @PostMapping public ResponseEntity<ProductImageResponse> add(@PathVariable Long productId, @Valid @RequestBody ProductImageRequest request) {
        ProductImageResponse response = service.add(productId, request); return ResponseEntity.created(URI.create("/api/admin/products/" + productId + "/images/" + response.id())).body(response);
    }
    @DeleteMapping("/{imageId}") public ResponseEntity<Void> delete(@PathVariable Long productId, @PathVariable Long imageId) {
        service.delete(productId, imageId); return ResponseEntity.noContent().build();
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImageResponse> upload(@PathVariable Long productId, @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean primaryImage, @RequestParam(defaultValue = "0") int sortOrder) {
        String imageUrl = storage.store(file);
        ProductImageResponse response = service.add(productId, new ProductImageRequest(imageUrl, file.getOriginalFilename(), primaryImage, sortOrder));
        return ResponseEntity.created(URI.create("/api/admin/products/" + productId + "/images/" + response.id())).body(response);
    }
}
