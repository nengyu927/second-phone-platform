package com.example.secondphone.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.dto.RepairOrderRequest;
import com.example.secondphone.dto.RepairOrderResponse;
import com.example.secondphone.service.RepairOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/repairs") @Tag(name="Repair Management",description="維修單 CRUD 與進度管理")
public class RepairOrderController {
    private final RepairOrderService service; public RepairOrderController(RepairOrderService service){this.service=service;}
    @GetMapping @Operation(summary="查詢維修單",description="可依關鍵字與狀態篩選") @ApiResponse(responseCode="200",description="查詢成功")
    public ResponseEntity<List<RepairOrderResponse>> getAll(@RequestParam(name="keyword",required=false) String keyword,@RequestParam(name="status",required=false) String status){return ResponseEntity.ok((keyword==null||keyword.isBlank())&&(status==null||status.isBlank())?service.getAllRepairOrders():service.searchRepairOrders(keyword,status));}
    @GetMapping("/{id}") @Operation(summary="查詢單筆維修單") @ApiResponse(responseCode="200",description="查詢成功") @ApiResponse(responseCode="404",description="找不到維修單")
    public ResponseEntity<RepairOrderResponse> getById(@PathVariable("id") Long id){return ResponseEntity.ok(service.getRepairOrderById(id));}
    @PostMapping @Operation(summary="建立維修單") @ApiResponse(responseCode="201",description="建立成功") @ApiResponse(responseCode="400",description="資料驗證失敗") @ApiResponse(responseCode="404",description="會員不存在")
    public ResponseEntity<RepairOrderResponse> create(@Valid @RequestBody RepairOrderRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.createRepairOrder(request));}
    @PutMapping("/{id}") @Operation(summary="修改維修單") @ApiResponse(responseCode="200",description="修改成功") @ApiResponse(responseCode="400",description="資料驗證失敗") @ApiResponse(responseCode="404",description="找不到資料")
    public ResponseEntity<RepairOrderResponse> update(@PathVariable("id") Long id,@Valid @RequestBody RepairOrderRequest request){return ResponseEntity.ok(service.updateRepairOrder(id,request));}
    @DeleteMapping("/{id}") @Operation(summary="刪除維修單") @ApiResponse(responseCode="204",description="刪除成功") @ApiResponse(responseCode="404",description="找不到維修單")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){service.deleteRepairOrder(id);return ResponseEntity.noContent().build();}
}
