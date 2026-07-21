package com.example.secondphone.dto;
import com.example.secondphone.entity.*;import jakarta.validation.constraints.*;
public record AdminMemberRequest(@NotBlank @Size(max=50)String username,@Size(min=8,max=72)String password,@NotBlank @Size(max=100)String name,@Email @Size(max=100)String email,@Size(max=20)String phone,@NotNull MemberRole role,@NotNull MemberStatus status){}
