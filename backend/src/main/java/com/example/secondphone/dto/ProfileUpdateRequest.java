package com.example.secondphone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(@NotBlank @Size(max = 100) String name, @Size(max = 20) String phone) {
}
