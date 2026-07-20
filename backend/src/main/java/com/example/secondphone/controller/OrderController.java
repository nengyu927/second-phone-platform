package com.example.secondphone.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.dto.OrderRequest;
import com.example.secondphone.dto.OrderResponse;
import com.example.secondphone.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/orders") @Tag(name="Order Management",description="訂單 CRUD、金額計算與庫存調整")
public class OrderController {
    private final OrderService service; public OrderController(OrderService service){this.service=service;}
    @GetMapping @Operation(summary="查詢訂單",description="可依關鍵字與狀態篩選") @ApiResponse(responseCode="200",description="查詢成功")
    public ResponseEntity<List<OrderResponse>> getAll(@RequestParam(name="keyword",required=false) String keyword,@RequestParam(name="status",required=false) String status){return ResponseEntity.ok((keyword==null||keyword.isBlank())&&(status==null||status.isBlank())?service.getAllOrders():service.searchOrders(keyword,status));}
    @GetMapping("/{id}") @Operation(summary="查詢單筆訂單") @ApiResponse(responseCode="200",description="查詢成功") @ApiResponse(responseCode="404",description="找不到訂單")
    public ResponseEntity<OrderResponse> getById(@PathVariable("id") Long id){return ResponseEntity.ok(service.getOrderById(id));}
    @PostMapping @Operation(summary="建立訂單",description="價格由商品帶入，總金額由後端計算並扣除庫存") @ApiResponse(responseCode="201",description="建立成功") @ApiResponse(responseCode="400",description="資料驗證失敗") @ApiResponse(responseCode="404",description="會員或商品不存在") @ApiResponse(responseCode="409",description="庫存不足")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(request));}
    @PutMapping("/{id}") @Operation(summary="修改訂單",description="依新數量及狀態正確調整庫存") @ApiResponse(responseCode="200",description="修改成功") @ApiResponse(responseCode="400",description="資料驗證失敗") @ApiResponse(responseCode="404",description="找不到資料") @ApiResponse(responseCode="409",description="庫存不足")
    public ResponseEntity<OrderResponse> update(@PathVariable("id") Long id,@Valid @RequestBody OrderRequest request){return ResponseEntity.ok(service.updateOrder(id,request));}
    @DeleteMapping("/{id}") @Operation(summary="刪除訂單",description="未取消的訂單會先補回庫存") @ApiResponse(responseCode="204",description="刪除成功") @ApiResponse(responseCode="404",description="找不到訂單")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){service.deleteOrder(id);return ResponseEntity.noContent().build();}
}
