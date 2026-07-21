package com.example.secondphone.dto;
import jakarta.validation.constraints.*;
public record CheckoutRequest(@NotBlank(message="收件人為必填") @Size(max=100) String recipientName,@NotBlank(message="電話為必填") @Size(max=20) String recipientPhone,@NotBlank(message="地址為必填") @Size(max=300) String recipientAddress,@NotBlank(message="付款方式為必填") String paymentMethod,@Size(max=1000) String note){}
