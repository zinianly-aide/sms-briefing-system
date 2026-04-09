package com.example.sms.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateGroupRequest(
        @NotBlank(message = "群组名称不能为空") String name,
        String ownerDept,
        List<String> tags
) {
}
