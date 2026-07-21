package com.example.secondphone.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private InventoryMovementType type;
    @Column(name = "change_quantity", nullable = false)
    private int changeQuantity;
    @Column(name = "before_stock", nullable = false)
    private int beforeStock;
    @Column(name = "after_stock", nullable = false)
    private int afterStock;
    @Column(nullable = false, length = 255)
    private String reason;
    @Column(name = "operator_username", nullable = false, length = 100)
    private String operatorUsername;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist void prePersist() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public InventoryMovementType getType() { return type; }
    public void setType(InventoryMovementType type) { this.type = type; }
    public int getChangeQuantity() { return changeQuantity; }
    public void setChangeQuantity(int value) { changeQuantity = value; }
    public int getBeforeStock() { return beforeStock; }
    public void setBeforeStock(int value) { beforeStock = value; }
    public int getAfterStock() { return afterStock; }
    public void setAfterStock(int value) { afterStock = value; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getOperatorUsername() { return operatorUsername; }
    public void setOperatorUsername(String value) { operatorUsername = value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
