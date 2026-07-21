package com.example.secondphone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BrandRequest(@NotBlank(message = "品牌名稱為必填") @Size(max = 80) String name, boolean active) {}
