package com.example.secondphone.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.entity.Order;
import com.example.secondphone.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/orders")
@Tag(name="Order Management",description="訂單 CRUD 與搜尋")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service){this.service=service;}

    @GetMapping @Operation(summary="查詢或搜尋訂單") @ApiResponse(responseCode="200",description="查詢成功")
    public ResponseEntity<List<Order>> findAll(@Parameter(description="會員 ID") @RequestParam(required=false) Long memberId,@Parameter(description="商品 ID") @RequestParam(required=false) Long productId,@Parameter(description="訂單狀態") @RequestParam(required=false) String orderStatus){return ResponseEntity.ok(service.search(memberId,productId,orderStatus));}
    @GetMapping("/{id}") @Operation(summary="查詢單一訂單") @ApiResponses({@ApiResponse(responseCode="200",description="查詢成功"),@ApiResponse(responseCode="404",description="訂單不存在")})
    public ResponseEntity<Order> findById(@Parameter(description="訂單 ID") @PathVariable Long id){return ResponseEntity.ok(service.findById(id));}
    @PostMapping @Operation(summary="新增訂單") @ApiResponses({@ApiResponse(responseCode="201",description="新增成功"),@ApiResponse(responseCode="400",description="驗證失敗"),@ApiResponse(responseCode="404",description="會員或商品不存在")})
    public ResponseEntity<Order> create(@Valid @RequestBody Order order){Order saved=service.create(order);return ResponseEntity.created(URI.create("/api/orders/"+saved.getId())).body(saved);}
    @PutMapping("/{id}") @Operation(summary="修改訂單") @ApiResponses({@ApiResponse(responseCode="200",description="修改成功"),@ApiResponse(responseCode="400",description="驗證失敗"),@ApiResponse(responseCode="404",description="資料不存在")})
    public ResponseEntity<Order> update(@PathVariable Long id,@Valid @RequestBody Order order){return ResponseEntity.ok(service.update(id,order));}
    @DeleteMapping("/{id}") @Operation(summary="刪除訂單") @ApiResponses({@ApiResponse(responseCode="204",description="刪除成功"),@ApiResponse(responseCode="404",description="訂單不存在")})
    public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
