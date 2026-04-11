package com.example.sms.contact.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContactEntity {
    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
    private String department;
    private String title;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ContactEntity() {}

    public ContactEntity(Long id, String name, String mobile, String department, String title, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.department = department;
        this.title = title;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
