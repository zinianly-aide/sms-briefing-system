package com.example.sms.config.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.config.entity.SystemConfig;
import com.example.sms.config.service.SystemConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configs")
public class SystemConfigController {

    private final SystemConfigService service;

    public SystemConfigController(SystemConfigService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<SystemConfig>> list() {
        return ApiResponse.success(service.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<SystemConfig> get(@PathVariable Long id) {
        SystemConfig config = service.getById(id);
        if (config == null) {
            return ApiResponse.error(404, "配置项不存在");
        }
        return ApiResponse.success(config);
    }

    @PostMapping
    public ApiResponse<SystemConfig> create(@RequestBody SystemConfig config) {
        return ApiResponse.success(service.create(config));
    }

    @PutMapping("/{id}")
    public ApiResponse<SystemConfig> update(@PathVariable Long id, @RequestBody SystemConfig config) {
        config.setId(id);
        return ApiResponse.success(service.update(config));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(service.delete(id));
    }
}
