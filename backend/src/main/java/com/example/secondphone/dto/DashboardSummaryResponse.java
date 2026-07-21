package com.example.secondphone.dto;

import java.math.BigDecimal;

public class DashboardSummaryResponse {
    private final long memberCount;
    private final long productCount;
    private final long orderCount;
    private final long repairCount;
    private final long pendingOrderCount;
    private final long activeRepairCount;
    private final long completedRepairCount;
    private final long lowStockProductCount;
    private final long tradeInCount;
    private final long pendingTradeInCount;
    private final BigDecimal totalOrderAmount;
    private final BigDecimal completedOrderAmount;

    public DashboardSummaryResponse(long memberCount, long productCount, long orderCount, long repairCount,
            long pendingOrderCount, long activeRepairCount, long completedRepairCount,
            long lowStockProductCount, long tradeInCount, long pendingTradeInCount, BigDecimal totalOrderAmount, BigDecimal completedOrderAmount) {
        this.memberCount=memberCount; this.productCount=productCount; this.orderCount=orderCount; this.repairCount=repairCount;
        this.pendingOrderCount=pendingOrderCount; this.activeRepairCount=activeRepairCount;
        this.completedRepairCount=completedRepairCount; this.lowStockProductCount=lowStockProductCount;
        this.tradeInCount=tradeInCount; this.pendingTradeInCount=pendingTradeInCount;
        this.totalOrderAmount=totalOrderAmount; this.completedOrderAmount=completedOrderAmount;
    }
    public long getMemberCount(){return memberCount;} public long getProductCount(){return productCount;}
    public long getOrderCount(){return orderCount;} public long getRepairCount(){return repairCount;}
    public long getPendingOrderCount(){return pendingOrderCount;} public long getActiveRepairCount(){return activeRepairCount;}
    public long getCompletedRepairCount(){return completedRepairCount;} public long getLowStockProductCount(){return lowStockProductCount;}
    public long getTradeInCount(){return tradeInCount;} public long getPendingTradeInCount(){return pendingTradeInCount;}
    public BigDecimal getTotalOrderAmount(){return totalOrderAmount;} public BigDecimal getCompletedOrderAmount(){return completedOrderAmount;}
}
