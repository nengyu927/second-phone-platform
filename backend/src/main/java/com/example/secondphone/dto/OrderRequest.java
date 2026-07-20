package com.example.secondphone.dto;
import io.swagger.v3.oas.annotations.media.Schema;import jakarta.validation.constraints.*;
@Schema(description="建立或修改單一商品訂單的資料；價格與總金額由後端決定")
public record OrderRequest(
 @Schema(description="會員 ID",example="1") @NotNull Long memberId,
 @Schema(description="商品 ID",example="2") @NotNull Long productId,
 @Schema(description="購買數量，至少 1",example="1") @NotNull @Min(1) Integer quantity,
 @Schema(description="訂單狀態",example="PENDING") String orderStatus,
 @Schema(description="收件人姓名",example="王小明") @NotBlank String recipientName,
 @Schema(description="收件人電話",example="0912345678") @NotBlank String recipientPhone,
 @Schema(description="收件地址",example="台北市中正區") @NotBlank String shippingAddress,
 @Schema(description="訂單備註") String note){}
