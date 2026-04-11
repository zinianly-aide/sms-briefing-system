package com.example.sms.template.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.common.dto.PageResult;
import com.example.sms.template.entity.Template;
import com.example.sms.template.service.TemplateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {
    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResult<Template>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Template> list = service.listAll();
        return ApiResponse.success(PageResult.of(list, list.size(), page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<Template> get(@PathVariable Long id) {
        Template template = service.getById(id);
        if (template == null) {
            return ApiResponse.error(404, "模板不存在");
        }
        return ApiResponse.success(template);
    }

    @PostMapping
    public ApiResponse<Template> create(@Valid @RequestBody Template template) {
        return ApiResponse.success(service.create(template));
    }

    @PutMapping("/{id}")
    public ApiResponse<Template> update(@PathVariable Long id, @Valid @RequestBody Template template) {
        Template payload = new Template(id, template.getName(), template.getCategory(), template.getContent(), template.getStatus(), template.getOwner(), template.getDefaultGroupIds(), template.getUpdatedAt());
        return ApiResponse.success(service.update(payload));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(service.delete(id));
    }

    @GetMapping("/search")
    public ApiResponse<List<Template>> search(@RequestParam String keyword) {
        return ApiResponse.success(service.search(keyword));
    }
}
