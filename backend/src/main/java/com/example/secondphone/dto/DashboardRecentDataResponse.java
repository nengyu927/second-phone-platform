package com.example.secondphone.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardRecentDataResponse {
    private final List<RecentMemberResponse> recentMembers;
    private final List<RecentProductResponse> recentProducts;
    private final List<RecentOrderResponse> recentOrders;
    private final List<RecentRepairResponse> recentRepairs;

    public DashboardRecentDataResponse(List<RecentMemberResponse> members, List<RecentProductResponse> products,
            List<RecentOrderResponse> orders, List<RecentRepairResponse> repairs) {
        recentMembers=members; recentProducts=products; recentOrders=orders; recentRepairs=repairs;
    }
    public List<RecentMemberResponse> getRecentMembers(){return recentMembers;}
    public List<RecentProductResponse> getRecentProducts(){return recentProducts;}
    public List<RecentOrderResponse> getRecentOrders(){return recentOrders;}
    public List<RecentRepairResponse> getRecentRepairs(){return recentRepairs;}

    public record RecentMemberResponse(Long id,String account,String name,String email,String phone,LocalDateTime createdAt){}
    public record RecentProductResponse(Long id,String productName,BigDecimal price,Integer stock,String status,LocalDateTime createdAt){}
    public record RecentOrderResponse(Long id,Long memberId,Long productId,BigDecimal totalAmount,String orderStatus,LocalDateTime createdAt){}
    public record RecentRepairResponse(Long id,Long memberId,String deviceBrand,String deviceModel,String repairStatus,LocalDateTime receivedAt,LocalDateTime createdAt){}
}
