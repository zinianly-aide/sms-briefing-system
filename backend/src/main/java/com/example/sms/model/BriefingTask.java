package com.example.sms.model;

public record BriefingTask(Long id, String title, String channel, String plannedSendTime, String status,
                           int recipientCount, String creator) {
}
