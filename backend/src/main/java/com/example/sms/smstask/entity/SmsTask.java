package com.example.sms.smstask.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsTask {
    private Long id;

    @NotBlank(message = "任务标题不能为空")
    private String title;

    @NotBlank(message = "发送渠道不能为空")
    private String channel;
    private LocalDateTime plannedSendTime;

    @NotBlank(message = "任务状态不能为空")
    private String status;
    private Integer recipientCount;
    private String creator;
    private String successRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SmsTask() {}

    public SmsTask(Long id, String title, String channel, LocalDateTime plannedSendTime, String status, Integer recipientCount, String creator, String successRate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.channel = channel;
        this.plannedSendTime = plannedSendTime;
        this.status = status;
        this.recipientCount = recipientCount;
        this.creator = creator;
        this.successRate = successRate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
