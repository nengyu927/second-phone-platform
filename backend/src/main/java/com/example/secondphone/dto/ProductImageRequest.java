package com.example.secondphone.dto;

import jakarta.validation.constraints.*;

public record ProductImageRequest(
        @NotBlank(message = "圖片網址為必填") @Size(max = 500) String imageUrl,
        @Size(max = 160) String altText,
        boolean primaryImage,
        @Min(0) int sortOrder) {}
