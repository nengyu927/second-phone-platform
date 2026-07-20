package com.example.secondphone.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.entity.RepairOrder;
import com.example.secondphone.service.RepairOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/repairs")
@Tag(name="Repair Management",description="維修單 CRUD 與搜尋")
public class RepairOrderController {
    private final RepairOrderService service;
    public RepairOrderController(RepairOrderService service){this.service=service;}
    @GetMapping @Operation(summary="查詢或搜尋維修單") @ApiResponse(responseCode="200",description="查詢成功")
    public ResponseEntity<List<RepairOrder>> findAll(@Parameter(description="會員 ID") @RequestParam(required=false) Long memberId,@Parameter(description="維修狀態") @RequestParam(required=false) String repairStatus,@Parameter(description="裝置品牌") @RequestParam(required=false) String deviceBrand){return ResponseEntity.ok(service.search(memberId,repairStatus,deviceBrand));}
    @GetMapping("/{id}") @Operation(summary="查詢單一維修單") @ApiResponses({@ApiResponse(responseCode="200",description="查詢成功"),@ApiResponse(responseCode="404",description="維修單不存在")})
    public ResponseEntity<RepairOrder> findById(@PathVariable Long id){return ResponseEntity.ok(service.findById(id));}
    @PostMapping @Operation(summary="新增維修單") @ApiResponses({@ApiResponse(responseCode="201",description="新增成功"),@ApiResponse(responseCode="400",description="驗證失敗"),@ApiResponse(responseCode="404",description="會員不存在")})
    public ResponseEntity<RepairOrder> create(@Valid @RequestBody RepairOrder order){RepairOrder saved=service.create(order);return ResponseEntity.created(URI.create("/api/repairs/"+saved.getId())).body(saved);}
    @PutMapping("/{id}") @Operation(summary="修改維修單") @ApiResponses({@ApiResponse(responseCode="200",description="修改成功"),@ApiResponse(responseCode="400",description="驗證失敗"),@ApiResponse(responseCode="404",description="資料不存在")})
    public ResponseEntity<RepairOrder> update(@PathVariable Long id,@Valid @RequestBody RepairOrder order){return ResponseEntity.ok(service.update(id,order));}
    @DeleteMapping("/{id}") @Operation(summary="刪除維修單") @ApiResponses({@ApiResponse(responseCode="204",description="刪除成功"),@ApiResponse(responseCode="404",description="維修單不存在")})
    public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
