package com.example.sms.auth.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.common.security.JwtUtil;
import com.example.sms.auth.entity.User;
import com.example.sms.auth.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
        String token = jwtUtil.generateToken(username);
        return ApiResponse.success(Map.of(
            "token", token,
            "username", username,
            "displayName", user.getDisplayName() != null ? user.getDisplayName() : username,
            "role", user.getRole() != null ? user.getRole() : "user"
        ));
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String displayName = body.get("displayName");

        if (!org.springframework.util.StringUtils.hasText(username) || !org.springframework.util.StringUtils.hasText(password)) {
            return ApiResponse.error(400, "用户名和密码不能为空");
        }
        if (password.length() < 4) {
            return ApiResponse.error(400, "密码至少4位");
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
