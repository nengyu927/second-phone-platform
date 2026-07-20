package com.example.secondphone.service;
import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
import java.math.BigDecimal;import java.time.LocalDate;import java.util.*;import org.junit.jupiter.api.*;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.Mock;import org.mockito.junit.jupiter.MockitoExtension;import org.springframework.http.HttpStatus;import org.springframework.web.server.ResponseStatusException;
import com.example.secondphone.dto.*;import com.example.secondphone.entity.*;import com.example.secondphone.repository.*;
@ExtendWith(MockitoExtension.class) class RepairOrderServiceTests{
 @Mock RepairOrderRepository repairs;@Mock MemberRepository members;RepairOrderService service;Member member;
 @BeforeEach void setup(){service=new RepairOrderService(repairs,members);member=member();}
 @Test void getAll(){when(repairs.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(repair()));assertEquals(1,service.getAllRepairOrders().size());}
 @Test void getOne(){when(repairs.findById(1L)).thenReturn(Optional.of(repair()));assertEquals(1L,service.getRepairOrderById(1L).id());}
 @Test void createSuccess(){stubCreate();service.createRepairOrder(request(null,null,BigDecimal.TEN));verify(repairs).save(any());}
 @Test void defaultsReceivedStatus(){stubCreate();assertEquals("RECEIVED",service.createRepairOrder(request(null,null,BigDecimal.TEN)).repairStatus());}
 @Test void defaultsReceivedDate(){stubCreate();assertEquals(LocalDate.now(),service.createRepairOrder(request(null,null,BigDecimal.TEN)).receivedDate());}
 @Test void missingMember(){assertEquals(HttpStatus.NOT_FOUND,assertThrows(ResponseStatusException.class,()->service.createRepairOrder(request(null,null,BigDecimal.TEN))).getStatusCode());}
 @Test void updatesStatus(){RepairOrder r=repair();stubUpdate(r);assertEquals("INSPECTING",service.updateRepairOrder(1L,request("INSPECTING",r.getReceivedDate(),BigDecimal.TEN)).repairStatus());}
 @Test void completedSetsDate(){RepairOrder r=repair();stubUpdate(r);assertEquals(LocalDate.now(),service.updateRepairOrder(1L,request("COMPLETED",r.getReceivedDate(),BigDecimal.TEN)).completedDate());}
 @Test void rejectsNegativeCost(){assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ResponseStatusException.class,()->service.createRepairOrder(request(null,null,new BigDecimal("-1")))).getStatusCode());}
 @Test void deleteSuccess(){RepairOrder r=repair();when(repairs.findById(1L)).thenReturn(Optional.of(r));service.deleteRepairOrder(1L);verify(repairs).delete(r);}
 @Test void deleteMissing(){assertEquals(HttpStatus.NOT_FOUND,assertThrows(ResponseStatusException.class,()->service.deleteRepairOrder(9L)).getStatusCode());}
 @Test void invalidStatusReturnsBadRequest(){when(members.findById(1L)).thenReturn(Optional.of(member));assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ResponseStatusException.class,()->service.createRepairOrder(request("UNKNOWN",null,BigDecimal.TEN))).getStatusCode());}
 private void stubCreate(){when(members.findById(1L)).thenReturn(Optional.of(member));when(repairs.save(any())).thenAnswer(i->{RepairOrder r=i.getArgument(0);r.setId(1L);return r;});}
 private void stubUpdate(RepairOrder r){when(repairs.findById(1L)).thenReturn(Optional.of(r));when(members.findById(1L)).thenReturn(Optional.of(member));when(repairs.save(r)).thenReturn(r);}
 private RepairOrderRequest request(String status,LocalDate received,BigDecimal estimated){return new RepairOrderRequest(1L,"Apple","iPhone 15",null,"無法充電",status,estimated,null,received,null,null,"技師A",null);}
 private Member member(){Member m=new Member();m.setId(1L);m.setAccount("user");m.setName("王小明");return m;}
 private RepairOrder repair(){RepairOrder r=new RepairOrder();r.setId(1L);r.setMember(member);r.setDeviceBrand("Apple");r.setDeviceModel("iPhone 15");r.setProblemDescription("無法充電");r.setRepairStatus("RECEIVED");r.setReceivedDate(LocalDate.now());return r;}
}
