package com.example.secondphone.dto;
import java.math.BigDecimal;import java.time.LocalDate;import java.time.LocalDateTime;import java.util.List;
public class DashboardRecentDataResponse{
 private final List<RecentMemberResponse> recentMembers;private final List<RecentProductResponse> recentProducts;private final List<RecentOrderResponse> recentOrders;private final List<RecentRepairResponse> recentRepairs;
 public DashboardRecentDataResponse(List<RecentMemberResponse> m,List<RecentProductResponse> p,List<RecentOrderResponse> o,List<RecentRepairResponse> r){recentMembers=m;recentProducts=p;recentOrders=o;recentRepairs=r;}
 public List<RecentMemberResponse> getRecentMembers(){return recentMembers;}public List<RecentProductResponse> getRecentProducts(){return recentProducts;}public List<RecentOrderResponse> getRecentOrders(){return recentOrders;}public List<RecentRepairResponse> getRecentRepairs(){return recentRepairs;}
 public record RecentMemberResponse(Long id,String account,String name,String email,String phone,LocalDateTime createdAt){}
 public record RecentProductResponse(Long id,String productName,BigDecimal price,Integer stock,String status,LocalDateTime createdAt){}
 public record RecentOrderResponse(Long id,Long memberId,Long productId,BigDecimal totalAmount,String orderStatus,LocalDateTime createdAt){}
 public record RecentRepairResponse(Long id,Long memberId,String deviceBrand,String deviceModel,String repairStatus,LocalDate receivedAt,LocalDateTime createdAt){}
}
