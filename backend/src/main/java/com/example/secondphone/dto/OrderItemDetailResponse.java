package com.example.secondphone.dto;
import java.math.BigDecimal;
public record OrderItemDetailResponse(Long id,Long productId,String productCode,String productName,BigDecimal unitPrice,Integer quantity,BigDecimal subtotal){}
