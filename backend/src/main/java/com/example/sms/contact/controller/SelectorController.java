package com.example.sms.contact.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.common.dto.PageResult;
import com.example.sms.contact.entity.ContactEntity;
import com.example.sms.contact.service.ContactService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/selector")
public class SelectorController {

    private final ContactService contactService;

    public SelectorController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/employees")
    public ApiResponse<PageResult<ContactEntity>> employees(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dept,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<ContactEntity> all = contactService.listAll();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.toLowerCase();
            all = all.stream()
                .filter(c -> (c.getName() != null && c.getName().toLowerCase().contains(kw))
                    || (c.getMobile() != null && c.getMobile().contains(kw)))
                .collect(Collectors.toList());
        }
        if (dept != null && !dept.isBlank()) {
            all = all.stream()
                .filter(c -> dept.equals(c.getDepartment()))
                .collect(Collectors.toList());
        }
        int total = all.size();
        int offset = (page - 1) * pageSize;
        List<ContactEntity> paged = all.stream()
            .skip(offset)
            .limit(pageSize)
            .collect(Collectors.toList());
        return ApiResponse.success(PageResult.of(paged, total, page, pageSize));
    }

    @GetMapping("/departments")
    public ApiResponse<List<String>> departments() {
        List<ContactEntity> all = contactService.listAll();
        List<String> depts = all.stream()
            .map(ContactEntity::getDepartment)
            .filter(d -> d != null && !d.isBlank())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        return ApiResponse.success(depts);
    }
}
