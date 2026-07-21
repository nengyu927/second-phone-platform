package com.example.secondphone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.example.secondphone.entity.Member;
import com.example.secondphone.entity.MemberRole;

public interface MemberRepository extends JpaRepository<Member, Long> {

    java.util.List<Member> findTop5ByOrderByCreatedAtDesc();

    boolean existsByAccount(String account);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Optional<Member> findByAccountIgnoreCaseOrEmailIgnoreCase(String account, String email);

    Optional<Member> findByEmailIgnoreCase(String email);

    boolean existsByRole(MemberRole role);

    long countByRole(MemberRole role);

    boolean existsByAccountAndIdNot(String account, Long id);

    List<Member> findByAccountContainingIgnoreCaseOrNameContainingIgnoreCase(String account, String name);
}
