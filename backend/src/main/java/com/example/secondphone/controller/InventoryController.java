package com.example.secondphone.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.dto.*;
import com.example.secondphone.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/admin/inventory") @Tag(name = "庫存", description = "商品庫存調整與異動紀錄")
public class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service) { this.service = service; }
    @GetMapping("/movements") public PagedResponse<InventoryMovementResponse> list(@RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) { return service.findAll(productId, page, size); }
    @PostMapping("/products/{productId}/adjustments") public InventoryMovementResponse adjust(@PathVariable Long productId,
            @Valid @RequestBody InventoryAdjustmentRequest request, Authentication authentication) { return service.adjust(productId, request, authentication); }
}
