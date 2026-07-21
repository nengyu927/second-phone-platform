package com.example.secondphone.repository;
import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;import com.example.secondphone.entity.TradeIn;
public interface TradeInRepository extends JpaRepository<TradeIn,Long>{List<TradeIn> findAllByOrderByCreatedAtDesc();List<TradeIn> findByMemberIdOrderByCreatedAtDesc(Long memberId);Optional<TradeIn> findByIdAndMemberId(Long id,Long memberId);boolean existsByTradeInNumber(String number);long countByStatus(String status);}
