package com.example.sms.briefing.controller;

import com.example.sms.briefing.entity.Briefing;
import com.example.sms.briefing.service.BriefingService;
import com.example.sms.common.api.ApiResponse;
import com.example.sms.common.dto.PageResult;
import jakarta.validation.Valid;
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
    public ApiResponse<PageResult<Briefing>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(service.listPaged(page, pageSize));
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
    public ApiResponse<Briefing> create(@Valid @RequestBody Briefing briefing) {
        return ApiResponse.success(service.create(briefing));
    }

    @PutMapping("/{id}")
    public ApiResponse<Briefing> update(@PathVariable Long id, @Valid @RequestBody Briefing briefing) {
        Briefing payload = new Briefing(id, briefing.getTitle(), briefing.getContent(), briefing.getTemplateId(), briefing.getStatus(), briefing.getChannel(), briefing.getAuthor(), briefing.getVersion(), briefing.getAudience(), briefing.getUpdatedAt(), briefing.getCreatedBy(), briefing.getCreatedAt(), briefing.getDisasterType(), briefing.getDisasterLevel(), briefing.getContentPart2(), briefing.getRemark(), briefing.getLegacyPayload());
        return ApiResponse.success(service.update(payload));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(service.delete(id));
    }

    @GetMapping("/search")
    public ApiResponse<PageResult<Briefing>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(service.searchPaged(keyword, page, pageSize));
    }

    @PostMapping("/{id}/clone")
    public ApiResponse<Briefing> clone(@PathVariable Long id) {
        Briefing original = service.getById(id);
        if (original == null) {
            return ApiResponse.error(404, "简讯不存在");
        }
        Briefing clone = new Briefing(null, original.getTitle() + " (副本)", original.getContent(), original.getTemplateId(),
            "草稿", original.getChannel(), original.getAuthor(), "V1.0", original.getAudience(),
            null, original.getCreatedBy(), null, original.getDisasterType(), original.getDisasterLevel(),
            original.getContentPart2(), original.getRemark(), null);
        return ApiResponse.success(service.create(clone));
    }
}
