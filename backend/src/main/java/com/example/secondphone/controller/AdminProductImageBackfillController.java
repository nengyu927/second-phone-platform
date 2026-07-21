package com.example.secondphone.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.secondphone.dto.ProductImageBackfillApplyRequest;
import com.example.secondphone.dto.ProductImageBackfillApplyResult;
import com.example.secondphone.dto.ProductImageBackfillPreviewResult;
import com.example.secondphone.service.ProductImageBackfillApplyService;
import com.example.secondphone.service.ProductImageBackfillPreviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/products/image-backfill")
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public class AdminProductImageBackfillController {
    private final ProductImageBackfillPreviewService service;
    private final ProductImageBackfillApplyService applyService;

    public AdminProductImageBackfillController(ProductImageBackfillPreviewService service,
            ProductImageBackfillApplyService applyService) {
        this.service = service;
        this.applyService = applyService;
    }

    @GetMapping("/preview")
    public ProductImageBackfillPreviewResult preview() {
        return service.preview();
    }

    @PostMapping("/apply")
    public ProductImageBackfillApplyResult apply(@Valid @RequestBody ProductImageBackfillApplyRequest request) {
        return applyService.apply(request.confirmedModels());
    }
}
