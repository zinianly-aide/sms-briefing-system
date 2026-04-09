package com.example.sms.report.controller;
import com.example.sms.common.api.ApiResponse; import com.example.sms.report.dto.ReportOverviewResponse; import com.example.sms.report.service.ReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/reports","/api/dashboard"})
public class ReportController {
    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<ReportOverviewResponse> overview() {
        return ApiResponse.success(service.overview());
    }
}
