package com.example.sms.contact.entity;

import java.time.LocalDateTime;

public record ContactEntity(
    Long id,
    String name,
    String mobile,
    String department,
    String title,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
