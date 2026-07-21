package com.example.secondphone.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Convert;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String account;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Email
    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Convert(converter = MemberRoleConverter.class)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Convert(converter = MemberStatusConverter.class)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (role == null) {
            role = MemberRole.CUSTOMER;
        }
        if (status == null) {
            status = MemberStatus.ACTIVE;
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role == null ? null : role.name();
    }

    public MemberRole getRoleEnum() { return role; }

    public void setRole(MemberRole role) {
        this.role = role;
    }

    public void setRole(String role) {
        this.role = "USER".equalsIgnoreCase(role)
                ? MemberRole.CUSTOMER
                : MemberRole.valueOf(role.toUpperCase());
    }

    public String getStatus() {
        return status == null ? null : status.name();
    }

    public MemberStatus getStatusEnum() { return status; }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = "INACTIVE".equalsIgnoreCase(status)
                ? MemberStatus.DISABLED
                : MemberStatus.valueOf(status.toUpperCase());
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
