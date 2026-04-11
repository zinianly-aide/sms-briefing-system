package com.example.sms.config.service.impl;

import com.example.sms.config.entity.SystemConfig;
import com.example.sms.config.mapper.SystemConfigMapper;
import com.example.sms.config.service.SystemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigMapper configMapper;

    public SystemConfigServiceImpl(SystemConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    @Override
    public List<SystemConfig> listAll() {
        return configMapper.selectAll();
    }

    @Override
    public SystemConfig getById(Long id) {
        return configMapper.selectById(id);
    }

    @Override
    public SystemConfig getByKey(String key) {
        return configMapper.selectByKey(key);
    }

    @Override
    public String getValue(String key) {
        SystemConfig config = configMapper.selectByKey(key);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional
    public SystemConfig create(SystemConfig config) {
        config.setUpdatedAt(LocalDateTime.now());
        configMapper.insert(config);
        return config;
    }

    @Override
    @Transactional
    public SystemConfig update(SystemConfig config) {
        SystemConfig existing = configMapper.selectById(config.getId());
        if (existing == null) {
            throw new RuntimeException("配置项不存在");
        }
        config.setUpdatedAt(LocalDateTime.now());
        configMapper.update(config);
        return config;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return configMapper.deleteById(id) > 0;
    }
}
