package com.example.secondphone.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.example.secondphone.dto.AdminMemberRequest;
import com.example.secondphone.dto.MemberResponse;
import com.example.secondphone.entity.Member;
import com.example.secondphone.exception.BusinessException;
import com.example.secondphone.service.MemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    ResponseEntity<List<MemberResponse>> findAll(
            @RequestParam(name = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(memberService.search(keyword).stream().map(MemberResponse::from).toList());
    }

    @GetMapping("/{id}")
    ResponseEntity<MemberResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(MemberResponse.from(memberService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<MemberResponse> create(@Valid @RequestBody AdminMemberRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new BusinessException("新增會員時必須設定密碼");
        }
        Member created = memberService.create(toMember(request));
        return ResponseEntity.created(URI.create("/api/members/" + created.getId()))
                .body(MemberResponse.from(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<MemberResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminMemberRequest request) {
        return ResponseEntity.ok(MemberResponse.from(memberService.update(id, toMember(request))));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Member toMember(AdminMemberRequest request) {
        Member member = new Member();
        member.setAccount(request.username().trim());
        member.setPassword(request.password());
        member.setName(request.name().trim());
        member.setEmail(normalizeNullable(request.email()));
        member.setPhone(normalizeNullable(request.phone()));
        member.setRole(request.role());
        member.setStatus(request.status());
        return member;
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
