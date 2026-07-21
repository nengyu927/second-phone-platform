package com.example.secondphone.dto;
import java.math.BigDecimal;import java.util.List;
public record CartResponse(Long id,List<CartItemResponse> items,Integer itemCount,BigDecimal totalAmount){}
