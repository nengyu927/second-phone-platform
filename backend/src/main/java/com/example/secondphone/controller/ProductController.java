package com.example.secondphone.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.secondphone.entity.Product;
import com.example.secondphone.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "二手手機商品 CRUD 與搜尋")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "查詢或搜尋商品列表", description = "多個搜尋條件使用 AND 邏輯；未提供條件時回傳全部商品。")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<List<Product>> findAll(
            @Parameter(description = "商品名稱關鍵字") @RequestParam(required = false) String keyword,
            @Parameter(description = "品牌關鍵字") @RequestParam(required = false) String brand,
            @Parameter(description = "商品狀態") @RequestParam(required = false) String status) {
        return ResponseEntity.ok(productService.search(keyword, brand, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢單一商品")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    public ResponseEntity<Product> findById(
            @Parameter(description = "商品 ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @Operation(summary = "新增商品")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "輸入資料驗證失敗")
    })
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product createdProduct = productService.create(product);
        return ResponseEntity.created(URI.create("/api/products/" + createdProduct.getId())).body(createdProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改商品")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "400", description = "輸入資料驗證失敗"),
            @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    public ResponseEntity<Product> update(
            @Parameter(description = "商品 ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除商品")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "刪除成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "商品 ID", example = "1") @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
