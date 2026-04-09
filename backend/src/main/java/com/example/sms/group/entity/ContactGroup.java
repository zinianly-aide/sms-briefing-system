package com.example.sms.group.entity;

import java.time.LocalDateTime;

public record ContactGroup(
    Long id,
    String name,
    String ownerDept,
    Integer memberCount,
    String tags,
    LocalDateTime lastSyncTime,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
