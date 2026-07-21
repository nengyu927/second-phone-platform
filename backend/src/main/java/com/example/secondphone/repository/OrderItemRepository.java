package com.example.secondphone.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.secondphone.entity.OrderItem;
public interface OrderItemRepository extends JpaRepository<OrderItem,Long>{List<OrderItem> findByOrderIdOrderById(Long orderId);}
