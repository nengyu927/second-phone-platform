package com.example.secondphone.dto;
import java.math.BigDecimal;import java.time.LocalDateTime;
public record OrderResponse(Long id,Long memberId,String memberName,Long productId,String productName,Integer quantity,BigDecimal unitPrice,BigDecimal totalAmount,String orderStatus,String recipientName,String recipientPhone,String shippingAddress,String note,LocalDateTime createdAt,LocalDateTime updatedAt){}
