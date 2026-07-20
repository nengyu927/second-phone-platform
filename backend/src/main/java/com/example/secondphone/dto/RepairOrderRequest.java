package com.example.secondphone.dto;
import java.math.BigDecimal;import java.time.LocalDate;import io.swagger.v3.oas.annotations.media.Schema;import jakarta.validation.constraints.*;
@Schema(description="建立或修改維修單的資料")
public record RepairOrderRequest(
 @Schema(description="會員 ID",example="1") @NotNull Long memberId,
 @Schema(description="裝置品牌",example="Apple") @NotBlank String deviceBrand,
 @Schema(description="裝置型號",example="iPhone 15") @NotBlank String deviceModel,
 @Schema(description="裝置 IMEI") String imei,
 @Schema(description="問題描述",example="無法充電") @NotBlank String problemDescription,
 @Schema(description="維修狀態",example="RECEIVED") String repairStatus,
 @Schema(description="預估費用，不可為負數",example="1000") @DecimalMin("0.0") BigDecimal estimatedCost,
 @Schema(description="最終費用，不可為負數",example="1200") @DecimalMin("0.0") BigDecimal finalCost,
 @Schema(description="收件日期；未提供時使用今天") LocalDate receivedDate,
 @Schema(description="預計完成日期") LocalDate expectedCompletionDate,
 @Schema(description="完成日期；COMPLETED 且未提供時自動填今天") LocalDate completedDate,
 @Schema(description="技師姓名") String technicianName,
 @Schema(description="維修備註") String repairNotes){}
