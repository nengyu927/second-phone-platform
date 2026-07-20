package com.example.secondphone.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.secondphone.dto.OrderRequest;
import com.example.secondphone.dto.OrderResponse;
import com.example.secondphone.entity.Member;
import com.example.secondphone.entity.Order;
import com.example.secondphone.entity.Product;
import com.example.secondphone.repository.MemberRepository;
import com.example.secondphone.repository.OrderRepository;
import com.example.secondphone.repository.ProductRepository;

@Service
@Transactional(readOnly=true)
public class OrderService {
    private static final Set<String> ORDER_STATUSES=Set.of("PENDING","CONFIRMED","SHIPPED","COMPLETED","CANCELLED");
    private final OrderRepository orders; private final MemberRepository members; private final ProductRepository products;
    public OrderService(OrderRepository orders,MemberRepository members,ProductRepository products){this.orders=orders;this.members=members;this.products=products;}

    public List<OrderResponse> getAllOrders(){return orders.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();}
    public OrderResponse getOrderById(Long id){return toResponse(findOrder(id));}
    public List<OrderResponse> searchOrders(String keyword,String status){
        String key=keyword==null?"":keyword.trim().toLowerCase(Locale.ROOT);
        String wanted=status==null?"":status.trim();
        return orders.findAllByOrderByCreatedAtDesc().stream()
            .filter(o->wanted.isBlank()||wanted.equalsIgnoreCase(o.getOrderStatus()))
            .filter(o->key.isBlank()||contains(o.getMember().getName(),key)||contains(o.getMember().getAccount(),key)||contains(o.getProduct().getProductName(),key)||contains(o.getRecipientName(),key))
            .map(this::toResponse).toList();
    }
    @Transactional public OrderResponse createOrder(OrderRequest request){
        Member member=findMember(request.memberId()); Product product=findProduct(request.productId());
        normalizeStatus(request.orderStatus(),"PENDING");
        ensurePrice(product); deduct(product,request.quantity());
        Order order=new Order(); apply(order,request,member,product); return toResponse(orders.save(order));
    }
    @Transactional public OrderResponse updateOrder(Long id,OrderRequest request){
        Order order=findOrder(id); Member member=findMember(request.memberId()); Product product=findProduct(request.productId());
        boolean oldActive=!isCancelled(order.getOrderStatus()); boolean newActive=!isCancelled(normalizeStatus(request.orderStatus(),"PENDING"));
        if(oldActive){restore(order.getProduct(),order.getQuantity());}
        if(newActive){ensurePrice(product);deduct(product,request.quantity());}
        apply(order,request,member,product); return toResponse(orders.save(order));
    }
    @Transactional public void deleteOrder(Long id){Order order=findOrder(id);if(!isCancelled(order.getOrderStatus()))restore(order.getProduct(),order.getQuantity());orders.delete(order);}

    private void apply(Order order,OrderRequest r,Member m,Product p){
        ensurePrice(p);order.setMember(m);order.setProduct(p);order.setQuantity(r.quantity());order.setUnitPrice(p.getPrice());order.setTotalAmount(p.getPrice().multiply(BigDecimal.valueOf(r.quantity())));
        order.setOrderStatus(normalizeStatus(r.orderStatus(),"PENDING"));order.setRecipientName(r.recipientName());order.setRecipientPhone(r.recipientPhone());order.setShippingAddress(r.shippingAddress());order.setNote(r.note());
    }
    private void deduct(Product p,int quantity){int stock=p.getStock()==null?0:p.getStock();if(stock<quantity)throw new ResponseStatusException(HttpStatus.CONFLICT,"商品庫存不足");p.setStock(stock-quantity);products.save(p);}
    private void restore(Product p,int quantity){p.setStock((p.getStock()==null?0:p.getStock())+quantity);products.save(p);}
    private void ensurePrice(Product p){if(p.getPrice()==null||p.getPrice().compareTo(BigDecimal.ZERO)<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"商品價格不可為空或負數");}
    private Order findOrder(Long id){return orders.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"找不到訂單"));}
    private Member findMember(Long id){return members.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"找不到會員"));}
    private Product findProduct(Long id){return products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"找不到商品"));}
    private boolean contains(String value,String key){return value!=null&&value.toLowerCase(Locale.ROOT).contains(key);}
    private boolean isCancelled(String status){return "CANCELLED".equalsIgnoreCase(status);}
    private String normalizeStatus(String status,String fallback){String value=status==null||status.isBlank()?fallback:status.trim().toUpperCase(Locale.ROOT);if(!ORDER_STATUSES.contains(value))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"不支援的訂單狀態");return value;}
    private OrderResponse toResponse(Order o){return new OrderResponse(o.getId(),o.getMember().getId(),o.getMember().getName(),o.getProduct().getId(),o.getProduct().getProductName(),o.getQuantity(),o.getUnitPrice(),o.getTotalAmount(),o.getOrderStatus(),o.getRecipientName(),o.getRecipientPhone(),o.getShippingAddress(),o.getNote(),o.getCreatedAt(),o.getUpdatedAt());}
}
