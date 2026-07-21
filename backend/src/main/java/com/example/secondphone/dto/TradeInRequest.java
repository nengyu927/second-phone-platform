package com.example.secondphone.dto;
import java.math.BigDecimal;import jakarta.validation.constraints.*;
public record TradeInRequest(@NotBlank @Size(max=100) String customerName,@NotBlank @Size(max=30) String customerPhone,@NotBlank @Size(max=60) String brand,@NotBlank @Size(max=120) String model,@Size(max=40) String storageCapacity,@Size(max=40) String color,@Size(max=30) String imei,@NotBlank @Size(max=40) String appearanceCondition,@NotBlank @Size(max=40) String functionCondition,@Size(max=2000) String description,@DecimalMin("0") BigDecimal expectedPrice){}
