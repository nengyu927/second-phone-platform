package com.example.secondphone.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.secondphone.entity.Order;
import com.example.secondphone.repository.MemberRepository;
import com.example.secondphone.repository.OrderRepository;
import com.example.secondphone.repository.ProductRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orders, MemberRepository members, ProductRepository products) {
        orderRepository=orders; memberRepository=members; productRepository=products;
    }
    public List<Order> findAll(){return orderRepository.findAll();}
    public Order findById(Long id){return orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"訂單不存在"));}
    public Order create(Order order){validate(order); calculateTotal(order); return orderRepository.save(order);}
    public Order update(Long id, Order input){
        validate(input); Order order=findById(id);
        order.setMemberId(input.getMemberId()); order.setProductId(input.getProductId()); order.setQuantity(input.getQuantity());
        order.setUnitPrice(input.getUnitPrice()); order.setRecipientName(input.getRecipientName()); order.setRecipientPhone(input.getRecipientPhone());
        order.setShippingAddress(input.getShippingAddress()); order.setPaymentMethod(input.getPaymentMethod()); order.setOrderStatus(input.getOrderStatus()); order.setNote(input.getNote());
        calculateTotal(order); return orderRepository.save(order);
    }
    public void delete(Long id){orderRepository.delete(findById(id));}
    public List<Order> search(Long memberId, Long productId, String status){
        List<Order> list=memberId!=null?orderRepository.findByMemberId(memberId):productId!=null?orderRepository.findByProductId(productId):hasText(status)?orderRepository.findByOrderStatus(status.trim()):findAll();
        return list.stream().filter(o->memberId==null||memberId.equals(o.getMemberId())).filter(o->productId==null||productId.equals(o.getProductId())).filter(o->!hasText(status)||status.trim().equalsIgnoreCase(o.getOrderStatus())).toList();
    }
    private void validate(Order o){
        if(!memberRepository.existsById(o.getMemberId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"會員不存在");
        if(!productRepository.existsById(o.getProductId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"商品不存在");
        if(o.getQuantity()==null||o.getQuantity()<1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"數量不可小於 1");
        if(o.getUnitPrice()==null||o.getUnitPrice().compareTo(BigDecimal.ZERO)<0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"單價不可小於 0");
    }
    private void calculateTotal(Order o){o.setTotalAmount(o.getUnitPrice().multiply(BigDecimal.valueOf(o.getQuantity())));}
    private boolean hasText(String v){return v!=null&&!v.isBlank();}
}
