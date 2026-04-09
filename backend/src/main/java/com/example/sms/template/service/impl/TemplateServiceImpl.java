package com.example.sms.template.service.impl;
import com.example.sms.template.entity.Template; import com.example.sms.template.service.TemplateService; import org.springframework.stereotype.Service; import java.util.List;
@Service public class TemplateServiceImpl implements TemplateService { public List<Template> list(){ return List.of(new Template(21L,"气象灾害预警","预警通知")); } }
