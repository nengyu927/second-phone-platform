package com.example.secondphone.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.secondphone.dto.ProductCsvImportResult;
import com.example.secondphone.service.ProductCsvImportService;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public class AdminProductImportController {
    private final ProductCsvImportService service;

    public AdminProductImportController(ProductCsvImportService service) {
        this.service = service;
    }

    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductCsvImportResult importCsv(
            @RequestParam MultipartFile file,
            @RequestParam Long categoryId,
            @RequestParam Long brandId) {
        return service.importCsv(file, categoryId, brandId);
    }
}
