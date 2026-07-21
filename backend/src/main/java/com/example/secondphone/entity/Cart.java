package com.example.secondphone.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "carts")
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "member_id", nullable = false, unique = true) private Member member;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true) private List<CartItem> items = new ArrayList<>();
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @PrePersist void createTimestamps(){createdAt=updatedAt=LocalDateTime.now();}
    @PreUpdate void updateTimestamp(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public Member getMember(){return member;} public void setMember(Member value){member=value;}
    public List<CartItem> getItems(){return items;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
