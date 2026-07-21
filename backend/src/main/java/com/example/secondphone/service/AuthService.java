package com.example.secondphone.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondphone.dto.AuthResponse;
import com.example.secondphone.dto.LoginRequest;
import com.example.secondphone.dto.MemberResponse;
import com.example.secondphone.dto.ProfileUpdateRequest;
import com.example.secondphone.dto.RegisterRequest;
import com.example.secondphone.entity.Member;
import com.example.secondphone.entity.MemberRole;
import com.example.secondphone.entity.MemberStatus;
import com.example.secondphone.exception.DuplicateResourceException;
import com.example.secondphone.exception.ResourceNotFoundException;
import com.example.secondphone.repository.MemberRepository;
import com.example.secondphone.security.JwtTokenProvider;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public MemberResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();
        if (memberRepository.existsByAccount(username)) {
            throw new DuplicateResourceException("使用者名稱已存在");
        }
        if (memberRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("電子信箱已註冊");
        }

        Member member = new Member();
        member.setAccount(username);
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(request.password()));
        member.setName(request.name().trim());
        member.setPhone(normalizeNullable(request.phone()));
        member.setRole(MemberRole.CUSTOMER);
        member.setStatus(MemberStatus.ACTIVE);
        return MemberResponse.from(memberRepository.save(member));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.account(), request.password()));
        Member member = current(authentication.getName());
        String token = tokenProvider.createToken(member.getAccount());
        return new AuthResponse(token, "Bearer", tokenProvider.getExpirationSeconds(), MemberResponse.from(member));
    }

    @Transactional(readOnly = true)
    public Member current(String username) {
        return memberRepository.findByAccountIgnoreCaseOrEmailIgnoreCase(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("會員不存在"));
    }

    @Transactional
    public MemberResponse updateProfile(String username, ProfileUpdateRequest request) {
        Member member = current(username);
        member.setName(request.name().trim());
        member.setPhone(normalizeNullable(request.phone()));
        return MemberResponse.from(memberRepository.save(member));
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
