package com.example.secondphone.dto;
import jakarta.validation.constraints.*;
public record AddCartItemRequest(@NotNull(message="商品為必填") Long productId,@NotNull(message="數量為必填") @Min(value=1,message="數量至少為 1") Integer quantity){}
