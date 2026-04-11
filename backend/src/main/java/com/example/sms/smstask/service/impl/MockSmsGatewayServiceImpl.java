package com.example.sms.smstask.service.impl;

import com.example.sms.smstask.service.SmsGatewayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MockSmsGatewayServiceImpl implements SmsGatewayService {

    @Value("${sms.mock.success-rate:85}")
    private int successRate;

    @Override
    public List<SendResult> send(List<String> mobiles, String content) {
        List<SendResult> results = new ArrayList<>();
        for (String mobile : mobiles) {
            boolean success = ThreadLocalRandom.current().nextInt(100) < successRate;
            // Simulate small delay
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 50));
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            results.add(new SendResult(mobile, success, success ? null : "模拟发送失败: 网络超时"));
        }
        return results;
    }
}
