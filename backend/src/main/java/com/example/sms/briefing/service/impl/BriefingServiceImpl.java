package com.example.sms.briefing.service.impl;

import com.example.sms.briefing.entity.Briefing;
import com.example.sms.briefing.mapper.BriefingMapper;
import com.example.sms.briefing.service.BriefingService;
import com.example.sms.common.constant.DomainStatus;
import com.example.sms.common.dto.PageResult;
import com.example.sms.common.exception.BusinessException;
import com.example.sms.group.entity.GroupMember;
import com.example.sms.group.service.GroupMemberService;
import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.entity.SmsTaskRecipient;
import com.example.sms.smstask.mapper.SmsTaskRecipientMapper;
import com.example.sms.smstask.service.SmsTaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class BriefingServiceImpl implements BriefingService {
    private static final Logger log = LoggerFactory.getLogger(BriefingServiceImpl.class);

    private final BriefingMapper briefingMapper;
    private final SmsTaskService smsTaskService;
    private final GroupMemberService groupMemberService;
    private final SmsTaskRecipientMapper smsTaskRecipientMapper;
    private final ObjectMapper objectMapper;

    public BriefingServiceImpl(BriefingMapper briefingMapper,
                               SmsTaskService smsTaskService,
                               GroupMemberService groupMemberService,
                               SmsTaskRecipientMapper smsTaskRecipientMapper,
                               ObjectMapper objectMapper) {
        this.briefingMapper = briefingMapper;
        this.smsTaskService = smsTaskService;
        this.groupMemberService = groupMemberService;
        this.smsTaskRecipientMapper = smsTaskRecipientMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public PageResult<Briefing> listPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Briefing> list = briefingMapper.selectPage(pageSize, offset);
        int total = briefingMapper.count();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public List<Briefing> listAll() {
        return briefingMapper.selectAll();
    }

    @Override
    public Briefing getById(Long id) {
        return briefingMapper.selectById(id);
    }

    @Override
    @Transactional
    public Briefing create(Briefing briefing) {
        Briefing created = new Briefing(
            null,
            briefing.getTitle(),
            briefing.getContent(),
            briefing.getTemplateId(),
            StringUtils.hasText(briefing.getStatus()) ? briefing.getStatus() : DomainStatus.Briefing.DRAFT,
            StringUtils.hasText(briefing.getChannel()) ? briefing.getChannel() : DomainStatus.Channel.SMS,
            briefing.getAuthor(),
            briefing.getVersion(),
            briefing.getAudience(),
            LocalDateTime.now(),
            briefing.getCreatedBy(),
            LocalDateTime.now(),
            briefing.getDisasterType(),
            briefing.getDisasterLevel(),
            briefing.getContentPart2(),
            briefing.getRemark(),
            briefing.getLegacyPayload()
        );
        briefingMapper.insert(created);
        createTaskForScheduling(created);
        return created;
    }

    @Override
    @Transactional
    public Briefing update(Briefing briefing) {
        Briefing existing = getById(briefing.getId());
        if (existing == null) {
            throw new BusinessException(404, "简讯不存在");
        }
        Briefing updated = new Briefing(briefing.getId(), briefing.getTitle(), briefing.getContent(), briefing.getTemplateId(), briefing.getStatus(), briefing.getChannel(), briefing.getAuthor(), briefing.getVersion(), briefing.getAudience(), LocalDateTime.now(), briefing.getCreatedBy(), existing.getCreatedAt() != null ? existing.getCreatedAt() : LocalDateTime.now(), briefing.getDisasterType(), briefing.getDisasterLevel(), briefing.getContentPart2(), briefing.getRemark(), briefing.getLegacyPayload());
        briefingMapper.update(updated);
        return updated;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return briefingMapper.deleteById(id) > 0;
    }

    @Override
    public List<Briefing> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return listAll();
        }
        return briefingMapper.search(keyword);
    }

    @Override
    public PageResult<Briefing> searchPaged(String keyword, int page, int pageSize) {
        if (!StringUtils.hasText(keyword)) {
            return listPaged(page, pageSize);
        }
        int offset = (page - 1) * pageSize;
        List<Briefing> list = briefingMapper.searchPage(keyword, pageSize, offset);
        int total = briefingMapper.countByKeyword(keyword);
        return PageResult.of(list, total, page, pageSize);
    }

    private void createTaskForScheduling(Briefing briefing) {
        ScheduleConfig config = parseScheduleConfig(briefing.getLegacyPayload());
        List<Long> groupIds = resolveGroupIds(briefing, config);
        if (groupIds.isEmpty()) {
            return;
        }

        List<GroupMember> members = collectDistinctMembers(groupIds);
        if (members.isEmpty()) {
            log.info("Briefing {} has no group members for scheduling, skip task creation", briefing.getTitle());
            return;
        }

        String scheduleType = normalizeScheduleType(config.scheduleType);
        SmsTask task = smsTaskService.create(new SmsTask(
            null,
            briefing.getTitle(),
            briefing.getChannel(),
            resolvePlannedSendTime(config, scheduleType),
            DomainStatus.Task.PENDING,
            members.size(),
            StringUtils.hasText(briefing.getCreatedBy()) ? briefing.getCreatedBy() : briefing.getAuthor(),
            "—",
            null,
            null,
            scheduleType,
            config.recurrenceInterval,
            config.recurrenceUnit,
            parseDateTime(config.recurrenceEndTime),
            0,
            config.recurrenceMaxCount
        ));

        for (GroupMember member : members) {
            smsTaskRecipientMapper.insert(new SmsTaskRecipient(
                null,
                task.getId(),
                member.getContactId(),
                member.getContactMobile(),
                member.getContactName(),
                DomainStatus.Recipient.PENDING,
                null,
                null
            ));
        }

        if ("immediate".equals(scheduleType)) {
            smsTaskService.executeTask(task.getId());
        }
    }

    private List<GroupMember> collectDistinctMembers(List<Long> groupIds) {
        Map<String, GroupMember> members = new LinkedHashMap<>();
        for (Long groupId : groupIds) {
            for (GroupMember member : groupMemberService.getMembersByGroupId(groupId)) {
                if (!StringUtils.hasText(member.getContactMobile())) {
                    continue;
                }
                String key = member.getContactId() != null ? "contact:" + member.getContactId() : "mobile:" + member.getContactMobile();
                members.putIfAbsent(key, member);
            }
        }
        return new ArrayList<>(members.values());
    }

    private List<Long> resolveGroupIds(Briefing briefing, ScheduleConfig config) {
        if (config.groupIds != null && !config.groupIds.isEmpty()) {
            return config.groupIds;
        }
        List<Long> groupIds = new ArrayList<>();
        if (!StringUtils.hasText(briefing.getAudience())) {
            return groupIds;
        }
        for (String item : briefing.getAudience().split(",")) {
            String trimmed = item.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                groupIds.add(Long.parseLong(trimmed));
            } catch (NumberFormatException ignored) {
                log.warn("Skip non-numeric audience entry: {}", trimmed);
            }
        }
        return groupIds;
    }

    private ScheduleConfig parseScheduleConfig(String legacyPayload) {
        if (!StringUtils.hasText(legacyPayload)) {
            return new ScheduleConfig();
        }
        try {
            return objectMapper.readValue(legacyPayload, ScheduleConfig.class);
        } catch (Exception e) {
            throw new BusinessException(400, "调度配置格式非法");
        }
    }

    private String normalizeScheduleType(String scheduleType) {
        return StringUtils.hasText(scheduleType) ? scheduleType.toLowerCase(Locale.ROOT) : "immediate";
    }

    private LocalDateTime resolvePlannedSendTime(ScheduleConfig config, String scheduleType) {
        if ("immediate".equals(scheduleType)) {
            return LocalDateTime.now();
        }
        LocalDateTime planned = parseDateTime(config.scheduledTime);
        return planned != null ? planned : LocalDateTime.now();
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return LocalDateTime.parse(value);
    }

    private static class ScheduleConfig {
        public String scheduleType;
        public String scheduledTime;
        public Integer recurrenceInterval;
        public String recurrenceUnit;
        public String recurrenceEndTime;
        public Integer recurrenceMaxCount;
        public List<Long> groupIds;
    }
}
