package com.example.sms.service;

import com.example.sms.dto.CreateBriefingRequest;
import com.example.sms.dto.DashboardSummaryResponse;
import com.example.sms.model.BriefingTask;
import com.example.sms.model.ContactGroup;
import com.example.sms.model.TemplateCard;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MockDataService {

    private final AtomicLong taskIdGenerator = new AtomicLong(1000);
    private final List<BriefingTask> tasks = new ArrayList<>(List.of(
            new BriefingTask(301L, "台风天气预警简讯", "短信", "2026-04-09 14:00", "待发送", 842, "办公室值班员"),
            new BriefingTask(302L, "领导出访日报", "短信+企微", "2026-04-09 08:30", "已完成", 128, "综合秘书"),
            new BriefingTask(303L, "园区交通提醒", "短信", "2026-04-10 07:45", "草稿", 265, "行政专员")
    ));

    public DashboardSummaryResponse getDashboard() {
        List<ContactGroup> groups = List.of(
                new ContactGroup(11L, "全员通讯录", "集团办公室", 1260, "2026-04-09 09:20", List.of("HR同步", "默认")),
                new ContactGroup(12L, "园区值班群组", "行政部", 86, "2026-04-09 11:00", List.of("轮班", "高优先级")),
                new ContactGroup(13L, "营销条线负责人", "市场中心", 42, "2026-04-08 18:30", List.of("手工维护"))
        );

        List<TemplateCard> templates = List.of(
                new TemplateCard(21L, "气象灾害预警", "预警通知", "【集团简讯】受强对流天气影响，请各单位检查值班安排并做好出行防护。", "2026-04-08 16:00", "启用中"),
                new TemplateCard(22L, "领导行程播报", "日报简讯", "【集团简讯】今日领导行程如下，请相关单位提前做好接待准备。", "2026-04-07 10:30", "启用中"),
                new TemplateCard(23L, "临时交通管制", "运营通知", "【集团简讯】因园区临时施工，西门道路将于18:00-22:00实施交通管制。", "2026-04-06 19:10", "待审核")
        );

        return new DashboardSummaryResponse(1688, groups.size(), templates.size(), 2, groups, templates, tasks);
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
}
