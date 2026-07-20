package com.example.secondphone.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.secondphone.entity.RepairOrder;

public interface RepairOrderRepository extends JpaRepository<RepairOrder,Long>{
    List<RepairOrder> findAllByOrderByCreatedAtDesc();
    List<RepairOrder> findTop5ByOrderByCreatedAtDesc();
    List<RepairOrder> findByMember_IdOrderByCreatedAtDesc(Long memberId);
    List<RepairOrder> findByRepairStatusOrderByCreatedAtDesc(String repairStatus);
    long countByRepairStatus(String repairStatus);
    long countByRepairStatusNotIn(Collection<String> statuses);
}
