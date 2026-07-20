package com.example.secondphone.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity @Table(name="repair_orders")
public class RepairOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotNull @Column(name="member_id",nullable=false) private Long memberId;
    @NotBlank @Column(name="device_brand",nullable=false,length=50) private String deviceBrand;
    @NotBlank @Column(name="device_model",nullable=false,length=100) private String deviceModel;
    @Column(length=30) private String imei;
    @NotBlank @Column(name="problem_description",nullable=false,columnDefinition="TEXT") private String problemDescription;
    @NotBlank @Column(name="repair_status",nullable=false,length=30) private String repairStatus="RECEIVED";
    @DecimalMin("0.0") @Column(name="estimated_cost",precision=12,scale=2) private BigDecimal estimatedCost;
    @DecimalMin("0.0") @Column(name="final_cost",precision=12,scale=2) private BigDecimal finalCost;
    @Column(name="technician_note",columnDefinition="TEXT") private String technicianNote;
    @Column(name="received_at",nullable=false,updatable=false) private LocalDateTime receivedAt;
    @Column(name="completed_at") private LocalDateTime completedAt;
    @Column(name="created_at",nullable=false,updatable=false) private LocalDateTime createdAt;
    @Column(name="updated_at",nullable=false) private LocalDateTime updatedAt;
    @PrePersist public void prePersist(){LocalDateTime now=LocalDateTime.now();if(repairStatus==null||repairStatus.isBlank())repairStatus="RECEIVED";if(receivedAt==null)receivedAt=now;createdAt=now;updatedAt=now;setCompletionTime();}
    @PreUpdate public void preUpdate(){updatedAt=LocalDateTime.now();setCompletionTime();}
    private void setCompletionTime(){if("COMPLETED".equalsIgnoreCase(repairStatus)&&completedAt==null)completedAt=LocalDateTime.now();}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getMemberId(){return memberId;} public void setMemberId(Long v){memberId=v;}
    public String getDeviceBrand(){return deviceBrand;} public void setDeviceBrand(String v){deviceBrand=v;}
    public String getDeviceModel(){return deviceModel;} public void setDeviceModel(String v){deviceModel=v;}
    public String getImei(){return imei;} public void setImei(String v){imei=v;}
    public String getProblemDescription(){return problemDescription;} public void setProblemDescription(String v){problemDescription=v;}
    public String getRepairStatus(){return repairStatus;} public void setRepairStatus(String v){repairStatus=v;}
    public BigDecimal getEstimatedCost(){return estimatedCost;} public void setEstimatedCost(BigDecimal v){estimatedCost=v;}
    public BigDecimal getFinalCost(){return finalCost;} public void setFinalCost(BigDecimal v){finalCost=v;}
    public String getTechnicianNote(){return technicianNote;} public void setTechnicianNote(String v){technicianNote=v;}
    public LocalDateTime getReceivedAt(){return receivedAt;} public void setReceivedAt(LocalDateTime v){receivedAt=v;}
    public LocalDateTime getCompletedAt(){return completedAt;} public void setCompletedAt(LocalDateTime v){completedAt=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}
