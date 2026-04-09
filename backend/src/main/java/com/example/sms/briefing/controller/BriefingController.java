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
        return ApiResponse.success(service.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Briefing> get(@PathVariable Long id) {
        Briefing briefing = service.getById(id);
        if (briefing == null) {
            return ApiResponse.error(404, "简讯不存在");
        }
        return ApiResponse.success(briefing);
    }

    @PostMapping
    public ApiResponse<Briefing> create(@RequestBody Briefing briefing) {
        return ApiResponse.success(service.create(briefing));
    }

    @PutMapping("/{id}")
    public ApiResponse<Briefing> update(@PathVariable Long id, @RequestBody Briefing briefing) {
        Briefing payload = new Briefing(id, briefing.title(), briefing.content(), briefing.templateId(), briefing.status(), briefing.channel(), briefing.author(), briefing.version(), briefing.audience(), briefing.updatedAt(), briefing.createdBy());
        return ApiResponse.success(service.update(payload));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(service.delete(id));
    }

    @GetMapping("/search")
    public ApiResponse<List<Briefing>> search(@RequestParam String keyword) {
        return ApiResponse.success(service.search(keyword));
    }
}
