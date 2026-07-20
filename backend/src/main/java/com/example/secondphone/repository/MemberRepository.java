package com.example.secondphone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.example.secondphone.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    java.util.List<Member> findTop5ByOrderByCreatedAtDesc();

    boolean existsByAccount(String account);

    boolean existsByAccountAndIdNot(String account, Long id);

    List<Member> findByAccountContainingIgnoreCaseOrNameContainingIgnoreCase(String account, String name);
}
