package com.example.secondphone.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.secondphone.dto.MemberResponse;
import com.example.secondphone.dto.ProfileUpdateRequest;
import com.example.secondphone.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/member/profile")
public class MemberProfileController {

    private final AuthService authService;

    public MemberProfileController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    MemberResponse get(Authentication authentication) {
        return MemberResponse.from(authService.current(authentication.getName()));
    }

    @PutMapping
    MemberResponse update(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return authService.updateProfile(authentication.getName(), request);
    }
}
