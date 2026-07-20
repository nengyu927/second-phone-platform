package com.example.secondphone.service;
import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
import java.math.BigDecimal;import java.util.*;import org.junit.jupiter.api.BeforeEach;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.Mock;import org.mockito.junit.jupiter.MockitoExtension;import org.springframework.http.HttpStatus;import org.springframework.web.server.ResponseStatusException;
import com.example.secondphone.dto.*;import com.example.secondphone.entity.Member;import com.example.secondphone.entity.Order;import com.example.secondphone.entity.Product;import com.example.secondphone.repository.*;
@ExtendWith(MockitoExtension.class) class OrderServiceTests{
 @Mock OrderRepository orders;@Mock MemberRepository members;@Mock ProductRepository products;OrderService service;Member member;Product product;
 @BeforeEach void setup(){service=new OrderService(orders,members,products);member=member();product=product(10);}
 @Test void getAll(){when(orders.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(order(2,"PENDING")));assertEquals(1,service.getAllOrders().size());}
 @Test void getOne(){when(orders.findById(1L)).thenReturn(Optional.of(order(2,"PENDING")));assertEquals(1L,service.getOrderById(1L).id());}
 @Test void createSuccess(){stubCreate();service.createOrder(request(2,"PENDING"));verify(orders).save(any(Order.class));}
 @Test void usesProductPrice(){stubCreate();assertEquals(new BigDecimal("100.00"),service.createOrder(request(2,"PENDING")).unitPrice());}
 @Test void calculatesTotal(){stubCreate();assertEquals(new BigDecimal("200.00"),service.createOrder(request(2,"PENDING")).totalAmount());}
 @Test void deductsStock(){stubCreate();service.createOrder(request(2,"PENDING"));assertEquals(8,product.getStock());}
 @Test void insufficientStock(){product.setStock(1);when(members.findById(1L)).thenReturn(Optional.of(member));when(products.findById(2L)).thenReturn(Optional.of(product));assertEquals(HttpStatus.CONFLICT,assertThrows(ResponseStatusException.class,()->service.createOrder(request(2,"PENDING"))).getStatusCode());}
 @Test void missingMember(){assertEquals(HttpStatus.NOT_FOUND,assertThrows(ResponseStatusException.class,()->service.createOrder(request(2,"PENDING"))).getStatusCode());}
 @Test void missingProduct(){when(members.findById(1L)).thenReturn(Optional.of(member));assertEquals(HttpStatus.NOT_FOUND,assertThrows(ResponseStatusException.class,()->service.createOrder(request(2,"PENDING"))).getStatusCode());}
 @Test void updateQuantityAdjustsStock(){product.setStock(8);Order o=order(2,"PENDING");when(orders.findById(1L)).thenReturn(Optional.of(o));when(members.findById(1L)).thenReturn(Optional.of(member));when(products.findById(2L)).thenReturn(Optional.of(product));when(orders.save(o)).thenReturn(o);service.updateOrder(1L,request(5,"PENDING"));assertEquals(5,product.getStock());}
 @Test void cancelRestoresStock(){product.setStock(8);Order o=order(2,"PENDING");stubUpdate(o);service.updateOrder(1L,request(2,"CANCELLED"));assertEquals(10,product.getStock());}
 @Test void repeatedCancelDoesNotRestore(){Order o=order(2,"CANCELLED");stubUpdate(o);service.updateOrder(1L,request(2,"CANCELLED"));assertEquals(10,product.getStock());}
 @Test void deleteActiveRestores(){product.setStock(8);Order o=order(2,"PENDING");when(orders.findById(1L)).thenReturn(Optional.of(o));service.deleteOrder(1L);assertEquals(10,product.getStock());verify(orders).delete(o);}
 @Test void deleteMissing(){assertEquals(HttpStatus.NOT_FOUND,assertThrows(ResponseStatusException.class,()->service.deleteOrder(9L)).getStatusCode());}
 @Test void invalidStatusReturnsBadRequest(){when(members.findById(1L)).thenReturn(Optional.of(member));when(products.findById(2L)).thenReturn(Optional.of(product));assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ResponseStatusException.class,()->service.createOrder(request(1,"UNKNOWN"))).getStatusCode());}
 private void stubCreate(){when(members.findById(1L)).thenReturn(Optional.of(member));when(products.findById(2L)).thenReturn(Optional.of(product));when(orders.save(any())).thenAnswer(i->{Order o=i.getArgument(0);o.setId(1L);return o;});}
 private void stubUpdate(Order o){when(orders.findById(1L)).thenReturn(Optional.of(o));when(members.findById(1L)).thenReturn(Optional.of(member));when(products.findById(2L)).thenReturn(Optional.of(product));when(orders.save(o)).thenReturn(o);}
 private OrderRequest request(int qty,String status){return new OrderRequest(1L,2L,qty,status,"王小明","0912345678","台北市",null);}
 private Member member(){Member m=new Member();m.setId(1L);m.setAccount("user");m.setName("王小明");return m;}
 private Product product(int stock){Product p=new Product();p.setId(2L);p.setProductName("iPhone");p.setPrice(new BigDecimal("100.00"));p.setStock(stock);return p;}
 private Order order(int qty,String status){Order o=new Order();o.setId(1L);o.setMember(member);o.setProduct(product);o.setQuantity(qty);o.setUnitPrice(product.getPrice());o.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(qty)));o.setOrderStatus(status);o.setRecipientName("王小明");o.setRecipientPhone("0912345678");o.setShippingAddress("台北市");return o;}
}
