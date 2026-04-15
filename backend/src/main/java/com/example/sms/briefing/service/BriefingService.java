package com.example.sms.briefing.service;

import com.example.sms.common.dto.PageResult;
import com.example.sms.briefing.entity.Briefing;

import java.util.List;

public interface BriefingService {
    PageResult<Briefing> listPaged(int page, int pageSize);

    List<Briefing> listAll();

    Briefing getById(Long id);

    Briefing create(Briefing briefing);

    Briefing update(Briefing briefing);

    boolean delete(Long id);

    List<Briefing> search(String keyword);

    PageResult<Briefing> searchPaged(String keyword, int page, int pageSize);
}
