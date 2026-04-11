package com.example.sms.contact.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.common.dto.PageResult;
import com.example.sms.contact.entity.ContactEntity;
import com.example.sms.contact.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactModuleController {
    
    private final ContactService service;

    public ContactModuleController(ContactService service) {
        this.service = service;
    }
    
    /**
     * 获取所有联系人列表
     */
    @GetMapping
    public ApiResponse<PageResult<ContactEntity>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<ContactEntity> list = service.listAll();
        return ApiResponse.success(PageResult.of(list, list.size(), page, pageSize));
    }
    
    /**
     * 根据ID获取联系人详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ContactEntity> getById(@PathVariable Long id) {
        ContactEntity contact = service.getById(id);
        if (contact == null) {
            return ApiResponse.error(404, "联系人不存在");
        }
        return ApiResponse.success(contact);
    }
    
    /**
     * 创建联系人
     */
    @PostMapping
    public ApiResponse<ContactEntity> create(@Valid @RequestBody ContactEntity contact) {
        try {
            ContactEntity created = service.create(contact);
            return ApiResponse.success(created);
        } catch (Exception e) {
            return ApiResponse.error(500, "创建联系人失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新联系人
     */
    @PutMapping("/{id}")
    public ApiResponse<ContactEntity> update(@PathVariable Long id, @Valid @RequestBody ContactEntity contact) {
        try {
            contact = new ContactEntity(id, contact.getName(), contact.getMobile(),
                contact.getDepartment(), contact.getTitle(), contact.getStatus(),
                contact.getCreatedAt(), contact.getUpdatedAt());
            ContactEntity updated = service.update(contact);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            return ApiResponse.error(500, "更新联系人失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除联系人
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        try {
            boolean result = service.delete(id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, "删除联系人失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据部门查询联系人
     */
    @GetMapping("/department/{department}")
    public ApiResponse<List<ContactEntity>> getByDepartment(@PathVariable String department) {
        return ApiResponse.success(service.getByDepartment(department));
    }
    
    /**
     * 根据状态查询联系人
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<ContactEntity>> getByStatus(@PathVariable String status) {
        return ApiResponse.success(service.getByStatus(status));
    }
    
    /**
     * 搜索联系人
     */
    @GetMapping("/search")
    public ApiResponse<List<ContactEntity>> search(@RequestParam String keyword) {
        return ApiResponse.success(service.search(keyword));
    }

    /**
     * 导入联系人（CSV）
     */
    @PostMapping("/import")
    public ApiResponse<java.util.Map<String, Object>> importContacts(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error(400, "文件为空");
            }
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".csv") && !filename.endsWith(".CSV"))) {
                return ApiResponse.error(400, "仅支持CSV文件");
            }
            java.util.Map<String, Object> result = service.importCsv(file.getBytes());
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, "导入失败: " + e.getMessage());
        }
    }

    /**
     * 导出联系人为CSV
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportContacts() {
        byte[] csvBytes = service.exportCsv();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.csv")
            .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
            .body(csvBytes);
    }
}
