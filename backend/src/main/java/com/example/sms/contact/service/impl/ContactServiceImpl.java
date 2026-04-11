package com.example.sms.contact.service.impl;

import com.example.sms.common.dto.PageResult;
import com.example.sms.common.exception.BusinessException;
import com.example.sms.contact.entity.ContactEntity;
import com.example.sms.contact.mapper.ContactMapper;
import com.example.sms.contact.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContactServiceImpl implements ContactService {
    
    @Autowired
    private ContactMapper contactMapper;
    
    @Override
    public PageResult<ContactEntity> listPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ContactEntity> list = contactMapper.selectPage(pageSize, offset);
        int total = contactMapper.count();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public List<ContactEntity> listAll() {
        return contactMapper.selectAll();
    }
    
    @Override
    public ContactEntity getById(Long id) {
        return contactMapper.selectById(id);
    }
    
    @Override
    public ContactEntity getByMobile(String mobile) {
        return contactMapper.selectByMobile(mobile);
    }
    
    @Override
    @Transactional
    public ContactEntity create(ContactEntity contact) {
        LocalDateTime now = LocalDateTime.now();
        contact = new ContactEntity(
            null, // ID 由数据库生成
            contact.getName(),
            contact.getMobile(),
            contact.getDepartment(),
            contact.getTitle(),
            contact.getStatus(),
            now,
            now
        );
        
        int result = contactMapper.insert(contact);
        if (result > 0) {
            return contact;
        }
        throw new BusinessException(500, "创建联系人失败");
    }

    @Override
    @Transactional
    public ContactEntity update(ContactEntity contact) {
        ContactEntity existing = getById(contact.getId());
        if (existing == null) {
            throw new BusinessException(404, "联系人不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        ContactEntity updated = new ContactEntity(
            contact.getId(),
            contact.getName(),
            contact.getMobile(),
            contact.getDepartment(),
            contact.getTitle(),
            contact.getStatus(),
            existing.getCreatedAt(),
            now
        );
        
        int result = contactMapper.update(updated);
        if (result > 0) {
            return updated;
        }
        throw new BusinessException(500, "更新联系人失败");
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        ContactEntity existing = getById(id);
        if (existing == null) {
            throw new BusinessException(404, "联系人不存在");
        }
        
        int result = contactMapper.deleteById(id);
        return result > 0;
    }
    
    @Override
    public List<ContactEntity> getByDepartment(String department) {
        return contactMapper.selectByDepartment(department);
    }
    
    @Override
    public List<ContactEntity> getByStatus(String status) {
        return contactMapper.selectByStatus(status);
    }
    
    @Override
    public List<ContactEntity> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return listAll();
        }
        return contactMapper.searchByKeyword(keyword);
    }

    @Override
    public PageResult<ContactEntity> searchPaged(String keyword, int page, int pageSize) {
        if (!StringUtils.hasText(keyword)) {
            return listPaged(page, pageSize);
        }
        int offset = (page - 1) * pageSize;
        List<ContactEntity> list = contactMapper.searchByKeywordPage(keyword, pageSize, offset);
        int total = contactMapper.countByKeyword(keyword);
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    @Transactional
    public Map<String, Object> importCsv(byte[] csvBytes) {
        int success = 0;
        int fail = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(csvBytes), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) {
                return Map.of("success", 0, "fail", 0, "errors", List.of("文件为空"));
            }

            String line;
            int rowNum = 1;
            while ((line = reader.readLine()) != null) {
                rowNum++;
                String[] parts = line.split(",", -1);
                try {
                    if (parts.length < 2) {
                        errors.add("第" + rowNum + "行: 列数不足");
                        fail++;
                        continue;
                    }
                    String name = parts[0].trim();
                    String mobile = parts[1].trim();
                    String department = parts.length > 2 ? parts[2].trim() : "";
                    String title = parts.length > 3 ? parts[3].trim() : "";

                    if (!StringUtils.hasText(name)) {
                        errors.add("第" + rowNum + "行: 姓名为空");
                        fail++;
                        continue;
                    }
                    if (!mobile.matches("^1[3-9]\\d{9}$")) {
                        errors.add("第" + rowNum + "行: 手机号格式不正确 - " + mobile);
                        fail++;
                        continue;
                    }

                    ContactEntity contact = new ContactEntity(null, name, mobile,
                        StringUtils.hasText(department) ? department : null,
                        StringUtils.hasText(title) ? title : null,
                        "active", LocalDateTime.now(), LocalDateTime.now());
                    contactMapper.insert(contact);
                    success++;
                } catch (Exception e) {
                    errors.add("第" + rowNum + "行: " + e.getMessage());
                    fail++;
                }
            }
        } catch (IOException e) {
            return Map.of("success", 0, "fail", 0, "errors", List.of("文件读取失败: " + e.getMessage()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("fail", fail);
        result.put("errors", errors);
        return result;
    }

    @Override
    public byte[] exportCsv() {
        List<ContactEntity> contacts = listAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write("姓名,手机号,部门,职位,状态\n".getBytes(StandardCharsets.UTF_8));
            for (ContactEntity c : contacts) {
                StringBuilder sb = new StringBuilder();
                sb.append(c.getName() != null ? c.getName() : "").append(",");
                sb.append(c.getMobile() != null ? c.getMobile() : "").append(",");
                sb.append(c.getDepartment() != null ? c.getDepartment() : "").append(",");
                sb.append(c.getTitle() != null ? c.getTitle() : "").append(",");
                sb.append(c.getStatus() != null ? c.getStatus() : "active").append("\n");
                out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new BusinessException(500, "生成CSV失败");
        }
        return out.toByteArray();
    }
}
