package com.example.secondphone.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ProductImageBackfillApplyRequest(
        @NotEmpty @Size(max = 100) List<@NotBlank @Size(max = 100) String> confirmedModels) {
}
