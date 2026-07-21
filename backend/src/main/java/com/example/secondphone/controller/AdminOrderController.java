package com.example.secondphone.controller;

import org.springframework.web.bind.annotation.*;
import com.example.secondphone.dto.*;
import com.example.secondphone.service.CheckoutService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/admin/orders") @Tag(name="後台訂單",description="後台查詢與更新訂單狀態")
public class AdminOrderController {
    private final CheckoutService service;public AdminOrderController(CheckoutService service){this.service=service;}
    @GetMapping public PagedResponse<OrderDetailResponse> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.adminOrders(page,size);}
    @GetMapping("/{id}") public OrderDetailResponse detail(@PathVariable Long id){return service.adminOrder(id);}
    @PatchMapping("/{id}/status") public OrderDetailResponse status(@PathVariable Long id,@Valid @RequestBody OrderStatusUpdateRequest request){return service.updateOrderStatus(id,request.status());}
    @PatchMapping("/{id}/payment-status") public OrderDetailResponse payment(@PathVariable Long id,@Valid @RequestBody OrderStatusUpdateRequest request){return service.updatePaymentStatus(id,request.status());}
    @PatchMapping("/{id}/shipping-status") public OrderDetailResponse shipping(@PathVariable Long id,@Valid @RequestBody OrderStatusUpdateRequest request){return service.updateShippingStatus(id,request.status());}
}
