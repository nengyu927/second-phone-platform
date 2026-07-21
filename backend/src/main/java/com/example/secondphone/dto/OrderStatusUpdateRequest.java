package com.example.secondphone.dto;
import jakarta.validation.constraints.NotBlank;
public record OrderStatusUpdateRequest(@NotBlank(message="狀態為必填") String status){}
