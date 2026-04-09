package com.example.sms.template.entity;

import java.time.LocalDateTime;

public record Template(
    Long id,
    String name,
    String category,
    String content,
    String status,
    String owner,
    LocalDateTime updatedAt
) {}
