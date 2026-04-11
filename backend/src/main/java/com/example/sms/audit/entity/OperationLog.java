package com.example.sms.audit.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private String module;
    private String action;
    private String operator;
    private String detail;
    private String ip;
    private LocalDateTime createdAt;

    public OperationLog() {}

    public OperationLog(Long id, String module, String action, String operator, String detail, String ip, LocalDateTime createdAt) {
        this.id = id;
        this.module = module;
        this.action = action;
        this.operator = operator;
        this.detail = detail;
        this.ip = ip;
        this.createdAt = createdAt;
    }
}
