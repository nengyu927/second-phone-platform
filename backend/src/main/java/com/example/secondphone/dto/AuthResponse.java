package com.example.secondphone.dto;

public record AuthResponse(String token, String tokenType, long expiresIn, MemberResponse user) {
}
