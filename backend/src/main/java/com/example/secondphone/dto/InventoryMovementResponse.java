package com.example.secondphone.dto;

import java.time.LocalDateTime;
import com.example.secondphone.entity.InventoryMovementType;

public record InventoryMovementResponse(Long id, Long productId, String productCode, String productName,
        InventoryMovementType type, int changeQuantity, int beforeStock, int afterStock,
        String reason, String operatorUsername, LocalDateTime createdAt) {}
