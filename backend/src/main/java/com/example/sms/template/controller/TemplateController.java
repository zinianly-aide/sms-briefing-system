package com.example.sms.template.controller;
import com.example.sms.common.api.ApiResponse; import com.example.sms.template.entity.Template; import com.example.sms.template.service.TemplateService;
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
    public ApiResponse<List<Template>> list() {
        return ApiResponse.success(service.list());
    }
}
