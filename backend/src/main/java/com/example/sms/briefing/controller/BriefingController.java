package com.example.sms.briefing.controller;
import com.example.sms.briefing.entity.Briefing;
import com.example.sms.briefing.service.BriefingService;
import com.example.sms.common.api.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/briefings")
public class BriefingController {
    private final BriefingService service;

    public BriefingController(BriefingService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Briefing>> list() {
        return ApiResponse.success(service.list());
    }
}
