package com.example.secondphone.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.secondphone.entity.RepairOrder;
import com.example.secondphone.repository.MemberRepository;
import com.example.secondphone.repository.RepairOrderRepository;

@Service
public class RepairOrderService {
    private final RepairOrderRepository repository; private final MemberRepository members;
    public RepairOrderService(RepairOrderRepository repository,MemberRepository members){this.repository=repository;this.members=members;}
    public List<RepairOrder> findAll(){return repository.findAll();}
    public RepairOrder findById(Long id){return repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"維修單不存在"));}
    public RepairOrder create(RepairOrder order){validate(order);setCompletedAt(order);return repository.save(order);}
    public RepairOrder update(Long id,RepairOrder input){
        validate(input);RepairOrder order=findById(id);order.setMemberId(input.getMemberId());order.setDeviceBrand(input.getDeviceBrand());order.setDeviceModel(input.getDeviceModel());order.setImei(input.getImei());order.setProblemDescription(input.getProblemDescription());order.setRepairStatus(input.getRepairStatus());order.setEstimatedCost(input.getEstimatedCost());order.setFinalCost(input.getFinalCost());order.setTechnicianNote(input.getTechnicianNote());setCompletedAt(order);return repository.save(order);
    }
    public void delete(Long id){repository.delete(findById(id));}
    public List<RepairOrder> search(Long memberId,String status,String brand){
        List<RepairOrder> list=memberId!=null?repository.findByMemberId(memberId):hasText(status)?repository.findByRepairStatus(status.trim()):hasText(brand)?repository.findByDeviceBrandContainingIgnoreCase(brand.trim()):findAll();
        return list.stream().filter(r->memberId==null||memberId.equals(r.getMemberId())).filter(r->!hasText(status)||status.trim().equalsIgnoreCase(r.getRepairStatus())).filter(r->!hasText(brand)||(r.getDeviceBrand()!=null&&r.getDeviceBrand().toLowerCase().contains(brand.trim().toLowerCase()))).toList();
    }
    private void validate(RepairOrder r){if(!members.existsById(r.getMemberId()))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"會員不存在");if(negative(r.getEstimatedCost())||negative(r.getFinalCost()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"維修費用不可小於 0");}
    private boolean negative(BigDecimal v){return v!=null&&v.compareTo(BigDecimal.ZERO)<0;}
    private void setCompletedAt(RepairOrder r){if("COMPLETED".equalsIgnoreCase(r.getRepairStatus())&&r.getCompletedAt()==null)r.setCompletedAt(LocalDateTime.now());}
    private boolean hasText(String v){return v!=null&&!v.isBlank();}
}
