package com.example.secondphone.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.secondphone.entity.CartItem;
public interface CartItemRepository extends JpaRepository<CartItem,Long>{Optional<CartItem> findByCartIdAndProductId(Long cartId,Long productId);Optional<CartItem> findByIdAndCartId(Long id,Long cartId);}
