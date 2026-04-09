package com.example.sms.service;

import com.example.sms.dto.CreateBriefingRequest;
import com.example.sms.dto.CreateContactRequest;
import com.example.sms.dto.CreateGroupRequest;
import com.example.sms.dto.CreateTemplateRequest;
import com.example.sms.dto.DashboardSummaryResponse;
import com.example.sms.model.BriefingTask;
import com.example.sms.model.Contact;
import com.example.sms.model.ContactGroup;
import com.example.sms.model.TemplateCard;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MockDataService {

    private final AtomicLong taskIdGenerator = new AtomicLong(1000);
    private final AtomicLong groupIdGenerator = new AtomicLong(20);
    private final AtomicLong templateIdGenerator = new AtomicLong(40);
    private final AtomicLong contactIdGenerator = new AtomicLong(2000);

    private final List<ContactGroup> groups = new ArrayList<>(List.of(
            new ContactGroup(11L, "全员通讯录", "集团办公室", 1260, "2026-04-09 09:20", List.of("HR同步", "默认")),
            new ContactGroup(12L, "园区值班群组", "行政部", 86, "2026-04-09 11:00", List.of("轮班", "高优先级")),
            new ContactGroup(13L, "营销条线负责人", "市场中心", 42, "2026-04-08 18:30", List.of("手工维护"))
    ));

    private final List<TemplateCard> templates = new ArrayList<>(List.of(
            new TemplateCard(21L, "气象灾害预警", "预警通知", "【集团简讯】受强对流天气影响，请各单位检查值班安排并做好出行防护。", "2026-04-08 16:00", "启用中"),
            new TemplateCard(22L, "领导行程播报", "日报简讯", "【集团简讯】今日领导行程如下，请相关单位提前做好接待准备。", "2026-04-07 10:30", "启用中"),
            new TemplateCard(23L, "临时交通管制", "运营通知", "【集团简讯】因园区临时施工，西门道路将于18:00-22:00实施交通管制。", "2026-04-06 19:10", "待审核")
    ));

    private final List<Contact> contacts = new ArrayList<>(List.of(
            new Contact(1001L, "张晓晨", "13800001111", "集团办公室", "办公室主任", "在岗"),
            new Contact(1002L, "王钰", "13800001112", "行政部", "值班主管", "在岗"),
            new Contact(1003L, "陈诺", "13800001113", "市场中心", "营销负责人", "外出")
    ));

    private final List<BriefingTask> tasks = new ArrayList<>(List.of(
            new BriefingTask(301L, "台风天气预警简讯", "短信", "2026-04-09 14:00", "待发送", 842, "办公室值班员"),
            new BriefingTask(302L, "领导出访日报", "短信+企微", "2026-04-09 08:30", "已完成", 128, "综合秘书"),
            new BriefingTask(303L, "园区交通提醒", "短信", "2026-04-10 07:45", "草稿", 265, "行政专员")
    ));

    public DashboardSummaryResponse getDashboard() {
        return new DashboardSummaryResponse(contacts.size(), groups.size(), templates.size(), 2, groups, templates, tasks);
    }

    public List<BriefingTask> getTasks() {
        return tasks;
    }

    public BriefingTask createTask(CreateBriefingRequest request) {
        String planned = request.plannedSendTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        BriefingTask task = new BriefingTask(
                taskIdGenerator.incrementAndGet(),
                request.title(),
                request.channel() == null || request.channel().isBlank() ? "短信" : request.channel(),
                planned,
                "待发送",
                request.groupIds().size() * 100,
                "当前登录用户"
        );
        tasks.add(0, task);
        return task;
    }

    public List<ContactGroup> getGroups() {
        return groups;
    }

    public ContactGroup createGroup(CreateGroupRequest request) {
        ContactGroup group = new ContactGroup(
                groupIdGenerator.incrementAndGet(),
                request.name(),
                request.ownerDept() == null ? "未分配" : request.ownerDept(),
                0,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                request.tags() == null ? List.of("手工维护") : request.tags()
        );
        groups.add(0, group);
        return group;
    }

    public List<TemplateCard> getTemplates() {
        return templates;
    }

    public TemplateCard createTemplate(CreateTemplateRequest request) {
        TemplateCard template = new TemplateCard(
                templateIdGenerator.incrementAndGet(),
                request.name(),
                request.category(),
                request.content(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                "启用中"
        );
        templates.add(0, template);
        return template;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public Contact createContact(CreateContactRequest request) {
        Contact contact = new Contact(
                contactIdGenerator.incrementAndGet(),
                request.name(),
                request.mobile(),
                request.department() == null ? "未分配" : request.department(),
                request.title() == null ? "未设置" : request.title(),
                "在岗"
        );
        contacts.add(0, contact);
        return contact;
    }
}
