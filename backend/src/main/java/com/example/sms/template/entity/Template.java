package com.example.sms.template.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Template {
    private Long id;

    @NotBlank(message = "模板名称不能为空")
    private String name;

    @NotBlank(message = "模板分类不能为空")
    private String category;

    @NotBlank(message = "模板内容不能为空")
    private String content;
    private String status;
    private String owner;
    private String defaultGroupIds;
    private LocalDateTime updatedAt;

    public Template() {}

    public Template(Long id, String name, String category, String content, String status, String owner, String defaultGroupIds, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.content = content;
        this.status = status;
        this.owner = owner;
        this.defaultGroupIds = defaultGroupIds;
        this.updatedAt = updatedAt;
    }
}
