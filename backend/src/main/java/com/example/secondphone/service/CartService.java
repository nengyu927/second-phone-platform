package com.example.secondphone.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.secondphone.dto.*;
import com.example.secondphone.entity.*;
import com.example.secondphone.exception.*;
import com.example.secondphone.repository.*;

@Service
public class CartService {
    private final CartRepository carts; private final CartItemRepository items; private final MemberRepository members; private final ProductRepository products;
    public CartService(CartRepository carts,CartItemRepository items,MemberRepository members,ProductRepository products){this.carts=carts;this.items=items;this.members=members;this.products=products;}
    @Transactional public CartResponse getCart(String account){return map(getOrCreate(account));}
    @Transactional public CartResponse addItem(String account,AddCartItemRequest request){
        Cart cart=getOrCreate(account);Product product=products.findById(request.productId()).orElseThrow(()->new ResourceNotFoundException("找不到指定商品"));
        ensurePurchasable(product);CartItem item=items.findByCartIdAndProductId(cart.getId(),product.getId()).orElseGet(()->{CartItem value=new CartItem();value.setCart(cart);value.setProduct(product);return value;});
        int quantity=(item.getQuantity()==null?0:item.getQuantity())+request.quantity();ensureStock(product,quantity);item.setQuantity(quantity);item.setUnitPrice(product.getPrice());items.save(item);return map(getOrCreate(account));
    }
    @Transactional public CartResponse updateItem(String account,Long itemId,UpdateCartItemRequest request){
        Cart cart=getOrCreate(account);CartItem item=items.findByIdAndCartId(itemId,cart.getId()).orElseThrow(()->new ResourceNotFoundException("找不到購物車項目"));
        ensurePurchasable(item.getProduct());ensureStock(item.getProduct(),request.quantity());item.setQuantity(request.quantity());item.setUnitPrice(item.getProduct().getPrice());items.save(item);return map(cart);
    }
    @Transactional public CartResponse removeItem(String account,Long itemId){Cart cart=getOrCreate(account);CartItem item=items.findByIdAndCartId(itemId,cart.getId()).orElseThrow(()->new ResourceNotFoundException("找不到購物車項目"));cart.getItems().removeIf(existing->existing.getId().equals(item.getId()));items.delete(item);return map(cart);}
    @Transactional public void clear(String account){Cart cart=getOrCreate(account);items.deleteAll(cart.getItems());cart.getItems().clear();}
    @Transactional public Cart getOrCreate(String account){Member member=findMember(account);return carts.findByMemberId(member.getId()).orElseGet(()->{Cart cart=new Cart();cart.setMember(member);return carts.save(cart);});}
    public Member findMember(String account){return members.findByAccountIgnoreCaseOrEmailIgnoreCase(account,account).orElseThrow(()->new ResourceNotFoundException("找不到登入會員"));}
    private void ensurePurchasable(Product product){String status=product.getStatus();if(!("ACTIVE".equalsIgnoreCase(status)||"AVAILABLE".equalsIgnoreCase(status)))throw new BusinessException("此商品目前未上架，無法加入購物車");if(product.getAvailableStock()<=0)throw new BusinessException("此商品目前缺貨");if(product.getPrice()==null)throw new BusinessException("此商品售價尚未設定");}
    private void ensureStock(Product product,int quantity){if(quantity>product.getAvailableStock())throw new BusinessException("商品庫存不足，目前最多可購買 "+product.getAvailableStock()+" 件");}
    private CartResponse map(Cart cart){var responses=cart.getItems().stream().map(item->{Product p=item.getProduct();BigDecimal price=p.getPrice();return new CartItemResponse(item.getId(),p.getId(),p.getProductCode(),p.getProductName(),p.getImageUrl(),p.getBrand(),p.getModel(),p.getStorageCapacity(),item.getQuantity(),price,price.multiply(BigDecimal.valueOf(item.getQuantity())),p.getAvailableStock());}).toList();int count=responses.stream().mapToInt(CartItemResponse::quantity).sum();BigDecimal total=responses.stream().map(CartItemResponse::subtotal).reduce(BigDecimal.ZERO,BigDecimal::add);return new CartResponse(cart.getId(),responses,count,total);}
}
