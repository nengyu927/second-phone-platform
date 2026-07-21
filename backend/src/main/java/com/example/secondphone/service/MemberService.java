package com.example.secondphone.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.example.secondphone.entity.Member;
import com.example.secondphone.entity.MemberRole;
import com.example.secondphone.entity.MemberStatus;
import com.example.secondphone.exception.BusinessException;
import com.example.secondphone.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository) {
        this(memberRepository, new BCryptPasswordEncoder());
    }

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public List<Member> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String value = keyword.trim();
        return memberRepository.findByAccountContainingIgnoreCaseOrNameContainingIgnoreCase(value, value);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "會員不存在"));
    }

    public Member create(Member member) {
        if (memberRepository.existsByAccount(member.getAccount())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在");
        }
        if (member.getEmail() != null && memberRepository.existsByEmailIgnoreCase(member.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "電子信箱已存在");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    public Member update(Long id, Member member) {
        Member existingMember = findById(id);
        if (memberRepository.existsByAccountAndIdNot(member.getAccount(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在");
        }
        if (member.getEmail() != null && memberRepository.existsByEmailIgnoreCaseAndIdNot(member.getEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "電子信箱已存在");
        }

        existingMember.setAccount(member.getAccount());
        if (member.getPassword() != null && !member.getPassword().isBlank()) {
            existingMember.setPassword(passwordEncoder.encode(member.getPassword()));
        }
        existingMember.setName(member.getName());
        existingMember.setEmail(member.getEmail());
        existingMember.setPhone(member.getPhone());
        existingMember.setRole(member.getRole());
        existingMember.setStatus(member.getStatus());
        return memberRepository.save(existingMember);
    }

    public void delete(Long id) {
        Member member = findById(id);
        memberRepository.delete(member);
    }

    public Member updateStatus(Long id, String value) {
        Member member = findById(id);
        try {
            member.setStatus(MemberStatus.valueOf(value.trim().toUpperCase()));
        } catch (RuntimeException exception) {
            throw new BusinessException("會員狀態僅能為 ACTIVE 或 DISABLED");
        }
        return memberRepository.save(member);
    }

    public Member updateRole(Long id, String value) {
        Member member = findById(id);
        MemberRole next;
        try {
            next = MemberRole.valueOf(value.trim().toUpperCase());
        } catch (RuntimeException exception) {
            throw new BusinessException("會員角色僅能為 CUSTOMER、STAFF 或 ADMIN");
        }
        if (member.getRoleEnum() == MemberRole.ADMIN && next != MemberRole.ADMIN
                && memberRepository.countByRole(MemberRole.ADMIN) <= 1) {
            throw new BusinessException("不可將最後一位管理員降級");
        }
        member.setRole(next);
        return memberRepository.save(member);
    }
}
