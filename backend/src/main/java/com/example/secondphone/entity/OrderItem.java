package com.example.secondphone.entity;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private Order order;
    @Column(name = "product_id", nullable = false) private Long productId;
    @Column(name = "product_code", length = 40) private String productCode;
    @Column(name = "product_name", nullable = false, length = 150) private String productName;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2) private BigDecimal unitPrice;
    @Column(nullable = false) private Integer quantity;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal subtotal;
    public Long getId(){return id;} public Order getOrder(){return order;} public void setOrder(Order value){order=value;}
    public Long getProductId(){return productId;} public void setProductId(Long value){productId=value;}
    public String getProductCode(){return productCode;} public void setProductCode(String value){productCode=value;}
    public String getProductName(){return productName;} public void setProductName(String value){productName=value;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal value){unitPrice=value;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer value){quantity=value;}
    public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal value){subtotal=value;}
}
