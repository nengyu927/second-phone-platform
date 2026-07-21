package com.example.secondphone.dto;
import java.math.BigDecimal;import java.time.LocalDateTime;import java.util.List;
public record OrderDetailResponse(Long id,String orderNumber,Long memberId,String memberName,String recipientName,String recipientPhone,String recipientAddress,BigDecimal subtotal,BigDecimal shippingFee,BigDecimal totalAmount,String orderStatus,String paymentStatus,String shippingStatus,String paymentMethod,String note,List<OrderItemDetailResponse> items,LocalDateTime createdAt,LocalDateTime updatedAt){}
