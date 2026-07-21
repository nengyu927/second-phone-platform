package com.example.secondphone.dto;
import jakarta.validation.constraints.*;
public record UpdateCartItemRequest(@NotNull(message="數量為必填") @Min(value=1,message="數量至少為 1") Integer quantity){}
