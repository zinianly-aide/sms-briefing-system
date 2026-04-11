package com.example.sms.audit.service.impl;

import com.example.sms.audit.entity.OperationLog;
import com.example.sms.audit.mapper.OperationLogMapper;
import com.example.sms.audit.service.AuditLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final OperationLogMapper logMapper;

    public AuditLogServiceImpl(OperationLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Override
    public List<OperationLog> listAll() {
        return logMapper.selectAll();
    }

    @Override
    public List<OperationLog> getByModule(String module) {
        return logMapper.selectByModule(module);
    }

    @Override
    public List<OperationLog> getByOperator(String operator) {
        return logMapper.selectByOperator(operator);
    }
}
