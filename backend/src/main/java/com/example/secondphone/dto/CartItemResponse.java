package com.example.secondphone.dto;
import java.math.BigDecimal;
public record CartItemResponse(Long id,Long productId,String productCode,String productName,String imageUrl,String brand,String model,String storageCapacity,Integer quantity,BigDecimal unitPrice,BigDecimal subtotal,Integer availableStock){}
