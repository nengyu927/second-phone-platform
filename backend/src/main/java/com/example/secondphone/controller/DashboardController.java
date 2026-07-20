package com.example.secondphone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.secondphone.dto.*;
import com.example.secondphone.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController @RequestMapping("/api/dashboard")
@Tag(name="Dashboard Management",description="後台統計摘要與最近資料")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service){this.service=service;}
    @GetMapping @Operation(summary="取得完整儀表板資料") @ApiResponse(responseCode="200",description="查詢成功")
    public ResponseEntity<DashboardResponse> getDashboard(){return ResponseEntity.ok(service.getDashboard());}
    @GetMapping("/summary") @Operation(summary="取得統計摘要") @ApiResponse(responseCode="200",description="查詢成功")
    public ResponseEntity<DashboardSummaryResponse> getSummary(){return ResponseEntity.ok(service.getSummary());}
    @GetMapping("/recent") @Operation(summary="取得最近資料") @ApiResponse(responseCode="200",description="查詢成功")
    public ResponseEntity<DashboardRecentDataResponse> getRecent(){return ResponseEntity.ok(service.getRecentData());}
}
