package com.example.secondphone.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.secondphone.dto.RepairOrderRequest;
import com.example.secondphone.dto.RepairOrderResponse;
import com.example.secondphone.entity.Member;
import com.example.secondphone.entity.RepairOrder;
import com.example.secondphone.repository.MemberRepository;
import com.example.secondphone.repository.RepairOrderRepository;

@Service
@Transactional(readOnly=true)
public class RepairOrderService {
    private static final Set<String> REPAIR_STATUSES=Set.of("RECEIVED","INSPECTING","WAITING_PARTS","REPAIRING","COMPLETED","RETURNED","CANCELLED");
    private final RepairOrderRepository repairs; private final MemberRepository members;
    public RepairOrderService(RepairOrderRepository repairs,MemberRepository members){this.repairs=repairs;this.members=members;}
    public List<RepairOrderResponse> getAllRepairOrders(){return repairs.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();}
    public RepairOrderResponse getRepairOrderById(Long id){return toResponse(findRepair(id));}
    public List<RepairOrderResponse> searchRepairOrders(String keyword,String status){String key=keyword==null?"":keyword.trim().toLowerCase(Locale.ROOT);String wanted=status==null?"":status.trim();return repairs.findAllByOrderByCreatedAtDesc().stream().filter(r->wanted.isBlank()||wanted.equalsIgnoreCase(r.getRepairStatus())).filter(r->key.isBlank()||contains(r.getMember().getName(),key)||contains(r.getMember().getAccount(),key)||contains(r.getDeviceBrand(),key)||contains(r.getDeviceModel(),key)).map(this::toResponse).toList();}
    @Transactional public RepairOrderResponse createRepairOrder(RepairOrderRequest request){validateCosts(request);RepairOrder repair=new RepairOrder();apply(repair,request,findMember(request.memberId()),true);return toResponse(repairs.save(repair));}
    @Transactional public RepairOrderResponse updateRepairOrder(Long id,RepairOrderRequest request){validateCosts(request);RepairOrder repair=findRepair(id);apply(repair,request,findMember(request.memberId()),false);return toResponse(repairs.save(repair));}
    @Transactional public void deleteRepairOrder(Long id){RepairOrder repair=findRepair(id);repairs.delete(repair);}
    private void apply(RepairOrder r,RepairOrderRequest q,Member m,boolean creating){
        r.setMember(m);r.setDeviceBrand(q.deviceBrand());r.setDeviceModel(q.deviceModel());r.setImei(q.imei());r.setProblemDescription(q.problemDescription());
        String status=q.repairStatus()==null||q.repairStatus().isBlank()?"RECEIVED":q.repairStatus().trim().toUpperCase(Locale.ROOT);if(!REPAIR_STATUSES.contains(status))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"不支援的維修狀態");r.setRepairStatus(status);
        r.setEstimatedCost(q.estimatedCost());r.setFinalCost(q.finalCost());r.setReceivedDate(q.receivedDate()!=null?q.receivedDate():(creating?LocalDate.now():r.getReceivedDate()));r.setExpectedCompletionDate(q.expectedCompletionDate());
        if(q.completedDate()!=null)r.setCompletedDate(q.completedDate());else if("COMPLETED".equals(status)&&r.getCompletedDate()==null)r.setCompletedDate(LocalDate.now());
        r.setTechnicianName(q.technicianName());r.setRepairNotes(q.repairNotes());
    }
    private void validateCosts(RepairOrderRequest q){if(negative(q.estimatedCost())||negative(q.finalCost()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"維修金額不可為負數");}
    private boolean negative(BigDecimal value){return value!=null&&value.compareTo(BigDecimal.ZERO)<0;}
    private RepairOrder findRepair(Long id){return repairs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"找不到維修單"));}
    private Member findMember(Long id){return members.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"找不到會員"));}
    private boolean contains(String value,String key){return value!=null&&value.toLowerCase(Locale.ROOT).contains(key);}
    private RepairOrderResponse toResponse(RepairOrder r){return new RepairOrderResponse(r.getId(),r.getMember().getId(),r.getMember().getName(),r.getDeviceBrand(),r.getDeviceModel(),r.getImei(),r.getProblemDescription(),r.getRepairStatus(),r.getEstimatedCost(),r.getFinalCost(),r.getReceivedDate(),r.getExpectedCompletionDate(),r.getCompletedDate(),r.getTechnicianName(),r.getRepairNotes(),r.getCreatedAt(),r.getUpdatedAt());}
}
