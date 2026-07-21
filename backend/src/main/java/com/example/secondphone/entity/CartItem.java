package com.example.secondphone.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"}))
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "cart_id", nullable = false) private Cart cart;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(nullable = false) private Integer quantity;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2) private BigDecimal unitPrice;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @PrePersist void createTimestamps(){createdAt=updatedAt=LocalDateTime.now();}
    @PreUpdate void updateTimestamp(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public Cart getCart(){return cart;} public void setCart(Cart value){cart=value;}
    public Product getProduct(){return product;} public void setProduct(Product value){product=value;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer value){quantity=value;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal value){unitPrice=value;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
