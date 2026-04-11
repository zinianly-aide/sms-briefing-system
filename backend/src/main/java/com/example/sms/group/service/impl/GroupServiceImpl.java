package com.example.sms.group.service.impl;

import com.example.sms.common.dto.PageResult;
import com.example.sms.common.exception.BusinessException;
import com.example.sms.group.entity.ContactGroup;
import com.example.sms.group.mapper.GroupMapper;
import com.example.sms.group.service.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    private final GroupMapper groupMapper;

    public GroupServiceImpl(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    @Override
    public PageResult<ContactGroup> listPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ContactGroup> list = groupMapper.selectPage(pageSize, offset);
        int total = groupMapper.count();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public List<ContactGroup> listAll() {
        return groupMapper.selectAll();
    }

    @Override
    public ContactGroup getById(Long id) {
        return groupMapper.selectById(id);
    }

    @Override
    @Transactional
    public ContactGroup create(ContactGroup group) {
        LocalDateTime now = LocalDateTime.now();
        ContactGroup created = new ContactGroup(null, group.getName(), group.getOwnerDept(), group.getMemberCount() == null ? 0 : group.getMemberCount(), group.getTags(), group.getLastSyncTime(), group.getStatus(), now, now);
        groupMapper.insert(created);
        return created;
    }

    @Override
    @Transactional
    public ContactGroup update(ContactGroup group) {
        ContactGroup existing = getById(group.getId());
        if (existing == null) {
            throw new BusinessException(404, "群组不存在");
        }
        ContactGroup updated = new ContactGroup(group.getId(), group.getName(), group.getOwnerDept(), group.getMemberCount(), group.getTags(), group.getLastSyncTime(), group.getStatus(), existing.getCreatedAt(), LocalDateTime.now());
        groupMapper.update(updated);
        return updated;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return groupMapper.deleteById(id) > 0;
    }

    @Override
    public List<ContactGroup> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return listAll();
        }
        return groupMapper.search(keyword);
    }

    @Override
    public PageResult<ContactGroup> searchPaged(String keyword, int page, int pageSize) {
        if (!StringUtils.hasText(keyword)) {
            return listPaged(page, pageSize);
        }
        int offset = (page - 1) * pageSize;
        List<ContactGroup> list = groupMapper.searchPage(keyword, pageSize, offset);
        int total = groupMapper.countByKeyword(keyword);
        return PageResult.of(list, total, page, pageSize);
    }
}
