package com.example.sms.contact.service.impl;

import com.example.sms.contact.entity.ContactEntity;
import com.example.sms.contact.mapper.ContactMapper;
import com.example.sms.contact.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {
    
    @Autowired
    private ContactMapper contactMapper;
    
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
            contact.name(),
            contact.mobile(),
            contact.department(),
            contact.title(),
            contact.status(),
            now,
            now
        );
        
        int result = contactMapper.insert(contact);
        if (result > 0) {
            return contact;
        }
        throw new RuntimeException("创建联系人失败");
    }
    
    @Override
    @Transactional
    public ContactEntity update(ContactEntity contact) {
        ContactEntity existing = getById(contact.id());
        if (existing == null) {
            throw new RuntimeException("联系人不存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        ContactEntity updated = new ContactEntity(
            contact.id(),
            contact.name(),
            contact.mobile(),
            contact.department(),
            contact.title(),
            contact.status(),
            existing.createdAt(),
            now
        );
        
        int result = contactMapper.update(updated);
        if (result > 0) {
            return updated;
        }
        throw new RuntimeException("更新联系人失败");
    }
    
    @Override
    @Transactional
    public boolean delete(Long id) {
        ContactEntity existing = getById(id);
        if (existing == null) {
            throw new RuntimeException("联系人不存在");
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
}
