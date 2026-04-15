package com.example.sms.auth.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.common.security.JwtUtil;
import com.example.sms.auth.entity.User;
import com.example.sms.auth.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Simple in-memory rate limiter: IP -> timestamp of last request
    private final ConcurrentHashMap<String, Long> registrationAttempts = new ConcurrentHashMap<>();
    private static final int MAX_REGISTRATIONS_PER_MINUTE = 5;

    public AuthController(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (!org.springframework.util.StringUtils.hasText(username) || !org.springframework.util.StringUtils.hasText(password)) {
            return ApiResponse.error(400, "用户名和密码不能为空");
        }
        User user = userMapper.selectByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ApiResponse.error(401, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(username, user.getRole());
        return ApiResponse.success(Map.of(
            "token", token,
            "username", username,
            "displayName", user.getDisplayName() != null ? user.getDisplayName() : username,
            "role", user.getRole() != null ? user.getRole() : "user"
        ));
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody Map<String, String> body,
                                        @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                                        jakarta.servlet.http.HttpServletRequest request) {
        String username = body.get("username");
        String password = body.get("password");
        String displayName = body.get("displayName");

        // Rate limiting
        String clientIp = forwardedFor != null ? forwardedFor.split(",")[0].trim() : request.getRemoteAddr();
        Long lastAttempt = registrationAttempts.get(clientIp);
        long now = System.currentTimeMillis();
        if (lastAttempt != null && now - lastAttempt < 60000) {
            Long count = registrationAttempts.get(clientIp + ":count");
            if (count != null && count >= MAX_REGISTRATIONS_PER_MINUTE) {
                return ApiResponse.error(429, "注册请求过于频繁，请稍后再试");
            }
        } else {
            registrationAttempts.put(clientIp, now);
            registrationAttempts.put(clientIp + ":count", 1L);
        }
        registrationAttempts.put(clientIp + ":count", registrationAttempts.getOrDefault(clientIp + ":count", 0L) + 1);

        if (!org.springframework.util.StringUtils.hasText(username) || !org.springframework.util.StringUtils.hasText(password)) {
            return ApiResponse.error(400, "用户名和密码不能为空");
        }
        if (username.length() < 3 || username.length() > 32) {
            return ApiResponse.error(400, "用户名长度需为3-32位");
        }
        if (password.length() < 8) {
            return ApiResponse.error(400, "密码至少8位");
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            return ApiResponse.error(400, "密码需包含字母和数字");
        }
        User existing = userMapper.selectByUsername(username);
        if (existing != null) {
            return ApiResponse.error(409, "用户名已存在");
        }
        User user = new User(null, username, passwordEncoder.encode(password),
            org.springframework.util.StringUtils.hasText(displayName) ? displayName : username,
            "user", LocalDateTime.now());
        userMapper.insert(user);
        return ApiResponse.success("注册成功");
    }
}
