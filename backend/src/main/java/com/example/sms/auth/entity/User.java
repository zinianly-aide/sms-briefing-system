package com.example.sms.auth.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String displayName;
    private String role;
    private LocalDateTime createdAt;

    public User() {}

    public User(Long id, String username, String password, String displayName, String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
        this.createdAt = createdAt;
    }
}
