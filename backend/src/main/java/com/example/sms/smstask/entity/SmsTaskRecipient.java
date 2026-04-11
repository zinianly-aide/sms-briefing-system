package com.example.sms.smstask.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsTaskRecipient {
    private Long id;
    private Long taskId;
    private Long contactId;
    private String mobile;
    private String name;
    private String status;
    private LocalDateTime sentAt;
    private String errorMsg;

    public SmsTaskRecipient() {}

    public SmsTaskRecipient(Long id, Long taskId, Long contactId, String mobile, String name, String status, LocalDateTime sentAt, String errorMsg) {
        this.id = id;
        this.taskId = taskId;
        this.contactId = contactId;
        this.mobile = mobile;
        this.name = name;
        this.status = status;
        this.sentAt = sentAt;
        this.errorMsg = errorMsg;
    }
}
