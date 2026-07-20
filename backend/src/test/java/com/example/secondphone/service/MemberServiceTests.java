package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.secondphone.entity.Member;
import com.example.secondphone.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTests {

    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository);
    }

    @Test
    void createMemberSuccessfully() {
        Member member = member("user01", "王小明");
        when(memberRepository.existsByAccount("user01")).thenReturn(false);
        when(memberRepository.save(member)).thenReturn(member);

        assertSame(member, memberService.create(member));
        verify(memberRepository).save(member);
    }

    @Test
    void findMissingMemberReturnsNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> memberService.findById(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void duplicateAccountReturnsConflict() {
        Member member = member("user01", "王小明");
        when(memberRepository.existsByAccount("user01")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> memberService.create(member));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void updateMemberSuccessfullyAndKeepsIdentityAndCreatedTime() {
        Member existing = member("oldAccount", "舊名稱");
        existing.setId(1L);
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 20, 10, 0);
        existing.setCreatedAt(createdAt);
        Member changes = member("newAccount", "新名稱");
        changes.setEmail("new@example.com");
        changes.setPhone("0912345678");
        changes.setRole("ADMIN");
        changes.setStatus("INACTIVE");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(memberRepository.existsByAccountAndIdNot("newAccount", 1L)).thenReturn(false);
        when(memberRepository.save(existing)).thenReturn(existing);

        Member updated = memberService.update(1L, changes);

        assertEquals(1L, updated.getId());
        assertEquals(createdAt, updated.getCreatedAt());
        assertEquals("newAccount", updated.getAccount());
        assertEquals("新名稱", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("0912345678", updated.getPhone());
        assertEquals("ADMIN", updated.getRole());
        assertEquals("INACTIVE", updated.getStatus());
    }

    @Test
    void deleteMemberSuccessfully() {
        Member existing = member("user01", "王小明");
        existing.setId(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(existing));

        memberService.delete(1L);

        verify(memberRepository).delete(existing);
    }

    @Test
    void searchMembersByAccountOrName() {
        Member matching = member("user01", "王小明");
        when(memberRepository.findByAccountContainingIgnoreCaseOrNameContainingIgnoreCase("王", "王"))
                .thenReturn(List.of(matching));

        assertEquals(List.of(matching), memberService.search(" 王 "));
    }

    private Member member(String account, String name) {
        Member member = new Member();
        member.setAccount(account);
        member.setPassword("password123");
        member.setName(name);
        member.setRole("USER");
        member.setStatus("ACTIVE");
        return member;
    }
}
