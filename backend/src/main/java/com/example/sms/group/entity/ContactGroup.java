package com.example.sms.group.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContactGroup {
    private Long id;

    @NotBlank(message = "群组名称不能为空")
    private String name;
    private String ownerDept;
    private Integer memberCount;
    private String tags;
    private LocalDateTime lastSyncTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ContactGroup() {}

    public ContactGroup(Long id, String name, String ownerDept, Integer memberCount, String tags, LocalDateTime lastSyncTime, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.ownerDept = ownerDept;
        this.memberCount = memberCount;
        this.tags = tags;
        this.lastSyncTime = lastSyncTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
