package com.example.secondphone.dto;
import java.math.BigDecimal;import java.time.LocalDateTime;
public record TradeInResponse(Long id,String tradeInNumber,Long memberId,String memberName,String customerName,String customerPhone,String brand,String model,String storageCapacity,String color,String imei,String appearanceCondition,String functionCondition,String description,BigDecimal expectedPrice,BigDecimal quotedPrice,String status,String rejectionReason,LocalDateTime createdAt,LocalDateTime updatedAt){}
