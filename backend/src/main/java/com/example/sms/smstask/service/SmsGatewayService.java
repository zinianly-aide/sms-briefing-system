package com.example.sms.smstask.service;

import java.util.List;

public interface SmsGatewayService {

    /**
     * 发送短信结果
     */
    record SendResult(String mobile, boolean success, String errorMsg) {}

    /**
     * 发送短信列表，返回每条结果
     */
    List<SendResult> send(List<String> mobiles, String content);
}
