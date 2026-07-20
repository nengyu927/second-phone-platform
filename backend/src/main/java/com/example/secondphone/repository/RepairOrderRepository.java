package com.example.secondphone.repository;
import java.util.List;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.secondphone.entity.RepairOrder;
public interface RepairOrderRepository extends JpaRepository<RepairOrder,Long>{
    List<RepairOrder> findTop5ByOrderByCreatedAtDesc();
    long countByRepairStatus(String repairStatus);
    long countByRepairStatusNotIn(Collection<String> statuses);
    List<RepairOrder> findByMemberId(Long memberId);
    List<RepairOrder> findByRepairStatus(String repairStatus);
    List<RepairOrder> findByDeviceBrandContainingIgnoreCase(String deviceBrand);
}
