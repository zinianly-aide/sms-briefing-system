package com.example.sms.contact.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.common.constant.DomainValueValidator;
import com.example.sms.common.dto.PageResult;
import com.example.sms.contact.entity.ContactEntity;
import com.example.sms.contact.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactModuleController {

    private final ContactService service;

    public ContactModuleController(ContactService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResult<ContactEntity>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(service.listPaged(page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ContactEntity> getById(@PathVariable Long id) {
        ContactEntity contact = service.getById(id);
        if (contact == null) {
            return ApiResponse.error(404, "联系人不存在");
        }
        return ApiResponse.success(contact);
    }

    @PostMapping
    public ApiResponse<ContactEntity> create(@Valid @RequestBody ContactEntity contact) {
        DomainValueValidator.validateContactStatus(contact.getStatus());
        return ApiResponse.success(service.create(contact));
    }

    @PutMapping("/{id}")
    public ApiResponse<ContactEntity> update(@PathVariable Long id, @Valid @RequestBody ContactEntity contact) {
        DomainValueValidator.validateContactStatus(contact.getStatus());
        contact = new ContactEntity(id, contact.getName(), contact.getMobile(),
            contact.getDepartment(), contact.getTitle(), contact.getStatus(),
            contact.getCreatedAt(), contact.getUpdatedAt());
        return ApiResponse.success(service.update(contact));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(service.delete(id));
    }

    @GetMapping("/department/{department}")
    public ApiResponse<List<ContactEntity>> getByDepartment(@PathVariable String department) {
        return ApiResponse.success(service.getByDepartment(department));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<ContactEntity>> getByStatus(@PathVariable String status) {
        DomainValueValidator.validateContactStatus(status);
        return ApiResponse.success(service.getByStatus(status));
    }

    @GetMapping("/search")
    public ApiResponse<PageResult<ContactEntity>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(service.searchPaged(keyword, page, pageSize));
    }

    @PostMapping("/import")
    public ApiResponse<java.util.Map<String, Object>> importContacts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error(400, "文件为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".csv") && !filename.endsWith(".CSV"))) {
            return ApiResponse.error(400, "仅支持CSV文件");
        }
        try {
            java.util.Map<String, Object> result = service.importCsv(file.getBytes());
            return ApiResponse.success(result);
        } catch (IOException e) {
            return ApiResponse.error(500, "读取文件失败");
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportContacts() {
        byte[] csvBytes = service.exportCsv();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.csv")
            .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
            .body(csvBytes);
    }
}
