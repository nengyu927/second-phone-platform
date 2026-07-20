package com.example.secondphone.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.secondphone.entity.Member;
import com.example.secondphone.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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
        return memberRepository.save(member);
    }

    public Member update(Long id, Member member) {
        Member existingMember = findById(id);
        if (memberRepository.existsByAccountAndIdNot(member.getAccount(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在");
        }

        existingMember.setAccount(member.getAccount());
        existingMember.setPassword(member.getPassword());
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
}
