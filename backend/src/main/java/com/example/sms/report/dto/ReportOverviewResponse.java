package com.example.sms.report.dto;

import com.example.sms.group.entity.ContactGroup;
import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.template.entity.Template;

import java.util.List;

public record ReportOverviewResponse(
    Integer totalContacts,
    Integer activeGroups,
    Integer templateCount,
    Integer totalTasks,
    Integer pendingTasks,
    List<ContactGroup> groups,
    List<Template> templates,
    List<SmsTask> tasks
) {}
