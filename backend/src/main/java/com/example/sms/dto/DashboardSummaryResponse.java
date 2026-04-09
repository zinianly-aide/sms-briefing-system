package com.example.sms.dto;

import com.example.sms.model.BriefingTask;
import com.example.sms.model.ContactGroup;
import com.example.sms.model.TemplateCard;

import java.util.List;

public record DashboardSummaryResponse(int totalContacts, int activeGroups, int templateCount,
                                       int pendingTasks, List<ContactGroup> groups,
                                       List<TemplateCard> templates, List<BriefingTask> tasks) {
}
