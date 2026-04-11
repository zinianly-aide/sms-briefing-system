package com.example.sms.audit.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.audit.entity.OperationLog;
import com.example.sms.audit.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<OperationLog>> list() {
        return ApiResponse.success(service.listAll());
    }

    @GetMapping("/module/{module}")
    public ApiResponse<List<OperationLog>> byModule(@PathVariable String module) {
        return ApiResponse.success(service.getByModule(module));
    }

    @GetMapping("/operator/{operator}")
    public ApiResponse<List<OperationLog>> byOperator(@PathVariable String operator) {
        return ApiResponse.success(service.getByOperator(operator));
    }
}
