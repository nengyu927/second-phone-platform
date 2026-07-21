package com.example.secondphone.dto;

public record ProductImageResponse(Long id, String imageUrl, String altText, boolean primaryImage, int sortOrder) {}
