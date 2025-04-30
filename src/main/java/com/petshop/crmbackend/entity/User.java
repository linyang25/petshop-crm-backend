package com.petshop.crmbackend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 数据库自增
    private Long id; // 主键id（自增）

    @Column(name = "user_id", nullable = false)
    private String userId; // 五位随机数

    @Column(nullable = false)
    private String username; // 用户名，必填

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // 加密密码，必填

    @Column(name = "full_name", nullable = false)
    private String fullName; // 真实姓名，必填

    @Column(nullable = false)
    private String email; // 邮箱，必填

    @Column(nullable = false)
    private String phone; // 手机号，必填

    private String role; // 角色 (选填)

    private String status; // 状态 (选填)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

    // 必须的无参构造器
    public User() {
    }

    // 有参构造器（自动生成userId）
    public User(String username, String email, String passwordHash, String phone, String fullName, String role, String status) {
        this.userId = generateRandomUserId();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    private String generateRandomUserId() {
        Random random = new Random();
        return String.format("%05d", random.nextInt(100000)); // 生成5位数
    }


    // Getter & Setter 方法
    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
