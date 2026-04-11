package com.example.sms.smstask.service;

public enum SmsErrorCode {
    SUCCESS("0000", "发送成功"),
    TIMEOUT("1001", "网关超时"),
    INVALID_NUMBER("1002", "号码无效"),
    INSUFFICIENT_BALANCE("1003", "余额不足"),
    GATEWAY_ERROR("1004", "网关异常"),
    CONTENT_TOO_LONG("1005", "内容超长"),
    FREQUENCY_LIMIT("1006", "频率超限"),
    UNKNOWN("9999", "未知错误");

    private final String code;
    private final String message;

    SmsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
