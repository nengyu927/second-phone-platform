package com.example.secondphone.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity(name="CustomerOrder") @Table(name="orders")
public class Order {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="member_id",nullable=false) private Member member;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) private Product product;
    @Column(nullable=false) private Integer quantity=1;
    @Column(name="unit_price",nullable=false,precision=12,scale=2) private BigDecimal unitPrice;
    @Column(name="total_amount",nullable=false,precision=12,scale=2) private BigDecimal totalAmount=BigDecimal.ZERO;
    @Column(name="order_status",nullable=false,length=30) private String orderStatus="PENDING";
    @Column(name="recipient_name",nullable=false,length=100) private String recipientName;
    @Column(name="recipient_phone",nullable=false,length=20) private String recipientPhone;
    @Column(name="shipping_address",nullable=false,length=300) private String shippingAddress;
    @Column(columnDefinition="TEXT") private String note;
    @Column(name="created_at",nullable=false,updatable=false) private LocalDateTime createdAt;
    @Column(name="updated_at",nullable=false) private LocalDateTime updatedAt;
    @PrePersist void prePersist(){LocalDateTime now=LocalDateTime.now();if(orderStatus==null||orderStatus.isBlank())orderStatus="PENDING";createdAt=now;updatedAt=now;}
    @PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Member getMember(){return member;} public void setMember(Member v){member=v;}
    public Product getProduct(){return product;} public void setProduct(Product v){product=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
    public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;}
    public String getOrderStatus(){return orderStatus;} public void setOrderStatus(String v){orderStatus=v;}
    public String getRecipientName(){return recipientName;} public void setRecipientName(String v){recipientName=v;}
    public String getRecipientPhone(){return recipientPhone;} public void setRecipientPhone(String v){recipientPhone=v;}
    public String getShippingAddress(){return shippingAddress;} public void setShippingAddress(String v){shippingAddress=v;}
    public String getNote(){return note;} public void setNote(String v){note=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}
