package com.example.secondphone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CategoryRequest(@NotBlank(message = "分類名稱為必填") @Size(max = 80) String name, boolean active) {}
