package com.example.sms.group.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupMember {
    private Long id;
    private Long groupId;
    private Long contactId;
    private String role;
    private LocalDateTime joinedAt;

    // Joined contact info (for display)
    private transient String contactName;
    private transient String contactMobile;
    private transient String contactDepartment;

    public GroupMember() {}

    public GroupMember(Long id, Long groupId, Long contactId, String role, LocalDateTime joinedAt) {
        this.id = id;
        this.groupId = groupId;
        this.contactId = contactId;
        this.role = role;
        this.joinedAt = joinedAt;
    }
}
