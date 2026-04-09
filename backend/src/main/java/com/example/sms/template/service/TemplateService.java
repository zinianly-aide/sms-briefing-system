package com.example.sms.template.service;

import com.example.sms.template.entity.Template;

import java.util.List;

public interface TemplateService {
    List<Template> listAll();

    Template getById(Long id);

    Template create(Template template);

    Template update(Template template);

    boolean delete(Long id);

    List<Template> search(String keyword);
}
