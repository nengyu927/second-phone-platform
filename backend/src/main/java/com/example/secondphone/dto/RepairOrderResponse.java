package com.example.secondphone.dto;
import java.math.BigDecimal;import java.time.*;
public record RepairOrderResponse(Long id,Long memberId,String memberName,String deviceBrand,String deviceModel,String imei,String problemDescription,String repairStatus,BigDecimal estimatedCost,BigDecimal finalCost,LocalDate receivedDate,LocalDate expectedCompletionDate,LocalDate completedDate,String technicianName,String repairNotes,LocalDateTime createdAt,LocalDateTime updatedAt){}
