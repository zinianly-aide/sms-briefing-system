package com.example.sms.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public record CreateBriefingRequest(
        @NotBlank(message = "标题不能为空") String title,
        @NotBlank(message = "内容不能为空") String content,
        @NotEmpty(message = "至少选择一个发送群组") List<Long> groupIds,
        Long templateId,
        @Future(message = "预约发送时间必须晚于当前时间") LocalDateTime plannedSendTime,
        String channel
) {
}
