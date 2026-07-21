package com.example.secondphone.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.secondphone.entity.Cart;
public interface CartRepository extends JpaRepository<Cart,Long>{Optional<Cart> findByMemberId(Long memberId);}
