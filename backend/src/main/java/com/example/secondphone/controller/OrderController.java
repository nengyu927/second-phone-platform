package com.example.secondphone.controller;

import java.util.List;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.dto.*;
import com.example.secondphone.service.CheckoutService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/orders") @Tag(name="會員訂單",description="會員建立、查詢與取消自己的訂單")
public class OrderController {
    private final CheckoutService service;public OrderController(CheckoutService service){this.service=service;}
    @PostMapping public ResponseEntity<OrderDetailResponse> create(Authentication auth,@Valid @RequestBody CheckoutRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.checkout(auth.getName(),request));}
    @GetMapping public List<OrderDetailResponse> list(Authentication auth){return service.memberOrders(auth.getName());}
    @GetMapping("/{id}") public OrderDetailResponse detail(Authentication auth,@PathVariable Long id){return service.memberOrder(auth.getName(),id);}
    @PostMapping("/{id}/cancel") public OrderDetailResponse cancel(Authentication auth,@PathVariable Long id){return service.cancel(auth.getName(),id);}
}
