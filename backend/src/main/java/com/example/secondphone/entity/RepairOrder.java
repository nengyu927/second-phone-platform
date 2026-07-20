package com.example.secondphone.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "repair_orders")
public class RepairOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @Column(name = "device_brand", nullable = false, length = 50)
    private String deviceBrand;
    @Column(name = "device_model", nullable = false, length = 100)
    private String deviceModel;
    @Column(length = 30)
    private String imei;
    @Column(name = "problem_description", nullable = false, columnDefinition = "TEXT")
    private String problemDescription;
    @Column(name = "repair_status", nullable = false, length = 30)
    private String repairStatus = "RECEIVED";
    @Column(name = "estimated_cost", precision = 12, scale = 2)
    private BigDecimal estimatedCost;
    @Column(name = "final_cost", precision = 12, scale = 2)
    private BigDecimal finalCost;
    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;
    @Column(name = "expected_completion_date")
    private LocalDate expectedCompletionDate;
    @Column(name = "completed_date")
    private LocalDate completedDate;
    @Column(name = "technician_name", length = 100)
    private String technicianName;
    @Column(name = "repair_notes", columnDefinition = "TEXT")
    private String repairNotes;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (repairStatus == null || repairStatus.isBlank())
            repairStatus = "RECEIVED";
        if (receivedDate == null)
            receivedDate = LocalDate.now();
        createdAt = now;
        updatedAt = now;
        completeIfNeeded();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
        completeIfNeeded();
    }

    private void completeIfNeeded() {
        if ("COMPLETED".equalsIgnoreCase(repairStatus) && completedDate == null)
            completedDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member v) {
        member = v;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String v) {
        deviceBrand = v;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String v) {
        deviceModel = v;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String v) {
        imei = v;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String v) {
        problemDescription = v;
    }

    public String getRepairStatus() {
        return repairStatus;
    }

    public void setRepairStatus(String v) {
        repairStatus = v;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal v) {
        estimatedCost = v;
    }

    public BigDecimal getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(BigDecimal v) {
        finalCost = v;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate v) {
        receivedDate = v;
    }

    public LocalDate getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(LocalDate v) {
        expectedCompletionDate = v;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate v) {
        completedDate = v;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String v) {
        technicianName = v;
    }

    public String getRepairNotes() {
        return repairNotes;
    }

    public void setRepairNotes(String v) {
        repairNotes = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime v) {
        createdAt = v;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime v) {
        updatedAt = v;
    }
}
