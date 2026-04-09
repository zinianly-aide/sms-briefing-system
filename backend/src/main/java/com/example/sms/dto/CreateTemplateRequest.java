package com.example.sms.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTemplateRequest(
        @NotBlank(message = "模板名称不能为空") String name,
        @NotBlank(message = "分类不能为空") String category,
        @NotBlank(message = "模板内容不能为空") String content
) {
}
