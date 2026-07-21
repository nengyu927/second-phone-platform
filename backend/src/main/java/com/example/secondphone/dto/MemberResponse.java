package com.example.secondphone.dto;

import java.time.LocalDateTime;
import com.example.secondphone.entity.Member;
import com.example.secondphone.entity.MemberRole;
import com.example.secondphone.entity.MemberStatus;

public record MemberResponse(Long id, String username, String account, String email, String name, String phone,
        MemberRole role, MemberStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getAccount(), member.getAccount(), member.getEmail(), member.getName(),
                member.getPhone(), member.getRoleEnum(), member.getStatusEnum(), member.getCreatedAt(), member.getUpdatedAt());
    }
}
