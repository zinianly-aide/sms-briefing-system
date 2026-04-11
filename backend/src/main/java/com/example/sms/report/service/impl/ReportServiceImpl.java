package com.example.sms.report.service.impl;

import com.example.sms.contact.mapper.ContactMapper;
import com.example.sms.group.entity.ContactGroup;
import com.example.sms.group.mapper.GroupMapper;
import com.example.sms.report.dto.ReportOverviewResponse;
import com.example.sms.report.service.ReportService;
import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.mapper.SmsTaskMapper;
import com.example.sms.template.entity.Template;
import com.example.sms.template.mapper.TemplateMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final ContactMapper contactMapper;
    private final GroupMapper groupMapper;
    private final TemplateMapper templateMapper;
    private final SmsTaskMapper smsTaskMapper;

    public ReportServiceImpl(ContactMapper contactMapper, GroupMapper groupMapper, TemplateMapper templateMapper, SmsTaskMapper smsTaskMapper) {
        this.contactMapper = contactMapper;
        this.groupMapper = groupMapper;
        this.templateMapper = templateMapper;
        this.smsTaskMapper = smsTaskMapper;
    }

    @Override
    public ReportOverviewResponse overview() {
        List<ContactGroup> groups = groupMapper.selectAll();
        List<Template> templates = templateMapper.selectAll();
        List<SmsTask> tasks = smsTaskMapper.selectAll();
        int totalContacts = contactMapper.selectAll().size();
        int activeGroups = (int) groups.stream().filter(group -> "启用".equals(group.getStatus())).count();
        int templateCount = templates.size();
        int totalTasks = tasks.size();
        int pendingTasks = (int) tasks.stream().filter(task -> "待发送".equals(task.getStatus())).count();

        return new ReportOverviewResponse(
            totalContacts,
            activeGroups,
            templateCount,
            totalTasks,
            pendingTasks,
            groups,
            templates,
            tasks
        );
    }
}
