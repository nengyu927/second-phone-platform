package com.example.secondphone.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.secondphone.dto.DashboardRecentDataResponse;
import com.example.secondphone.dto.DashboardRecentDataResponse.*;
import com.example.secondphone.dto.DashboardResponse;
import com.example.secondphone.dto.DashboardSummaryResponse;
import com.example.secondphone.entity.*;
import com.example.secondphone.repository.*;

@Service
public class DashboardService {
    private static final List<String> INACTIVE_REPAIR_STATUSES=List.of("COMPLETED","RETURNED","CANCELLED");
    private final MemberRepository members; private final ProductRepository products;
    private final OrderRepository orders; private final RepairOrderRepository repairs;
    public DashboardService(MemberRepository members,ProductRepository products,OrderRepository orders,RepairOrderRepository repairs){this.members=members;this.products=products;this.orders=orders;this.repairs=repairs;}

    public DashboardSummaryResponse getSummary(){
        return new DashboardSummaryResponse(
                members.count(),products.count(),orders.count(),repairs.count(),
                orders.countByOrderStatus("PENDING"),repairs.countByRepairStatusNotIn(INACTIVE_REPAIR_STATUSES),
                repairs.countByRepairStatus("COMPLETED"),products.countByStockLessThanEqual(5),
                zeroIfNull(orders.sumTotalAmountByOrderStatusNot("CANCELLED")),
                zeroIfNull(orders.sumTotalAmountByOrderStatus("COMPLETED")));
    }

    public DashboardRecentDataResponse getRecentData(){
        List<RecentMemberResponse> recentMembers=safe(members.findTop5ByOrderByCreatedAtDesc()).stream().limit(5)
                .map(m->new RecentMemberResponse(m.getId(),m.getAccount(),m.getName(),m.getEmail(),m.getPhone(),m.getCreatedAt())).toList();
        List<RecentProductResponse> recentProducts=safe(products.findTop5ByOrderByCreatedAtDesc()).stream().limit(5)
                .map(p->new RecentProductResponse(p.getId(),p.getProductName(),p.getPrice(),p.getStock(),p.getStatus(),p.getCreatedAt())).toList();
        List<RecentOrderResponse> recentOrders=safe(orders.findTop5ByOrderByCreatedAtDesc()).stream().limit(5)
                .map(o->new RecentOrderResponse(o.getId(),o.getMemberId(),o.getProductId(),zeroIfNull(o.getTotalAmount()),o.getOrderStatus(),o.getCreatedAt())).toList();
        List<RecentRepairResponse> recentRepairs=safe(repairs.findTop5ByOrderByCreatedAtDesc()).stream().limit(5)
                .map(r->new RecentRepairResponse(r.getId(),r.getMemberId(),r.getDeviceBrand(),r.getDeviceModel(),r.getRepairStatus(),r.getReceivedAt(),r.getCreatedAt())).toList();
        return new DashboardRecentDataResponse(recentMembers,recentProducts,recentOrders,recentRepairs);
    }
    public DashboardResponse getDashboard(){return new DashboardResponse(getSummary(),getRecentData());}
    private BigDecimal zeroIfNull(BigDecimal value){return value==null?BigDecimal.ZERO:value;}
    private <T> List<T> safe(List<T> values){return values==null?List.of():values;}
}
