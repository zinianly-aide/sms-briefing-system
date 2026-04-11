package com.example.sms.audit.aspect;

import com.example.sms.audit.entity.OperationLog;
import com.example.sms.audit.mapper.OperationLogMapper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    private final OperationLogMapper logMapper;

    public AuditLogAspect(OperationLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerMethods() {}

    @Pointcut("execution(* com.example.sms..controller.*.*(..)) && " +
              "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMethods() {}

    @Pointcut("execution(* com.example.sms..controller.*.*(..)) && " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMethods() {}

    @Pointcut("execution(* com.example.sms..controller.*.*(..)) && " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMethods() {}

    @AfterReturning("postMethods()")
    public void logPost() {
        saveLog("POST");
    }

    @AfterReturning("putMethods()")
    public void logPut() {
        saveLog("PUT");
    }

    @AfterReturning("deleteMethods()")
    public void logDelete() {
        saveLog("DELETE");
    }

    private void saveLog(String action) {
        try {
            String ip = "unknown";
            String path = "unknown";
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                ip = request.getRemoteAddr();
                path = request.getRequestURI();
            }

            // Determine module from path
            String module = "unknown";
            if (path.contains("/contacts")) module = "联系人";
            else if (path.contains("/groups")) module = "群组";
            else if (path.contains("/templates")) module = "模板";
            else if (path.contains("/briefings")) module = "简讯";
            else if (path.contains("/tasks")) module = "发送任务";
            else if (path.contains("/configs")) module = "系统配置";
            else if (path.contains("/hr")) module = "HR同步";
            else if (path.contains("/logs")) module = "审计日志";

            OperationLog opLog = new OperationLog(null, module, action, "系统用户", path, ip, LocalDateTime.now());
            logMapper.insert(opLog);
        } catch (Exception e) {
            log.warn("Failed to save audit log: {}", e.getMessage());
        }
    }
}
