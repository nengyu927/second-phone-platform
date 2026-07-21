package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.secondphone.dto.*;
import com.example.secondphone.entity.*;
import com.example.secondphone.repository.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTests {
    @Mock MemberRepository members; @Mock ProductRepository products; @Mock OrderRepository orders; @Mock RepairOrderRepository repairs; @Mock TradeInRepository tradeIns;
    DashboardService service;
    @BeforeEach void setUp(){service=new DashboardService(members,products,orders,repairs,tradeIns);}
    @Test void calculatesFourMainCounts(){when(members.count()).thenReturn(10L);when(products.count()).thenReturn(20L);when(orders.count()).thenReturn(30L);when(repairs.count()).thenReturn(40L);DashboardSummaryResponse s=service.getSummary();assertEquals(10,s.getMemberCount());assertEquals(20,s.getProductCount());assertEquals(30,s.getOrderCount());assertEquals(40,s.getRepairCount());}
    @Test void calculatesPendingOrders(){when(orders.countByOrderStatus("PENDING")).thenReturn(3L);assertEquals(3,service.getSummary().getPendingOrderCount());}
    @Test void calculatesActiveRepairs(){when(repairs.countByRepairStatusNotIn(List.of("COMPLETED","RETURNED","CANCELLED"))).thenReturn(4L);assertEquals(4,service.getSummary().getActiveRepairCount());}
    @Test void calculatesCompletedRepairs(){when(repairs.countByRepairStatus("COMPLETED")).thenReturn(5L);assertEquals(5,service.getSummary().getCompletedRepairCount());}
    @Test void calculatesLowStockProducts(){when(products.countByStockLessThanEqual(5)).thenReturn(6L);assertEquals(6,service.getSummary().getLowStockProductCount());}
    @Test void calculatesNonCancelledOrderAmount(){when(orders.sumTotalAmountByOrderStatusNot("CANCELLED")).thenReturn(new BigDecimal("1200"));assertEquals(new BigDecimal("1200"),service.getSummary().getTotalOrderAmount());}
    @Test void calculatesCompletedOrderAmount(){when(orders.sumTotalAmountByOrderStatus("COMPLETED")).thenReturn(new BigDecimal("800"));assertEquals(new BigDecimal("800"),service.getSummary().getCompletedOrderAmount());}
    @Test void convertsNullSumsToZero(){DashboardSummaryResponse s=service.getSummary();assertEquals(BigDecimal.ZERO,s.getTotalOrderAmount());assertEquals(BigDecimal.ZERO,s.getCompletedOrderAmount());}
    @Test void returnsAllFourRecentLists(){when(members.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(member()));when(products.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(product()));when(orders.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(order()));when(repairs.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(repair()));DashboardRecentDataResponse r=service.getRecentData();assertEquals(1,r.getRecentMembers().size());assertEquals(1,r.getRecentProducts().size());assertEquals(1,r.getRecentOrders().size());assertEquals(1,r.getRecentRepairs().size());}
    @Test void limitsEveryRecentListToFive(){when(members.findTop5ByOrderByCreatedAtDesc()).thenReturn(IntStream.range(0,6).mapToObj(i->member()).toList());when(products.findTop5ByOrderByCreatedAtDesc()).thenReturn(IntStream.range(0,6).mapToObj(i->product()).toList());when(orders.findTop5ByOrderByCreatedAtDesc()).thenReturn(IntStream.range(0,6).mapToObj(i->order()).toList());when(repairs.findTop5ByOrderByCreatedAtDesc()).thenReturn(IntStream.range(0,6).mapToObj(i->repair()).toList());DashboardRecentDataResponse r=service.getRecentData();assertEquals(5,r.getRecentMembers().size());assertEquals(5,r.getRecentProducts().size());assertEquals(5,r.getRecentOrders().size());assertEquals(5,r.getRecentRepairs().size());}
    private Member member(){Member m=new Member();m.setAccount("user");m.setName("User");return m;}
    private Product product(){Product p=new Product();p.setProductName("Phone");p.setPrice(BigDecimal.TEN);p.setStock(1);return p;}
    private Order order(){Order o=new Order();o.setMember(member());o.setProduct(product());o.setTotalAmount(null);o.setOrderStatus("PENDING");return o;}
    private RepairOrder repair(){RepairOrder r=new RepairOrder();r.setMember(member());r.setDeviceBrand("Apple");r.setDeviceModel("iPhone");r.setRepairStatus("RECEIVED");return r;}
}
