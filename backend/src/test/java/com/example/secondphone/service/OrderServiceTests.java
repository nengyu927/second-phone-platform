package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.example.secondphone.entity.Order;
import com.example.secondphone.repository.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    @Mock OrderRepository orders; @Mock MemberRepository members; @Mock ProductRepository products;
    OrderService service;
    @BeforeEach void setUp(){service=new OrderService(orders,members,products);}
    @Test void createSuccessfully(){Order o=order();when(members.existsById(1L)).thenReturn(true);when(products.existsById(2L)).thenReturn(true);when(orders.save(o)).thenReturn(o);assertSame(o,service.create(o));}
    @Test void calculatesTotalAmount(){Order o=order();o.setTotalAmount(new BigDecimal("99999"));when(members.existsById(1L)).thenReturn(true);when(products.existsById(2L)).thenReturn(true);when(orders.save(o)).thenReturn(o);service.create(o);assertEquals(new BigDecimal("600.00"),o.getTotalAmount());}
    @Test void missingMemberReturns404(){Order o=order();ResponseStatusException e=assertThrows(ResponseStatusException.class,()->service.create(o));assertEquals(HttpStatus.NOT_FOUND,e.getStatusCode());}
    @Test void missingProductReturns404(){Order o=order();when(members.existsById(1L)).thenReturn(true);ResponseStatusException e=assertThrows(ResponseStatusException.class,()->service.create(o));assertEquals(HttpStatus.NOT_FOUND,e.getStatusCode());}
    @Test void quantityBelowOneReturns400(){Order o=order();o.setQuantity(0);when(members.existsById(1L)).thenReturn(true);when(products.existsById(2L)).thenReturn(true);assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ResponseStatusException.class,()->service.create(o)).getStatusCode());}
    @Test void updateKeepsIdentityAndCreatedAt(){Order existing=order();existing.setId(5L);LocalDateTime time=LocalDateTime.now();existing.setCreatedAt(time);Order input=order();input.setQuantity(4);when(members.existsById(1L)).thenReturn(true);when(products.existsById(2L)).thenReturn(true);when(orders.findById(5L)).thenReturn(Optional.of(existing));when(orders.save(existing)).thenReturn(existing);Order result=service.update(5L,input);assertEquals(5L,result.getId());assertEquals(time,result.getCreatedAt());assertEquals(new BigDecimal("800.00"),result.getTotalAmount());}
    @Test void deleteSuccessfully(){Order o=order();when(orders.findById(1L)).thenReturn(Optional.of(o));service.delete(1L);verify(orders).delete(o);}
    @Test void searchWithAndConditions(){Order ok=order();Order wrong=order();wrong.setProductId(9L);when(orders.findByMemberId(1L)).thenReturn(List.of(ok,wrong));assertEquals(List.of(ok),service.search(1L,2L,"PENDING"));}
    private Order order(){Order o=new Order();o.setMemberId(1L);o.setProductId(2L);o.setQuantity(3);o.setUnitPrice(new BigDecimal("200.00"));o.setRecipientName("王小明");o.setRecipientPhone("0912345678");o.setShippingAddress("台北市");o.setPaymentMethod("CASH_ON_DELIVERY");o.setOrderStatus("PENDING");return o;}
}
