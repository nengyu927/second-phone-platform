package com.example.secondphone.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.secondphone.entity.Order;

public interface OrderRepository extends JpaRepository<Order,Long>{
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findTop5ByOrderByCreatedAtDesc();
    List<Order> findByMember_IdOrderByCreatedAtDesc(Long memberId);
    List<Order> findByProduct_IdOrderByCreatedAtDesc(Long productId);
    List<Order> findByOrderStatusOrderByCreatedAtDesc(String orderStatus);
    long countByOrderStatus(String orderStatus);
    @Query("SELECT COALESCE(SUM(o.totalAmount),0) FROM CustomerOrder o WHERE o.orderStatus <> :status")
    BigDecimal sumTotalAmountByOrderStatusNot(@Param("status") String status);
    @Query("SELECT COALESCE(SUM(o.totalAmount),0) FROM CustomerOrder o WHERE o.orderStatus = :status")
    BigDecimal sumTotalAmountByOrderStatus(@Param("status") String status);
}
