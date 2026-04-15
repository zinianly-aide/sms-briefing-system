package com.example.sms.template.service;

import com.example.sms.common.dto.PageResult;
import com.example.sms.template.entity.Template;

import java.util.List;

public interface TemplateService {
    PageResult<Template> listPaged(int page, int pageSize);

    List<Template> listAll();

    Template getById(Long id);

    Template create(Template template);

    Template update(Template template);

    boolean delete(Long id);

    List<Template> search(String keyword);

    PageResult<Template> searchPaged(String keyword, int page, int pageSize);
}
