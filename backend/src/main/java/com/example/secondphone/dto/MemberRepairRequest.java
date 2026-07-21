package com.example.secondphone.dto;
import java.time.LocalDate;import jakarta.validation.constraints.*;
public record MemberRepairRequest(@NotBlank @Size(max=60) String brand,@NotBlank @Size(max=120) String model,@Size(max=30) String imei,@NotBlank @Size(max=3000) String problemDescription,LocalDate appointmentDate){}
