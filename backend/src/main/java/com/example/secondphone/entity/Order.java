package com.example.secondphone.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity(name = "CustomerOrder")
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @Column(name = "member_id", nullable = false)
    private Long memberId;
    @NotNull @Column(name = "product_id", nullable = false)
    private Long productId;
    @NotNull @Min(1) @Column(nullable = false)
    private Integer quantity = 1;
    @NotNull @DecimalMin("0.0") @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    @NotNull @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @NotBlank @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;
    @NotBlank @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;
    @NotBlank @Column(name = "shipping_address", nullable = false, length = 300)
    private String shippingAddress;
    @NotBlank @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod = "CASH_ON_DELIVERY";
    @NotBlank @Column(name = "order_status", nullable = false, length = 30)
    private String orderStatus = "PENDING";
    @Column(columnDefinition = "TEXT")
    private String note;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (quantity == null) quantity = 1;
        if (paymentMethod == null || paymentMethod.isBlank()) paymentMethod = "CASH_ON_DELIVERY";
        if (orderStatus == null || orderStatus.isBlank()) orderStatus = "PENDING";
        createdAt = now; updatedAt = now;
    }
    @PreUpdate public void preUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getMemberId(){return memberId;} public void setMemberId(Long v){memberId=v;}
    public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
    public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;}
    public String getRecipientName(){return recipientName;} public void setRecipientName(String v){recipientName=v;}
    public String getRecipientPhone(){return recipientPhone;} public void setRecipientPhone(String v){recipientPhone=v;}
    public String getShippingAddress(){return shippingAddress;} public void setShippingAddress(String v){shippingAddress=v;}
    public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){paymentMethod=v;}
    public String getOrderStatus(){return orderStatus;} public void setOrderStatus(String v){orderStatus=v;}
    public String getNote(){return note;} public void setNote(String v){note=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}
