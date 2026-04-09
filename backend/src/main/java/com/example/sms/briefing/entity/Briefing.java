package com.example.sms.briefing.entity;

import java.time.LocalDateTime;

public record Briefing(
    Long id,
    String title,
    String content,
    Long templateId,
    String status,
    String channel,
    String author,
    String version,
    String audience,
    LocalDateTime updatedAt,
    String createdBy
) {}
