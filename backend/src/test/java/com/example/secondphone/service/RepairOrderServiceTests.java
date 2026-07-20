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
import com.example.secondphone.entity.RepairOrder;
import com.example.secondphone.repository.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTests {
    @Mock RepairOrderRepository repairs; @Mock MemberRepository members; RepairOrderService service;
    @BeforeEach void setUp(){service=new RepairOrderService(repairs,members);}
    @Test void createSuccessfully(){RepairOrder r=repair();when(members.existsById(1L)).thenReturn(true);when(repairs.save(r)).thenReturn(r);assertSame(r,service.create(r));}
    @Test void missingMemberReturns404(){RepairOrder r=repair();assertEquals(HttpStatus.NOT_FOUND,assertThrows(ResponseStatusException.class,()->service.create(r)).getStatusCode());}
    @Test void negativeCostReturns400(){RepairOrder r=repair();r.setEstimatedCost(new BigDecimal("-1"));when(members.existsById(1L)).thenReturn(true);assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ResponseStatusException.class,()->service.create(r)).getStatusCode());}
    @Test void completedStatusSetsCompletedAt(){RepairOrder r=repair();r.setRepairStatus("COMPLETED");when(members.existsById(1L)).thenReturn(true);when(repairs.save(r)).thenReturn(r);service.create(r);assertNotNull(r.getCompletedAt());}
    @Test void updateKeepsTimes(){RepairOrder old=repair();old.setId(2L);LocalDateTime created=LocalDateTime.now();LocalDateTime received=created.minusDays(1);old.setCreatedAt(created);old.setReceivedAt(received);RepairOrder input=repair();input.setDeviceModel("S25");when(members.existsById(1L)).thenReturn(true);when(repairs.findById(2L)).thenReturn(Optional.of(old));when(repairs.save(old)).thenReturn(old);RepairOrder result=service.update(2L,input);assertEquals(created,result.getCreatedAt());assertEquals(received,result.getReceivedAt());assertEquals("S25",result.getDeviceModel());}
    @Test void deleteSuccessfully(){RepairOrder r=repair();when(repairs.findById(1L)).thenReturn(Optional.of(r));service.delete(1L);verify(repairs).delete(r);}
    @Test void searchWithAndConditions(){RepairOrder ok=repair();RepairOrder wrong=repair();wrong.setDeviceBrand("Samsung");when(repairs.findByMemberId(1L)).thenReturn(List.of(ok,wrong));assertEquals(List.of(ok),service.search(1L,"RECEIVED","Apple"));}
    private RepairOrder repair(){RepairOrder r=new RepairOrder();r.setMemberId(1L);r.setDeviceBrand("Apple");r.setDeviceModel("iPhone 15");r.setProblemDescription("無法充電");r.setRepairStatus("RECEIVED");r.setEstimatedCost(new BigDecimal("1000"));return r;}
}
