package com.example.sms.briefing.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Briefing {
    private Long id;

    @NotBlank(message = "简讯标题不能为空")
    private String title;

    @NotBlank(message = "简讯内容不能为空")
    private String content;
    private Long templateId;

    @NotBlank(message = "简讯状态不能为空")
    private String status;
    private String channel;
    private String author;
    private String version;
    private String audience;
    private LocalDateTime updatedAt;
    private String createdBy;
    private LocalDateTime createdAt;
    private String disasterType;
    private String disasterLevel;
    private String contentPart2;
    private String remark;
    private String legacyPayload;

    public Briefing() {}

    public Briefing(Long id, String title, String content, Long templateId, String status, String channel, String author, String version, String audience, LocalDateTime updatedAt, String createdBy, LocalDateTime createdAt, String disasterType, String disasterLevel, String contentPart2, String remark, String legacyPayload) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.templateId = templateId;
        this.status = status;
        this.channel = channel;
        this.author = author;
        this.version = version;
        this.audience = audience;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.disasterType = disasterType;
        this.disasterLevel = disasterLevel;
        this.contentPart2 = contentPart2;
        this.remark = remark;
        this.legacyPayload = legacyPayload;
    }
}
