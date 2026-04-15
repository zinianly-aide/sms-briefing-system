package com.example.sms.common.constant;

import com.example.sms.common.exception.BusinessException;
import org.springframework.util.StringUtils;

import java.util.Set;

public final class DomainValueValidator {

    private DomainValueValidator() {}

    public static void validateContactStatus(String status) {
        validateOptionalValue("联系人状态", status, Set.of(
            DomainStatus.Contact.ACTIVE,
            DomainStatus.Contact.INACTIVE
        ));
    }

    public static void validateGroupStatus(String status) {
        validateOptionalValue("群组状态", status, Set.of(
            DomainStatus.Group.ENABLED,
            DomainStatus.Group.DISABLED
        ));
    }

    public static void validateTemplateStatus(String status) {
        validateOptionalValue("模板状态", status, Set.of(
            DomainStatus.Template.ACTIVE,
            DomainStatus.Template.DRAFT
        ));
    }

    public static void validateBriefingStatus(String status) {
        validateOptionalValue("简讯状态", status, Set.of(
            DomainStatus.Briefing.DRAFT,
            DomainStatus.Briefing.PENDING_REVIEW,
            DomainStatus.Briefing.PENDING_SEND,
            DomainStatus.Briefing.SENT
        ));
    }

    public static void validateTaskStatus(String status) {
        validateOptionalValue("任务状态", status, Set.of(
            DomainStatus.Task.DRAFT,
            DomainStatus.Task.PENDING,
            DomainStatus.Task.SENDING,
            DomainStatus.Task.COMPLETED,
            DomainStatus.Task.PARTIAL_SUCCESS,
            DomainStatus.Task.FAILED,
            DomainStatus.Task.CANCELLED
        ));
    }

    public static void validateChannel(String channel) {
        validateOptionalValue("发送渠道", channel, Set.of(
            DomainStatus.Channel.SMS,
            DomainStatus.Channel.SMS_WECOM
        ));
    }

    private static void validateOptionalValue(String fieldName, String value, Set<String> allowedValues) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        if (!allowedValues.contains(value)) {
            throw new BusinessException(400, fieldName + "非法: " + value);
        }
    }
}
