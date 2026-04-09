package com.example.sms.smstask.entity;

import java.time.LocalDateTime;

public record SmsTask(
    Long id,
    String title,
    String channel,
    LocalDateTime plannedSendTime,
    String status,
    Integer recipientCount,
    String creator,
    String successRate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
