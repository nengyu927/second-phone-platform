package com.example.secondphone.dto;

import java.time.LocalDateTime;
public record BrandResponse(Long id, String name, String slug, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {}
