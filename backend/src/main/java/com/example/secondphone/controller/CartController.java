package com.example.secondphone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.secondphone.dto.*;
import com.example.secondphone.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/cart") @Tag(name="購物車",description="登入會員自己的購物車")
public class CartController {
    private final CartService service;public CartController(CartService service){this.service=service;}
    @GetMapping public CartResponse get(Authentication auth){return service.getCart(auth.getName());}
    @PostMapping("/items") public CartResponse add(Authentication auth,@Valid @RequestBody AddCartItemRequest request){return service.addItem(auth.getName(),request);}
    @PutMapping("/items/{itemId}") public CartResponse update(Authentication auth,@PathVariable Long itemId,@Valid @RequestBody UpdateCartItemRequest request){return service.updateItem(auth.getName(),itemId,request);}
    @DeleteMapping("/items/{itemId}") public CartResponse remove(Authentication auth,@PathVariable Long itemId){return service.removeItem(auth.getName(),itemId);}
    @DeleteMapping public ResponseEntity<Void> clear(Authentication auth){service.clear(auth.getName());return ResponseEntity.noContent().build();}
}
