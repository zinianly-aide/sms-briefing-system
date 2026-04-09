package com.example.sms.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateContactRequest(
        @NotBlank(message = "姓名不能为空") String name,
        @NotBlank(message = "手机号不能为空") String mobile,
        String department,
        String title
) {
}
