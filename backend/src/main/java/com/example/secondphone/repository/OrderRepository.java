package com.example.secondphone.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import com.example.secondphone.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findTop5ByOrderByCreatedAtDesc();
    long countByOrderStatus(String orderStatus);
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CustomerOrder o WHERE o.orderStatus <> :status")
    BigDecimal sumTotalAmountByOrderStatusNot(@Param("status") String status);
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CustomerOrder o WHERE o.orderStatus = :status")
    BigDecimal sumTotalAmountByOrderStatus(@Param("status") String status);
    List<Order> findByMemberId(Long memberId);
    List<Order> findByOrderStatus(String orderStatus);
    List<Order> findByProductId(Long productId);
}
