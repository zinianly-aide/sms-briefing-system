package com.example.sms.contact.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.contact.service.HrSyncService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hr")
public class HrSyncController {

    private final HrSyncService hrSyncService;

    public HrSyncController(HrSyncService hrSyncService) {
        this.hrSyncService = hrSyncService;
    }

    @PostMapping("/sync")
    public ApiResponse<Map<String, Object>> sync() {
        try {
            return ApiResponse.success(hrSyncService.sync());
        } catch (Exception e) {
            return ApiResponse.error(500, "同步失败: " + e.getMessage());
        }
    }
}
