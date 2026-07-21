package com.example.secondphone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.example.secondphone.entity.InventoryMovementType;

public record InventoryAdjustmentRequest(
        @NotNull(message = "異動類型為必填") InventoryMovementType type,
        @NotNull(message = "異動數量為必填") Integer quantity,
        @NotBlank(message = "異動原因為必填") String reason) {}
