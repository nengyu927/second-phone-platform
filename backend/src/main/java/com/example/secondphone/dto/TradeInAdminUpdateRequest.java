package com.example.secondphone.dto;
import java.math.BigDecimal;import jakarta.validation.constraints.*;
public record TradeInAdminUpdateRequest(@NotBlank String status,@DecimalMin("0") BigDecimal quotedPrice,@Size(max=1000) String rejectionReason){}
