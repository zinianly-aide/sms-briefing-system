package com.example.sms.config.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemConfig {
    private Long id;
    private String configKey;
    private String configValue;
    private String configDesc;
    private LocalDateTime updatedAt;

    public SystemConfig() {}

    public SystemConfig(Long id, String configKey, String configValue, String configDesc, LocalDateTime updatedAt) {
        this.id = id;
        this.configKey = configKey;
        this.configValue = configValue;
        this.configDesc = configDesc;
        this.updatedAt = updatedAt;
    }
}
