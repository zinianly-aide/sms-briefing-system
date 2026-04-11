package com.example.sms.briefing.service.impl;

import com.example.sms.briefing.entity.Briefing;
import com.example.sms.briefing.mapper.BriefingMapper;
import com.example.sms.briefing.service.BriefingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BriefingServiceImpl implements BriefingService {
    private final BriefingMapper briefingMapper;

    public BriefingServiceImpl(BriefingMapper briefingMapper) {
        this.briefingMapper = briefingMapper;
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
        Briefing created = new Briefing(null, briefing.getTitle(), briefing.getContent(), briefing.getTemplateId(), briefing.getStatus(), briefing.getChannel(), briefing.getAuthor(), briefing.getVersion(), briefing.getAudience(), LocalDateTime.now(), briefing.getCreatedBy(), LocalDateTime.now(), briefing.getDisasterType(), briefing.getDisasterLevel(), briefing.getContentPart2(), briefing.getRemark(), briefing.getLegacyPayload());
        briefingMapper.insert(created);
        return created;
    }

    @Override
    @Transactional
    public Briefing update(Briefing briefing) {
        Briefing existing = getById(briefing.getId());
        if (existing == null) {
            throw new RuntimeException("简讯不存在");
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
}
