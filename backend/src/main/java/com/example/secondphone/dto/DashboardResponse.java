package com.example.secondphone.dto;

public class DashboardResponse {
    private final DashboardSummaryResponse summary;
    private final DashboardRecentDataResponse recentData;
    public DashboardResponse(DashboardSummaryResponse summary,DashboardRecentDataResponse recentData){this.summary=summary;this.recentData=recentData;}
    public DashboardSummaryResponse getSummary(){return summary;}
    public DashboardRecentDataResponse getRecentData(){return recentData;}
}
