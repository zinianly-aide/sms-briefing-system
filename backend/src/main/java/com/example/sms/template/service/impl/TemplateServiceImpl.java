package com.example.sms.template.service.impl;

import com.example.sms.common.dto.PageResult;
import com.example.sms.template.entity.Template;
import com.example.sms.template.mapper.TemplateMapper;
import com.example.sms.template.service.TemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {
    private final TemplateMapper templateMapper;

    public TemplateServiceImpl(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    @Override
    public PageResult<Template> listPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Template> list = templateMapper.selectPage(pageSize, offset);
        int total = templateMapper.count();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public List<Template> listAll() {
        return templateMapper.selectAll();
    }

    @Override
    public Template getById(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    @Transactional
    public Template create(Template template) {
        Template created = new Template(null, template.getName(), template.getCategory(), template.getContent(), template.getStatus(), template.getOwner(), template.getDefaultGroupIds(), LocalDateTime.now());
        templateMapper.insert(created);
        return created;
    }

    @Override
    @Transactional
    public Template update(Template template) {
        Template existing = getById(template.getId());
        if (existing == null) {
            throw new RuntimeException("模板不存在");
        }
        Template updated = new Template(template.getId(), template.getName(), template.getCategory(), template.getContent(), template.getStatus(), template.getOwner(), template.getDefaultGroupIds(), LocalDateTime.now());
        templateMapper.update(updated);
        return updated;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return templateMapper.deleteById(id) > 0;
    }

    @Override
    public List<Template> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return listAll();
        }
        return templateMapper.search(keyword);
    }

    @Override
    public PageResult<Template> searchPaged(String keyword, int page, int pageSize) {
        if (!StringUtils.hasText(keyword)) {
            return listPaged(page, pageSize);
        }
        int offset = (page - 1) * pageSize;
        List<Template> list = templateMapper.searchPage(keyword, pageSize, offset);
        int total = templateMapper.countByKeyword(keyword);
        return PageResult.of(list, total, page, pageSize);
    }
}
